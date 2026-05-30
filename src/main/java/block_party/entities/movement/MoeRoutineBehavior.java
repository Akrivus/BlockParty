package block_party.entities.movement;

import block_party.blocks.GardenLanternBlock;
import block_party.entities.Moe;
import block_party.entities.environment.MoeEnvironmentalRules;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.goals.HideUntil;
import block_party.entities.social.MoeSocialRules;
import block_party.registry.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class MoeRoutineBehavior {
    private static final double IDLE_ANCHOR_RADIUS = 24.0D;
    private static final double GARDEN_LANTERN_CHORE_RADIUS = 16.0D;

    private final Moe moe;

    public MoeRoutineBehavior(Moe moe) {
        this.moe = moe;
    }

    public boolean canUseGoal() {
        return !this.moe.shouldSkipGoalMovement()
                && !this.moe.isFollowing()
                && (this.moe.getEffectiveRoutineIntent() == RoutineIntent.SLEEP || this.destination() != null);
    }

    public boolean updateMovement() {
        if (this.moe.getEffectiveRoutineIntent() == RoutineIntent.SLEEP && this.moe.sleepAtHome(HideUntil.EXPOSED)) {
            return true;
        }
        if (this.lightNearbyGardenLantern()) {
            return true;
        }
        Vec3 destination = this.destination();
        if (destination == null || this.moe.position().distanceToSqr(destination) <= 1.44D) {
            return false;
        }
        this.moe.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, this.moveSpeed());
        this.applyWellbeing();
        return true;
    }

    public Vec3 destination() {
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger()) {
            return null;
        }
        Vec3 lanternDestination = this.unlitGardenLanternDestination();
        if (lanternDestination != null) {
            return lanternDestination;
        }
        Vec3 socialPlaceDestination = this.moe.social().placeDestination();
        if (socialPlaceDestination != null) {
            return socialPlaceDestination;
        }
        Vec3 socialDestination = this.moe.social().idleOrbitDestination();
        if (socialDestination != null) {
            return socialDestination;
        }
        Vec3 rememberedDestination = this.rememberedIdlePlaceDestination();
        if (rememberedDestination != null) {
            return rememberedDestination;
        }
        return this.idleAnchorDestination();
    }

    public boolean lightNearbyGardenLantern() {
        if (!(this.moe.level() instanceof ServerLevel level) || this.moe.shouldSkipGoalMovement()) {
            return false;
        }
        BlockPos lantern = this.nearestUnlitGardenLantern(2.25D).orElse(null);
        if (lantern == null) {
            return false;
        }
        BlockState state = level.getBlockState(lantern);
        level.setBlock(lantern, state.setValue(GardenLanternBlock.LIT, true), 3);
        this.moe.rememberPlace(new MoePlaceMemory.Place(
                MoePlaceMemory.PlaceType.GARDEN,
                lantern.immutable(),
                132.0D,
                0,
                6,
                MoeEnvironmentalRules.shelterScore(level, lantern),
                new MoePlaceMemory.Features(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, MoeAnchorType.GARDEN, 0.0D, 70)));
        this.moe.addRelaxation(0.08F);
        this.moe.setTemporaryAnimationKey("AWE", 40);
        this.moe.getLookControl().setLookAt(lantern.getX() + 0.5D, lantern.getY() + 0.5D, lantern.getZ() + 0.5D, 30.0F, 30.0F);
        return true;
    }

    public Optional<BlockPos> nearestUnlitGardenLantern(double radius) {
        if (!(this.moe.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }
        double radiusSqr = radius * radius;
        BlockPos origin = this.moe.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        int blockRadius = (int) Math.ceil(radius);
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-blockRadius, -3, -blockRadius), origin.offset(blockRadius, 3, blockRadius))) {
            BlockPos immutable = pos.immutable();
            double distance = immutable.distSqr(origin);
            if (distance > radiusSqr || distance >= bestDistance) {
                continue;
            }
            BlockState state = level.getBlockState(immutable);
            if (state.is(CustomTags.PLACE_GARDEN_LANTERNS)
                    && state.hasProperty(GardenLanternBlock.LIT)
                    && !state.getValue(GardenLanternBlock.LIT)
                    && this.isLanternInGardenContext(immutable)) {
                best = immutable;
                bestDistance = distance;
            }
        }
        return Optional.ofNullable(best);
    }

    private Vec3 unlitGardenLanternDestination() {
        if (this.moe.getEffectiveRoutineIntent() == RoutineIntent.SLEEP) {
            return null;
        }
        BlockPos lantern = this.nearestUnlitGardenLantern(GARDEN_LANTERN_CHORE_RADIUS).orElse(null);
        if (lantern == null) {
            return null;
        }
        return this.bestLanternStandingPosition(lantern)
                .map(Vec3::atBottomCenterOf)
                .orElse(Vec3.atBottomCenterOf(lantern));
    }

    private boolean isLanternInGardenContext(BlockPos lantern) {
        MoePlaceMemory.Place remembered = this.moe.rememberedPlace().orElse(MoePlaceMemory.Place.none());
        if (remembered.type() == MoePlaceMemory.PlaceType.GARDEN && remembered.pos().distSqr(lantern) <= 8.0D * 8.0D) {
            return true;
        }
        return MoePlaceMemory.evaluate(this.moe, lantern).type() == MoePlaceMemory.PlaceType.GARDEN
                || MoeAnchorResolver.nearbyRoutineAnchor(this.moe, GARDEN_LANTERN_CHORE_RADIUS, RoutineIntent.RELAX)
                .map(anchor -> anchor.type() == MoeAnchorType.GARDEN && anchor.dimPos().getPos().distSqr(lantern) <= 8.0D * 8.0D)
                .orElse(false);
    }

    private Optional<BlockPos> bestLanternStandingPosition(BlockPos lantern) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(lantern.offset(-1, -1, -1), lantern.offset(1, 1, 1))) {
            BlockPos immutable = candidate.immutable();
            if (immutable.equals(lantern) || !MoeEnvironmentalRules.canStandAt(this.moe.level(), immutable)) {
                continue;
            }
            double distance = immutable.distSqr(this.moe.blockPosition());
            if (distance < bestDistance) {
                best = immutable;
                bestDistance = distance;
            }
        }
        return Optional.ofNullable(best);
    }

    private Vec3 rememberedIdlePlaceDestination() {
        if (this.moe.getEffectiveRoutineIntent() != RoutineIntent.IDLE && this.moe.getEffectiveRoutineIntent() != RoutineIntent.RELAX) {
            return null;
        }
        MoePlaceMemory.Place remembered = this.moe.rememberedPlace().orElse(MoePlaceMemory.Place.none());
        if (!MoePlaceMemory.stillValid(this.moe, remembered)) {
            this.moe.clearRememberedPlace();
            return null;
        }
        if (this.moe.currentRoutineAnchor().map(anchor -> anchor.type() != MoeAnchorType.HOME).orElse(false)) {
            return null;
        }
        return switch (remembered.type()) {
            case HOUSE, GARDEN, GROVE, FIELD, WORKSHOP, WATERFRONT, CAVE, SHRINE, FARM -> Vec3.atBottomCenterOf(remembered.pos());
            case SHELTER, NONE -> null;
        };
    }

    private Vec3 idleAnchorDestination() {
        return MoeAnchorResolver.nearbyRoutineAnchor(this.moe, IDLE_ANCHOR_RADIUS)
                .map(anchor -> {
                    Vec3 center = Vec3.atBottomCenterOf(anchor.dimPos().getPos());
                    double distanceSqr = this.moe.position().distanceToSqr(center);
                    if (distanceSqr > 6.0D * 6.0D) {
                        return center;
                    }
                    return this.orbitDestination(center, 2.75D, 1.25D);
                })
                .orElse(null);
    }

    private Vec3 orbitDestination(Vec3 center, double radius, double tangentStep) {
        Vec3 flat = new Vec3(this.moe.getX() - center.x, 0.0D, this.moe.getZ() - center.z);
        if (flat.lengthSqr() < 0.0001D) {
            double angle = Math.floorMod(this.moe.getUUID().getLeastSignificantBits(), 6283L) / 1000.0D;
            flat = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
        }
        Vec3 radial = flat.normalize();
        Vec3 tangent = new Vec3(-radial.z, 0.0D, radial.x);
        return new Vec3(center.x, this.moe.getY(), center.z)
                .add(radial.scale(radius))
                .add(tangent.scale(tangentStep));
    }

    private double moveSpeed() {
        return 0.55D + Math.min(0.35D, MoeSocialRules.socialMoveSpeed(this.moe.getDere()) * 0.2D);
    }

    private void applyWellbeing() {
        if (this.moe.getEffectiveRoutineIntent() == RoutineIntent.RELAX
                && this.moe.currentRoutineAnchor().map(anchor -> anchor.type() == MoeAnchorType.GARDEN).orElse(false)) {
            this.moe.addRelaxation(0.02F);
            if (this.moe.getRelaxation() >= 1.0F && this.moe.getStress() > 0.0F) {
                this.moe.addStress(-0.01F);
            }
        }
    }
}
