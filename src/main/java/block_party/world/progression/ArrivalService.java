package block_party.world.progression;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.registry.CustomResources;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Evaluates data-backed Moe arrival definitions independently of block family. */
public final class ArrivalService {
    private ArrivalService() {
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof ServerPlayer player) {
            tryArrival(level, event.getPos(), event.getPlacedBlock(), player.getUUID());
        }
    }

    public static boolean recordCollectedItem(ServerLevel level, UUID player, ItemStack stack, int collected) {
        return PlayerProgressionCounters.recordItem(level, player, stack, collected);
    }

    public static boolean tryArrival(
            ServerLevel level, BlockPos placementPos, BlockState placed, UUID player) {
        ArrivalDefinition arrival = arrivalForPlacement(level, placementPos, placed);
        if (level == null || placementPos == null || placed == null || player == null
                || arrival == null
                || !placed.canSurvive(level, placementPos)
                || arrival.collectedCount(level, player) < arrival.threshold()
                || !CardinalSpawnRules.canSpawn(level, arrival.result().defaultBlockState(), player)
                || !ToriiInfluence.contains(level, placementPos)
                || hasNearby(level, placementPos, arrival.result(), arrival.exclusionRadius())) {
            return false;
        }
        Moe moe = MoeSpawner.spawn(level, findSpawnPos(level, placementPos), arrival.result().defaultBlockState(), player,
                new CompoundTag(), created -> {
                    created.setHasHome(true);
                    created.setHome(new DimBlockPos(level.dimension(), placementPos));
                });
        if (moe != null) {
            // Cardinal identities may be reused; bind this encounter and its scene actions to its triggering player.
            moe.setDialogueTarget(player);
        }
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

    private static ArrivalDefinition arrivalForPlacement(ServerLevel level, BlockPos pos, BlockState state) {
        if (level == null || pos == null || state == null) {
            return null;
        }
        BlockState support = level.getBlockState(pos.below());
        return CustomResources.ARRIVALS.definitions().stream()
                .filter(arrival -> arrival.matches(state, support)).findFirst().orElse(null);
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos placementPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = placementPos.relative(direction);
            if (level.getBlockState(candidate).isAir() && level.getBlockState(candidate.above()).isAir()) {
                return candidate;
            }
        }
        return placementPos.above();
    }
}
