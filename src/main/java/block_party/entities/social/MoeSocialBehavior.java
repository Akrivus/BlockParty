package block_party.entities.social;

import block_party.entities.Moe;
import block_party.entities.environment.MoePlaceMemory;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public final class MoeSocialBehavior {
    private static final double IDLE_SOCIAL_RADIUS = 10.0D;

    private final Moe moe;
    private int tickDelay;
    private int movementTicks;
    private Vec3 movementDestination;
    private double movementSpeed;

    public MoeSocialBehavior(Moe moe) {
        this.moe = moe;
    }

    public Vec3 idleOrbitDestination() {
        List<Moe> nearby = MoeSocialContext.nearby(this.moe, IDLE_SOCIAL_RADIUS).stream()
                .filter(other -> !other.isFollowing() && !other.isSitting() && !other.isPassenger())
                .toList();
        if (nearby.isEmpty()) {
            return null;
        }
        Vec3 center = this.moe.position();
        for (Moe other : nearby) {
            center = center.add(other.position());
        }
        center = center.scale(1.0D / (nearby.size() + 1));
        double radius = Math.min(4.5D, 2.25D + nearby.size() * 0.35D);
        return this.orbitDestination(center, radius, 1.75D);
    }

    public Vec3 placeDestination() {
        SocialPlaceCandidate candidate = this.bestSocialPlaceCandidate();
        if (candidate == null || candidate.behavior() == MoeSocialRules.SocialPlaceBehavior.IGNORE) {
            return null;
        }
        Vec3 center = Vec3.atBottomCenterOf(candidate.place().pos());
        double distanceSqr = this.moe.position().distanceToSqr(center);
        return switch (candidate.behavior()) {
            case SHARE -> distanceSqr > 5.0D * 5.0D
                    ? center
                    : this.orbitDestination(center, Math.max(2.0D, 1.6D + candidate.place().occupancy() * 0.35D), 1.0D);
            case ORBIT -> this.orbitDestination(center, Math.min(5.0D, 2.75D + candidate.place().occupancy() * 0.45D), 1.8D);
            case GUARD -> this.guardDestination(center, candidate.owner().position());
            case AVOID -> this.avoidPlaceDestination(center, 4.5D);
            case IGNORE -> null;
        };
    }

    public Optional<MoeSocialPlaceMemory> placeMemoryForTests() {
        SocialPlaceCandidate candidate = this.bestSocialPlaceCandidate();
        return candidate == null ? Optional.empty() : Optional.of(candidate.memory());
    }

    public boolean canUseGoal() {
        if (this.moe.shouldSkipGoalMovement() || this.moe.isFollowing()) {
            this.clearMovementIntent();
            return false;
        }
        if (this.moe.environment().hasMovementIntent() || this.moe.environment().routineDestination() != null) {
            return false;
        }
        if (this.hasMovementIntent()) {
            return true;
        }
        if (this.hasTickDelay()) {
            this.decrementTickDelay();
            return false;
        }
        return MoeSocialContext.find(this.moe, IDLE_SOCIAL_RADIUS - 2.0D).isPresent();
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
        this.resetTickDelay();
        boolean moved = this.updateState();
        if (moved) {
            this.moe.environment().clearMovementIntent();
        }
    }

    public void stopGoal() {
        if (this.moe.shouldSkipGoalMovement() || this.moe.isFollowing()) {
            this.clearMovementIntent();
        }
    }

    public boolean updateState() {
        MoeSocialContext context = MoeSocialContext.find(this.moe, IDLE_SOCIAL_RADIUS - 2.0D).orElse(null);
        if (context == null) {
            return false;
        }
        Moe socialTarget = context.target();
        MoeSocialRules.SocialSignal strongest = context.signal();
        this.moe.getLookControl().setLookAt(socialTarget, 30.0F, 30.0F);
        MoeSocialRules.SocialVisual visual = context.visual();
        MoeSocialRules.DereReaction reaction = context.reaction();
        boolean moved = this.updateBloodTypeMovement(socialTarget, strongest);
        moved = this.updateDereReaction(socialTarget, reaction) || moved;
        this.updateAnimation(strongest, visual, reaction);
        boolean tense = strongest.tension() > strongest.affinity();
        if (strongest.affinity() >= 0.6F || strongest.tension() >= 0.35F || strongest.interest() >= 0.5F) {
            this.moe.setEmotion(MoeSocialRules.responseEmotion(this.moe.getDere(), strongest, reaction, socialTarget.getEmotion()));
        }
        if (strongest.affinity() >= 0.6F) {
            this.moe.addRelaxation(0.05F);
        } else if (tense) {
            this.moe.addStress(0.05F);
        }
        this.applyDereFeeling(reaction);
        return moved;
    }

    public boolean updateMovementIntent() {
        if (this.movementTicks <= 0 || this.movementDestination == null) {
            return false;
        }
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger() || this.moe.hasDialogue()) {
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

    public boolean hasMovementIntent() {
        return this.movementTicks > 0 && this.movementDestination != null;
    }

    public boolean hasTickDelay() {
        return this.tickDelay > 0;
    }

    public void decrementTickDelay() {
        if (this.tickDelay > 0) {
            --this.tickDelay;
        }
    }

    public void resetTickDelay() {
        this.tickDelay = MoeSocialRules.socialTickDelay(this.moe.getDere(), this.moe.getRandom().nextInt());
    }

    private SocialPlaceCandidate bestSocialPlaceCandidate() {
        List<Moe> nearby = MoeSocialContext.nearby(this.moe, IDLE_SOCIAL_RADIUS + MoePlaceMemory.PLACE_RADIUS).stream()
                .filter(other -> other != this.moe && !other.isRemoved() && other.isAlive())
                .filter(other -> !other.isFollowing() && !other.isSitting() && !other.isPassenger())
                .toList();
        SocialPlaceCandidate best = null;
        for (Moe other : nearby) {
            MoePlaceMemory.Place place = other.rememberedPlace().orElse(null);
            if (place == null || !MoePlaceMemory.stillValid(other, place) || place.overcrowded()) {
                continue;
            }
            if (!isSocialPlaceType(place.type())) {
                continue;
            }
            MoeSocialRules.SocialSignal signal = MoeSocialContext.signal(this.moe, other);
            MoeSocialRules.SocialPlaceBehavior behavior = MoeSocialRules.placeBehavior(this.moe.getDere(), this.moe.getBloodType(), signal, place.occupancy(), place.capacity());
            if (behavior == MoeSocialRules.SocialPlaceBehavior.IGNORE) {
                continue;
            }
            double score = socialPlaceScore(this.moe, other, place, signal, behavior);
            SocialPlaceCandidate candidate = new SocialPlaceCandidate(
                    other,
                    place,
                    behavior,
                    new MoeSocialPlaceMemory(other.getUUID(), other.getGivenName(), place.type(), place.pos(), behavior, signal, score));
            if (best == null || candidate.memory().score() > best.memory().score()) {
                best = candidate;
            }
        }
        return best;
    }

    private static boolean isSocialPlaceType(MoePlaceMemory.PlaceType type) {
        return switch (type) {
            case GARDEN, GROVE, FIELD, WORKSHOP, WATERFRONT, SHRINE, FARM -> true;
            case HOUSE, SHELTER, CAVE, NONE -> false;
        };
    }

    private static double socialPlaceScore(Moe observer, Moe owner, MoePlaceMemory.Place place, MoeSocialRules.SocialSignal signal, MoeSocialRules.SocialPlaceBehavior behavior) {
        double placeDistance = Math.sqrt(observer.blockPosition().distSqr(place.pos()));
        double ownerDistance = Math.sqrt(observer.distanceToSqr(owner));
        double behaviorWeight = switch (behavior) {
            case SHARE -> 18.0D;
            case ORBIT -> 13.0D;
            case GUARD -> 10.0D;
            case AVOID -> 8.0D;
            case IGNORE -> 0.0D;
        };
        return behaviorWeight
                + signal.affinity() * 28.0D
                + signal.interest() * 14.0D
                - signal.tension() * 10.0D
                + Math.max(0.0D, place.score()) * 0.08D
                - placeDistance * 0.35D
                - ownerDistance * 0.15D;
    }

    private Vec3 guardDestination(Vec3 center, Vec3 ownerPosition) {
        Vec3 fromOwner = center.subtract(ownerPosition.x, center.y, ownerPosition.z);
        if (fromOwner.lengthSqr() < 0.0001D) {
            fromOwner = this.moe.position().subtract(center);
        }
        if (fromOwner.lengthSqr() < 0.0001D) {
            fromOwner = new Vec3(1.0D, 0.0D, 0.0D);
        }
        Vec3 radial = fromOwner.normalize();
        return new Vec3(center.x, this.moe.getY(), center.z).add(radial.scale(2.25D));
    }

    private Vec3 avoidPlaceDestination(Vec3 center, double distance) {
        Vec3 away = this.moe.position().subtract(center);
        if (away.lengthSqr() < 0.0001D) {
            away = new Vec3(this.moe.getRandom().nextDouble() - 0.5D, 0.0D, this.moe.getRandom().nextDouble() - 0.5D);
        }
        return this.moe.position().add(away.normalize().scale(distance));
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

    private boolean updateBloodTypeMovement(Moe socialTarget, MoeSocialRules.SocialSignal signal) {
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger()) {
            this.clearMovementIntent();
            return false;
        }
        double distanceSqr = this.moe.distanceToSqr(socialTarget);
        double stepDistance = MoeSocialRules.socialStepDistance(this.moe.getDere());
        double speed = MoeSocialRules.socialMoveSpeed(this.moe.getDere());
        switch (MoeSocialRules.movementFor(signal, distanceSqr)) {
            case APPROACH -> {
                Vec3 toward = socialTarget.position().subtract(this.moe.position());
                double distance = toward.length();
                if (distance > 0.0001D) {
                    Vec3 destination = this.moe.position().add(toward.normalize().scale(Math.min(stepDistance, distance - 1.5D)));
                    return this.setMovementDestination(destination, speed);
                }
            }
            case AVOID -> {
                Vec3 away = this.moe.position().subtract(socialTarget.position());
                if (away.lengthSqr() < 0.0001D) {
                    away = new Vec3(this.moe.getRandom().nextDouble() - 0.5D, 0.0D, this.moe.getRandom().nextDouble() - 0.5D);
                }
                Vec3 destination = this.moe.position().add(away.normalize().scale(stepDistance));
                return this.setMovementDestination(destination, speed);
            }
            case IDLE -> {
            }
        }
        return false;
    }

    private boolean updateDereReaction(Moe socialTarget, MoeSocialRules.DereReaction reaction) {
        if (this.moe.isFollowing() || this.moe.isSitting() || this.moe.isPassenger()) {
            return false;
        }
        return switch (reaction) {
            case CLING -> moveToward(socialTarget, MoeSocialRules.socialStepDistance(this.moe.getDere()) * 1.25D, 1.25D, 0.75D);
            case FLUSTER_RETREAT -> moveAwayFrom(socialTarget, 3.0D, MoeSocialRules.socialMoveSpeed(this.moe.getDere()));
            case SHY_RETREAT -> moveAwayFrom(socialTarget, 2.5D, 0.8D);
            case SHOW_OFF -> {
                Vec3 around = this.moe.position().subtract(socialTarget.position());
                if (around.lengthSqr() < 0.0001D) {
                    around = new Vec3(1.0D, 0.0D, 0.0D);
                }
                Vec3 side = new Vec3(-around.z, 0.0D, around.x).normalize().scale(2.0D);
                Vec3 destination = this.moe.position().add(side);
                yield this.setMovementDestination(destination, MoeSocialRules.socialMoveSpeed(this.moe.getDere()));
            }
            case CELEBRATE, OBSERVE, NONE -> false;
        };
    }

    private boolean moveToward(Moe socialTarget, double stepDistance, double speed, double personalSpace) {
        Vec3 toward = socialTarget.position().subtract(this.moe.position());
        double distance = toward.length();
        if (distance > personalSpace && distance > 0.0001D) {
            Vec3 destination = this.moe.position().add(toward.normalize().scale(Math.min(stepDistance, distance - personalSpace)));
            return this.setMovementDestination(destination, speed);
        }
        return false;
    }

    private boolean moveAwayFrom(Moe socialTarget, double stepDistance, double speed) {
        Vec3 away = this.moe.position().subtract(socialTarget.position());
        if (away.lengthSqr() < 0.0001D) {
            away = new Vec3(this.moe.getRandom().nextDouble() - 0.5D, 0.0D, this.moe.getRandom().nextDouble() - 0.5D);
        }
        Vec3 destination = this.moe.position().add(away.normalize().scale(stepDistance));
        return this.setMovementDestination(destination, speed);
    }

    private boolean setMovementDestination(Vec3 destination, double speed) {
        this.movementDestination = destination;
        this.movementSpeed = speed;
        this.movementTicks = MoeSocialRules.socialMovementDuration(this.moe.getDere());
        return this.moveToDestination(destination, speed);
    }

    private boolean moveToDestination(Vec3 destination, double speed) {
        boolean navigating = this.moe.getNavigation().moveTo(destination.x, destination.y, destination.z, speed);
        if (!navigating) {
            this.moe.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
        }
        return true;
    }

    private void applyDereFeeling(MoeSocialRules.DereReaction reaction) {
        switch (reaction) {
            case CELEBRATE -> this.moe.addRelaxation(0.04F);
            case CLING, FLUSTER_RETREAT, SHY_RETREAT, SHOW_OFF -> this.moe.addStress(0.03F);
            case OBSERVE, NONE -> {
            }
        }
    }

    private void updateAnimation(MoeSocialRules.SocialSignal signal, MoeSocialRules.SocialVisual visual, MoeSocialRules.DereReaction reaction) {
        String animation = switch (reaction) {
            case CELEBRATE, CLING -> "HAPPY_DANCE";
            case FLUSTER_RETREAT, SHY_RETREAT -> "SHIVER";
            case SHOW_OFF -> "WAVE";
            case OBSERVE -> visual == MoeSocialRules.SocialVisual.INTEREST ? "AWE" : "LOOK_AROUND";
            case NONE -> socialAnimationFor(signal, visual);
        };
        this.moe.setTemporaryAnimationKey(animation, 44);
    }

    private static String socialAnimationFor(MoeSocialRules.SocialSignal signal, MoeSocialRules.SocialVisual visual) {
        if (signal.tension() > signal.affinity() && signal.tension() >= 0.35F) {
            return "SHIVER";
        }
        if (signal.affinity() >= 0.6F || visual == MoeSocialRules.SocialVisual.FAME || visual == MoeSocialRules.SocialVisual.AFFINITY) {
            return "HAPPY_DANCE";
        }
        if (signal.interest() >= 0.5F || visual == MoeSocialRules.SocialVisual.INTEREST) {
            return "AWE";
        }
        return "DEFAULT";
    }

    private record SocialPlaceCandidate(
            Moe owner,
            MoePlaceMemory.Place place,
            MoeSocialRules.SocialPlaceBehavior behavior,
            MoeSocialPlaceMemory memory) {
    }
}
