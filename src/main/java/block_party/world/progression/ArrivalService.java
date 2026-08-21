package block_party.world.progression;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomResources;
import block_party.registry.CustomTags;
import java.util.UUID;
import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Evaluates data-backed Moe arrival definitions independently of block family. */
public final class ArrivalService {
    private ArrivalService() {
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof ServerPlayer player)
            tryArrival(level, event.getPos(), event.getPlacedBlock(), player.getUUID());
    }

    public static boolean recordCollectedItem(ServerLevel level, UUID player, ItemStack stack, int collected) {
        return PlayerProgressionCounters.recordItem(level, player, stack, collected);
    }

    public static boolean tryArrival(ServerLevel level, BlockPos placementPos, BlockState placed, UUID player) {
        if (level == null || placementPos == null || placed == null || player == null)
            return false;
        ArrivalDefinition arrival = arrivalForPlacement(level, placementPos, placed, player);
        return tryArrival(level, placementPos, placed, arrival, player);
    }

    public static boolean tryArrival(ServerLevel level, BlockPos placementPos, BlockState placed, ArrivalDefinition arrival, UUID player) {
        if (level == null || placementPos == null || placed == null || player == null || arrival == null
                || !arrival.matches(placed, level.getBlockState(placementPos.below()))
                || !placed.canSurvive(level, placementPos)
                || arrival.collectedCount(level, player) < arrival.threshold()
                || !CardinalSpawnRules.canSpawn(level, arrival.result().defaultBlockState(), player)
                || !ToriiInfluence.contains(level, placementPos)
                || hasNearby(level, placementPos, arrival.result(), arrival.exclusionRadius())) {
            return false;
        }
        boolean cardinal = arrival.result().defaultBlockState().is(CustomTags.CARDINAL);
        BlockPos homePos = cardinal ? null : findCorporealHome(level, placementPos, arrival);
        if (!cardinal && homePos == null)
            return false;
        BlockState sourceState = cardinal ? arrival.result().defaultBlockState() : level.getBlockState(homePos);
        CompoundTag tileData = blockEntityData(level, homePos);
        if (!cardinal)
            level.removeBlock(homePos, false);
        BlockPos spawnPos = cardinal ? findSpawnPos(level, placementPos) : homePos;
        Moe moe = MoeSpawner.spawn(level, spawnPos, sourceState, player, tileData, created -> {
            if (!cardinal) {
                created.setHasHome(true);
                created.setHome(new DimBlockPos(level.dimension(), homePos));
                created.setRoutineIntent(RoutineIntent.SLEEP);
            }
        });

        // Cardinal identities may be reused; bind this encounter and its scene actions to its triggering player.
        if (moe == null && !cardinal)
            restoreBlock(level, homePos, sourceState, tileData);
        if (moe != null)
            moe.setDialogueTarget(player);
        return moe != null;
    }

    public static boolean hasArrival(BlockState result) {
        return result != null && CustomResources.ARRIVALS.definitions().stream()
                .anyMatch(arrival -> result.is(arrival.result()));
    }

    private static boolean hasNearby(ServerLevel level, BlockPos pos, Block result, double radius) {
        return !level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(radius), moe ->
                moe.isAlive() && !moe.isRemoved() && moe.getVisibleBlockState().is(result)).isEmpty();
    }

    private static ArrivalDefinition arrivalForPlacement(
            ServerLevel level, BlockPos pos, BlockState state, UUID player) {
        if (level == null || pos == null || state == null || player == null)
            return null;
        BlockState support = level.getBlockState(pos.below());
        return CustomResources.ARRIVALS.definitions().stream()
                .filter(arrival -> arrival.matches(state, support))
                .filter(arrival -> arrival.collectedCount(level, player) >= arrival.threshold())
                .max(Comparator.comparingInt(ArrivalDefinition::placementSpecificity)
                        .thenComparing(arrival -> arrival.id().toString()))
                .orElse(null);
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos placementPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = placementPos.relative(direction);
            if (level.getBlockState(candidate).isAir() && level.getBlockState(candidate.above()).isAir())
                return candidate;
        }
        return placementPos.above();
    }

    private static BlockPos findCorporealHome(ServerLevel level, BlockPos origin, ArrivalDefinition arrival) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        int radius = arrival.homeSearchRadius();
        int vertical = arrival.homeSearchVerticalRadius();
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-radius, -vertical, -radius),
                origin.offset(radius, vertical, radius))) {
            BlockPos pos = candidate.immutable();
            if (!level.getBlockState(pos).is(arrival.result()) || !canEmergeFrom(level, pos))
                continue;
            double distance = pos.distSqr(origin);
            if (distance < bestDistance) {
                best = pos;
                bestDistance = distance;
            }
        }
        return best;
    }

    private static boolean canEmergeFrom(ServerLevel level, BlockPos source) {
        return level.getBlockState(source.above()).getCollisionShape(level, source.above()).isEmpty()
                && !level.getBlockState(source.below()).getCollisionShape(level, source.below()).isEmpty();
    }

    private static CompoundTag blockEntityData(ServerLevel level, BlockPos pos) {
        if (pos == null)
            return new CompoundTag();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity == null ? new CompoundTag() : blockEntity.getPersistentData().copy();
    }

    private static void restoreBlock(ServerLevel level, BlockPos pos, BlockState state, CompoundTag tileData) {
        level.setBlock(pos, state, 3);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && tileData != null && !tileData.isEmpty()) {
            blockEntity.getPersistentData().merge(tileData);
            blockEntity.setChanged();
        }
    }
}
