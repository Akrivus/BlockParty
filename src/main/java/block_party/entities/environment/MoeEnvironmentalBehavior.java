package block_party.entities.environment;

import block_party.entities.Moe;
import block_party.entities.social.MoeSocialRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class MoeEnvironmentalBehavior {
    private final Moe moe;
    private int movementTicks;
    private Vec3 movementDestination;
    private double movementSpeed;

    public MoeEnvironmentalBehavior(Moe moe) {
        this.moe = moe;
    }

    public boolean canUseGoal() {
        if (this.moe.shouldSkipGoalMovement() || this.moe.isFollowing()) {
            this.clearMovementIntent();
            return false;
        }
        return this.hasMovementIntent() || this.routineDestination() != null;
    }

    public boolean canContinueGoal() {
        return this.hasMovementIntent()
                && !this.moe.shouldSkipGoalMovement()
                && !this.moe.isFollowing();
    }

    public void tickGoal() {
        if (this.hasMovementIntent()) {
            this.updateMovementIntent();
            return;
        }
        this.updateRoutineMovement();
    }

    public void stopGoal() {
        if (this.moe.shouldSkipGoalMovement() || this.moe.isFollowing()
                || (!this.shouldSeekRainShelter() && !this.shouldSeekLight())) {
            this.clearMovementIntent();
        }
    }

    public boolean updateRoutineMovement() {
        boolean seekingRainShelter = this.shouldSeekRainShelter();
        boolean seekingLight = this.shouldSeekLight();
        Vec3 destination = this.routineDestination();
        if (destination == null || this.moe.position().distanceToSqr(destination) <= 1.44D) {
            this.clearMovementIntent();
            return false;
        }
        boolean moved = this.setMovementDestination(destination, this.moveSpeed());
        if (moved) {
            this.moe.setTemporaryAnimationKey(seekingRainShelter ? "SHIVER" : seekingLight ? "LOOK_AROUND" : "DEFAULT", 36);
        }
        return moved;
    }

    public Vec3 routineDestination() {
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger()) {
            return null;
        }
        BlockPos origin = this.moe.blockPosition();
        if (this.shouldSeekRainShelter()) {
            Vec3 remembered = this.rememberedPlaceDestination(MoePlaceMemory.PlaceType.HOUSE, MoePlaceMemory.PlaceType.SHELTER, MoePlaceMemory.PlaceType.WORKSHOP);
            if (remembered != null) {
                return remembered;
            }
            Vec3 shelter = MoeEnvironmentalRules.bestShelter(this.moe.level(), origin, MoeEnvironmentalRules.WEATHER_RADIUS)
                    .map(Vec3::atBottomCenterOf)
                    .orElse(null);
            if (shelter != null) {
                return shelter;
            }
        }
        if (this.shouldSeekLight()) {
            Vec3 remembered = this.rememberedPlaceDestination(MoePlaceMemory.PlaceType.HOUSE, MoePlaceMemory.PlaceType.WORKSHOP);
            if (remembered != null) {
                return remembered;
            }
            return MoeEnvironmentalRules.bestLight(this.moe.level(), origin, MoeEnvironmentalRules.LIGHT_RADIUS)
                    .map(Vec3::atBottomCenterOf)
                    .orElse(null);
        }
        return null;
    }

    public boolean shouldSeekRainShelter() {
        return !this.moe.ignoresRain()
                && this.moe.level().isRaining()
                && !MoeEnvironmentalRules.isStrongShelter(this.moe.level(), this.moe.blockPosition());
    }

    public boolean shouldSeekLight() {
        if (this.moe.ignoresDarkness()) {
            return false;
        }
        BlockPos feet = this.moe.blockPosition();
        BlockPos head = feet.above();
        int blockLight = MoeEnvironmentalRules.blockLight(this.moe.level(), feet);
        boolean locallyDark = blockLight < 8;
        boolean skyIsNotEnough = this.moe.level().isNight() || this.isNightByTime() || !this.moe.level().canSeeSky(head);
        return locallyDark && skyIsNotEnough;
    }

    public boolean hasMovementIntent() {
        return this.movementTicks > 0 && this.movementDestination != null;
    }

    public boolean updateMovementIntent() {
        if (this.movementTicks <= 0 || this.movementDestination == null) {
            return false;
        }
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger() || this.moe.hasDialogue()) {
            this.clearMovementIntent();
            return false;
        }
        if (!this.shouldSeekRainShelter() && !this.shouldSeekLight()) {
            this.clearMovementIntent();
            return false;
        }
        if (this.moe.position().distanceToSqr(this.movementDestination) <= 1.0D) {
            this.clearMovementIntent();
            return false;
        }
        --this.movementTicks;
        boolean moving = this.moveToDestination(this.movementDestination, this.movementSpeed);
        if (!moving) {
            this.clearMovementIntent();
        }
        return moving;
    }

    public void clearMovementIntent() {
        this.movementTicks = 0;
        this.movementDestination = null;
        this.movementSpeed = 0.0D;
    }

    private Vec3 rememberedPlaceDestination(MoePlaceMemory.PlaceType... types) {
        MoePlaceMemory.Place remembered = this.moe.rememberedPlace().orElse(MoePlaceMemory.Place.none());
        if (!MoePlaceMemory.stillValid(this.moe, remembered)) {
            this.moe.clearRememberedPlace();
            return null;
        }
        for (MoePlaceMemory.PlaceType type : types) {
            if (remembered.type() == type) {
                return Vec3.atBottomCenterOf(remembered.pos());
            }
        }
        return null;
    }

    private boolean isNightByTime() {
        long dayTime = Math.floorMod(this.moe.level().getDayTime(), 24000L);
        return dayTime >= 13000L && dayTime <= 23000L;
    }

    private double moveSpeed() {
        return 0.7D + Math.min(0.35D, MoeSocialRules.socialMoveSpeed(this.moe.getDere()) * 0.25D);
    }

    private int movementDuration() {
        return Math.max(36, MoeSocialRules.socialMovementDuration(this.moe.getDere()) + 12);
    }

    private boolean setMovementDestination(Vec3 destination, double speed) {
        this.movementDestination = destination;
        this.movementSpeed = speed;
        this.movementTicks = this.movementDuration();
        return this.moveToDestination(destination, speed);
    }

    private boolean moveToDestination(Vec3 destination, double speed) {
        boolean navigating = this.moe.getNavigation().moveTo(destination.x, destination.y, destination.z, speed);
        if (!navigating) {
            this.moe.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
        }
        return true;
    }
}
