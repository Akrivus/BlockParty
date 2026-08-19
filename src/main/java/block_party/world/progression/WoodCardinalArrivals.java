package block_party.world.progression;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.registry.CustomTags;
import java.util.UUID;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.BlockEvent;

/** Player conduct that introduces wood-family cardinal Moes after Suzu opens a gate. */
public final class WoodCardinalArrivals {
    public static final int REQUIRED_LOGS = 64;
    private static final int OAK_TIME_START = 0;
    private static final int OAK_TIME_END = 4000;
    private static final double ACTIVE_CARDINAL_RADIUS = 24.0D;
    private static final List<Arrival> ARRIVALS = List.of(
            new Arrival(Blocks.OAK_LOG, Blocks.OAK_SAPLING, Items.OAK_LOG),
            new Arrival(Blocks.BIRCH_LOG, Blocks.BIRCH_SAPLING, Items.BIRCH_LOG),
            new Arrival(Blocks.SPRUCE_LOG, Blocks.SPRUCE_SAPLING, Items.SPRUCE_LOG),
            new Arrival(Blocks.JUNGLE_LOG, Blocks.JUNGLE_SAPLING, Items.JUNGLE_LOG),
            new Arrival(Blocks.ACACIA_LOG, Blocks.ACACIA_SAPLING, Items.ACACIA_LOG),
            new Arrival(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_SAPLING, Items.DARK_OAK_LOG));

    private WoodCardinalArrivals() {
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        tryArrival(level, event.getPos(), event.getPlacedBlock(), player.getUUID(), level.getDayTime(), true);
    }

    public static boolean recordOakLogs(ServerLevel level, UUID player, ItemStack stack, int collected) {
        return PlayerProgressionCounters.recordItem(level, player, stack, collected);
    }

    public static boolean tryOakArrival(ServerLevel level, BlockPos saplingPos, BlockState sapling, UUID player) {
        return tryOakArrival(level, saplingPos, sapling, player, level == null ? 0L : level.getDayTime());
    }

    public static boolean tryOakArrival(
            ServerLevel level, BlockPos saplingPos, BlockState sapling, UUID player, long dayTime) {
        return tryArrival(level, saplingPos, sapling, player, dayTime, false);
    }

    public static boolean tryArrival(
            ServerLevel level, BlockPos saplingPos, BlockState sapling, UUID player, long dayTime) {
        return tryArrival(level, saplingPos, sapling, player, dayTime, true);
    }

    private static boolean tryArrival(
            ServerLevel level, BlockPos saplingPos, BlockState sapling, UUID player, long dayTime, boolean requireTorii) {
        Arrival arrival = arrivalForSapling(sapling);
        if (level == null || saplingPos == null || sapling == null || player == null
                || arrival == null
                || !sapling.canSurvive(level, saplingPos)
                || !oakTime(dayTime)
                || PlayerProgressionCounters.countItem(level, player, arrival.item()) < REQUIRED_LOGS
                || !CardinalSpawnRules.canSpawn(level, arrival.log().defaultBlockState(), player)
                || requireTorii && !ToriiInfluence.contains(level, saplingPos)
                || hasNearbyCardinal(level, saplingPos, arrival.log())) {
            return false;
        }
        BlockPos spawnPos = findSpawnPos(level, saplingPos);
        Moe moe = MoeSpawner.spawn(level, spawnPos, arrival.log().defaultBlockState(), player,
                new CompoundTag(), created -> {
                    created.setHasHome(true);
                    created.setHome(new DimBlockPos(level.dimension(), saplingPos));
                    created.setDialogueTarget(player);
                });
        return moe != null;
    }

    public static boolean usesPlantingArrival(BlockState cardinalState) {
        return cardinalState != null && ARRIVALS.stream().anyMatch(arrival -> cardinalState.is(arrival.log()));
    }

    private static boolean oakTime(long dayTime) {
        long time = Math.floorMod(dayTime, 24000L);
        return time >= OAK_TIME_START && time < OAK_TIME_END;
    }

    private static boolean hasNearbyCardinal(ServerLevel level, BlockPos pos, Block log) {
        return !level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(ACTIVE_CARDINAL_RADIUS), moe ->
                moe.isAlive() && !moe.isRemoved() && moe.getVisibleBlockState().is(log)).isEmpty();
    }

    private static Arrival arrivalForSapling(BlockState state) {
        return state == null ? null : ARRIVALS.stream().filter(arrival -> state.is(arrival.sapling())).findFirst().orElse(null);
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos saplingPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = saplingPos.relative(direction);
            if (level.getBlockState(candidate).isAir() && level.getBlockState(candidate.above()).isAir()) {
                return candidate;
            }
        }
        return saplingPos.above();
    }

    private record Arrival(Block log, Block sapling, Item item) {
    }
}
