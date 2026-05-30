package block_party.entities.social;

import block_party.entities.Moe;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record MoeSocialContext(
        Moe target,
        MoeSocialRules.SocialSignal signal,
        MoeSocialRules.SocialVisual visual,
        MoeSocialRules.DereReaction reaction) {
    public static Optional<MoeSocialContext> find(Moe moe, double radius) {
        AABB area = moe.getBoundingBox().inflate(radius);
        Moe socialTarget = null;
        MoeSocialRules.SocialSignal strongest = new MoeSocialRules.SocialSignal(0.0F, 0.0F, 0.0F);
        for (Moe other : moe.level().getEntities(EntityTypeTest.forClass(Moe.class), area, other ->
                other != moe && other.isAlive() && !other.isRemoved())) {
            MoeSocialRules.SocialSignal signal = signal(moe, other);
            if (signal.interest() > strongest.interest()) {
                strongest = signal;
                socialTarget = other;
            }
        }
        if (socialTarget == null) {
            return Optional.empty();
        }
        MoeSocialRules.SocialVisual visual = MoeSocialRules.visualFor(moe.getBloodType(), socialTarget.getBloodType(), strongest);
        MoeSocialRules.DereReaction reaction = MoeSocialRules.dereReaction(moe.getDere(), visual, strongest, moe.distanceToSqr(socialTarget));
        return Optional.of(new MoeSocialContext(socialTarget, strongest, visual, reaction));
    }

    public static List<Moe> nearby(Moe moe, double radius) {
        List<Moe> nearby = new ArrayList<>();
        AABB area = moe.getBoundingBox().inflate(radius);
        for (Moe other : moe.level().getEntities(EntityTypeTest.forClass(Moe.class), area, other ->
                other != moe && other.isAlive() && !other.isRemoved())) {
            nearby.add(other);
        }
        nearby.sort(Comparator.comparingDouble(moe::distanceToSqr));
        return List.copyOf(nearby);
    }

    public static MoeSocialRules.SocialSignal signal(Moe observer, Moe target) {
        return MoeSocialRules.combine(
                MoeSocialRules.bloodSignal(observer.getBloodType(), target.getBloodType()),
                SocialAffinities.signal(profile(observer), profile(target)));
    }

    private static SocialAffinities.Profile profile(Moe moe) {
        return new SocialAffinities.Profile(
                moe.getActualBlockState(),
                moe.getBloodType(),
                moe.getDere(),
                moe.getZodiac(),
                moe.getGender(),
                moe.getEmotion());
    }
}
