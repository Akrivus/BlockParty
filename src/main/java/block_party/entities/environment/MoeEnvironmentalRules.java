package block_party.entities.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class MoeEnvironmentalRules {
    public static final double WEATHER_RADIUS = 16.0D;
    public static final double LIGHT_RADIUS = 18.0D;
    public static final double CURIOSITY_RADIUS = 10.0D;

    private MoeEnvironmentalRules() {
    }

    public static ShelterScore shelterScore(Level level, BlockPos feetPos) {
        BlockPos headPos = feetPos.above();
        boolean covered = !level.canSeeSky(headPos);
        int blockLight = blockLight(level, feetPos);
        boolean nearDoor = hasDoorNearby(level, feetPos, 4);
        int solidSides = solidSideCount(level, feetPos);

        int score = 0;
        if (covered) {
            score += 30;
        }
        if (nearDoor) {
            score += 35;
        }
        score += Math.min(25, blockLight * 2);
        score += solidSides * 5;
        if (covered && nearDoor && blockLight >= 8) {
            score += 35;
        }
        return new ShelterScore(score, covered, nearDoor, blockLight, solidSides);
    }

    public static boolean isStrongShelter(Level level, BlockPos feetPos) {
        return shelterScore(level, feetPos).score() >= 75;
    }

    public static Optional<BlockPos> bestShelter(Level level, BlockPos origin, double radius) {
        int currentScore = shelterScore(level, origin).score();
        BlockPos best = null;
        int bestScore = currentScore;
        int horizontalRadius = (int) Math.ceil(radius);
        for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    if (x * x + z * z > radius * radius) {
                        continue;
                    }
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!canStandAt(level, candidate)) {
                        continue;
                    }
                    ShelterScore score = shelterScore(level, candidate);
                    if (score.score() > bestScore) {
                        best = candidate.immutable();
                        bestScore = score.score();
                    }
                }
            }
        }
        return bestScore >= currentScore + 15 ? Optional.ofNullable(best) : Optional.empty();
    }

    public static Optional<BlockPos> bestLight(Level level, BlockPos origin, double radius) {
        int currentScore = lightScore(level, origin);
        BlockPos best = null;
        int bestScore = currentScore;
        int horizontalRadius = (int) Math.ceil(radius);
        for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    if (x * x + z * z > radius * radius) {
                        continue;
                    }
                    BlockPos candidate = origin.offset(x, y, z);
                    if (!canStandAt(level, candidate)) {
                        continue;
                    }
                    int score = lightScore(level, candidate);
                    if (score > bestScore) {
                        best = candidate.immutable();
                        bestScore = score;
                    }
                }
            }
        }
        return bestScore >= currentScore + 25 ? Optional.ofNullable(best) : Optional.empty();
    }

    public static int lightScore(Level level, BlockPos feetPos) {
        ShelterScore shelter = shelterScore(level, feetPos);
        int score = shelter.blockLight() * 10;
        if (shelter.nearDoor()) {
            score += 20;
        }
        if (shelter.covered()) {
            score += 10;
        }
        return score + shelter.solidSides() * 2;
    }

    public static int blockLight(Level level, BlockPos feetPos) {
        BlockPos headPos = feetPos.above();
        return Math.max(
                Math.max(level.getBrightness(LightLayer.BLOCK, feetPos), level.getBlockState(feetPos).getLightEmission()),
                Math.max(level.getBrightness(LightLayer.BLOCK, headPos), level.getBlockState(headPos).getLightEmission()));
    }

    public static boolean canStandAt(Level level, BlockPos feetPos) {
        return level.getBlockState(feetPos).getCollisionShape(level, feetPos).isEmpty()
                && level.getBlockState(feetPos.above()).getCollisionShape(level, feetPos.above()).isEmpty()
                && !level.getBlockState(feetPos.below()).getCollisionShape(level, feetPos.below()).isEmpty();
    }

    private static boolean hasDoorNearby(Level level, BlockPos origin, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (level.getBlockState(pos).getBlock() instanceof DoorBlock) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int solidSideCount(Level level, BlockPos origin) {
        int count = 0;
        count += isWallLike(level, origin.north()) ? 1 : 0;
        count += isWallLike(level, origin.south()) ? 1 : 0;
        count += isWallLike(level, origin.east()) ? 1 : 0;
        count += isWallLike(level, origin.west()) ? 1 : 0;
        return count;
    }

    private static boolean isWallLike(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getCollisionShape(level, pos).max(net.minecraft.core.Direction.Axis.Y) >= 0.9D;
    }

    public record ShelterScore(int score, boolean covered, boolean nearDoor, int blockLight, int solidSides) {
    }
}
