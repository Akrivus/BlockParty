package block_party.entities.environment;

import block_party.entities.Moe;
import block_party.entities.social.MoeSocialRules;
import block_party.entities.social.SocialAffinities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class MoeEnvironmentalObservation {
    public static final double OBSERVATION_RADIUS = 8.0D;

    private MoeEnvironmentalObservation() {
    }

    public static Optional<Observation> scan(Moe moe) {
        BlockPos origin = moe.blockPosition();
        Observation best = Observation.none();
        int radius = (int) Math.ceil(OBSERVATION_RADIUS);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z > OBSERVATION_RADIUS * OBSERVATION_RADIUS) {
                        continue;
                    }
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = moe.level().getBlockState(pos);
                    if (state.isAir() || pos.equals(origin) || pos.equals(origin.above())) {
                        continue;
                    }
                    Observation observation = observeBlock(moe, pos, state);
                    if (observation.score() > best.score()) {
                        best = observation;
                    }
                }
            }
        }
        return best.kind() == Kind.NONE ? Optional.empty() : Optional.of(best);
    }

    public static Observation observeBlock(Moe moe, BlockPos pos, BlockState state) {
        SocialAffinities.LayeredSignal layeredSignal = SocialAffinities.layeredSignal(
                observerProfile(moe),
                new SocialAffinities.Profile(state, "", "", "", "", ""));
        MoeSocialRules.SocialSignal signal = layeredSignal.block();
        Kind kind = kindFor(signal);
        double score = score(signal, moe.blockPosition().distSqr(pos));
        return new Observation(kind, pos.immutable(), state, signal, layeredSignal, score);
    }

    public static String animationFor(Kind kind) {
        return switch (kind) {
            case AWE -> "AWE";
            case AFFINITY -> "HAPPY_DANCE";
            case TENSION -> "SHIVER";
            case NONE -> "DEFAULT";
        };
    }

    private static SocialAffinities.Profile observerProfile(Moe moe) {
        return new SocialAffinities.Profile(
                moe.getActualBlockState(),
                moe.getBloodType(),
                moe.getDere(),
                moe.getZodiac(),
                moe.getGender(),
                moe.getEmotion());
    }

    private static Kind kindFor(MoeSocialRules.SocialSignal signal) {
        if (signal.tension() >= 0.35F && signal.tension() >= signal.affinity()) {
            return Kind.TENSION;
        }
        if (signal.affinity() >= 0.35F) {
            return Kind.AFFINITY;
        }
        if (signal.interest() >= 0.4F) {
            return Kind.AWE;
        }
        return Kind.NONE;
    }

    private static double score(MoeSocialRules.SocialSignal signal, double distanceSqr) {
        double signalScore = signal.interest() * 3.0D + signal.tension() * 2.0D + signal.affinity();
        return signalScore - Math.sqrt(distanceSqr) * 0.04D;
    }

    public enum Kind {
        NONE,
        AWE,
        AFFINITY,
        TENSION
    }

    public record Observation(
            Kind kind,
            BlockPos pos,
            BlockState state,
            MoeSocialRules.SocialSignal signal,
            SocialAffinities.LayeredSignal layeredSignal,
            double score) {
        public static Observation none() {
            MoeSocialRules.SocialSignal empty = new MoeSocialRules.SocialSignal(0.0F, 0.0F, 0.0F);
            return new Observation(
                    Kind.NONE,
                    BlockPos.ZERO,
                    net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                    empty,
                    new SocialAffinities.LayeredSignal(empty, empty, empty, empty, empty, empty),
                    Double.NEGATIVE_INFINITY);
        }
    }
}
