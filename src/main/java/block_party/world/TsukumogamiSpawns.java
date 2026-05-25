package block_party.world;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.Shrine;
import block_party.db.records.TsukumogamiCandidate;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.entities.data.HidingSpots;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomTags;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class TsukumogamiSpawns {
    public static final int OBSERVABLE_DISTANCE = 2048;
    public static final long MATURATION_TICKS = 24000L;
    private static final int TICK_INTERVAL = 200;
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private TsukumogamiSpawns() {
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor accessor = event.getLevel();
        if (accessor instanceof ServerLevel level) {
            UUID player = event.getEntity() instanceof Player placer ? placer.getUUID() : EMPTY_UUID;
            trackPlacedBlock(level, event.getPos(), event.getPlacedBlock(), player);
        }
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            discardCandidate(level, event.getPos());
        }
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % TICK_INTERVAL != 0) {
            return;
        }
        for (ServerLevel level : event.getServer().getAllLevels()) {
            matureCandidates(level, level.getGameTime());
        }
    }

    public static boolean trackPlacedBlock(ServerLevel level, BlockPos pos, BlockState state, UUID player) {
        if (!state.is(CustomTags.TSUKUMOGAMI_CANDIDATES)) {
            return false;
        }
        BlockPartyDB db = BlockPartyDB.get(level);
        Shrine shrine = closestObservableShrine(db, level, pos).orElse(null);
        if (shrine == null) {
            return false;
        }
        CompoundTag tileEntityData = new CompoundTag();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            tileEntityData = blockEntity.getPersistentData().copy();
        }
        UUID owner = player == null || player.equals(EMPTY_UUID) ? shrine.playerUuid() : player;
        try {
            db.upsertTsukumogamiCandidate(
                    level,
                    pos,
                    owner,
                    state,
                    tileEntityData,
                    level.getGameTime(),
                    level.getGameTime() + MATURATION_TICKS,
                    shrine.databaseId());
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    public static int matureCandidates(ServerLevel level, long gameTime) {
        BlockPartyDB db = BlockPartyDB.get(level);
        int spawned = 0;
        try {
            for (TsukumogamiCandidate candidate : db.listMatureTsukumogamiCandidates(gameTime)) {
                if (candidate.dimPos().getDim() != level.dimension()) {
                    continue;
                }
                if (trySpawn(level, candidate)) {
                    ++spawned;
                }
            }
        } catch (SQLException ignored) {
        }
        return spawned;
    }

    public static boolean isValidCandidate(ServerLevel level, TsukumogamiCandidate candidate) {
        BlockPos pos = candidate.dimPos().getPos();
        BlockState current = level.getBlockState(pos);
        return candidate.dimPos().getDim() == level.dimension()
                && current.equals(candidate.blockState())
                && current.is(CustomTags.TSUKUMOGAMI_CANDIDATES)
                && !current.is(CustomTags.CARDINAL)
                && HidingSpots.get(level).find(pos).isEmpty()
                && closestObservableShrine(BlockPartyDB.get(level), level, pos).isPresent()
                && canSpawnMoeAt(level, pos)
                && !isRedstoneCircuitPosition(level, pos);
    }

    private static boolean trySpawn(ServerLevel level, TsukumogamiCandidate candidate) {
        BlockPartyDB db = BlockPartyDB.get(level);
        try {
            if (!isValidCandidate(level, candidate)) {
                db.deleteTsukumogamiCandidate(candidate.databaseId());
                return false;
            }
            BlockPos sourcePos = candidate.dimPos().getPos();
            BlockEntity blockEntity = level.getBlockEntity(sourcePos);
            CompoundTag tileEntityData = blockEntity == null ? candidate.tileEntityData() : blockEntity.getPersistentData().copy();
            Moe moe = MoeSpawner.spawn(
                    level,
                    sourcePos.above(),
                    level.getBlockState(sourcePos),
                    candidate.playerUuid(),
                    tileEntityData,
                    created -> {
                        created.setHasHome(true);
                        created.setHome(new DimBlockPos(level.dimension(), sourcePos));
                        created.setRoutineIntent(RoutineIntent.REST);
                    });
            if (moe == null) {
                db.deleteTsukumogamiCandidate(candidate.databaseId());
                return false;
            }
            level.destroyBlock(sourcePos, false);
            db.deleteTsukumogamiCandidate(candidate.databaseId());
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    private static Optional<Shrine> closestObservableShrine(BlockPartyDB db, ServerLevel level, BlockPos pos) {
        try {
            return db.listShrineRows(level.dimension()).stream()
                    .filter(shrine -> shrine.dimPos().getPos().distManhattan(pos) <= OBSERVABLE_DISTANCE)
                    .min(Comparator.comparingInt(shrine -> shrine.dimPos().getPos().distManhattan(pos)));
        } catch (SQLException exception) {
            return Optional.empty();
        }
    }

    private static boolean canSpawnMoeAt(ServerLevel level, BlockPos sourcePos) {
        BlockPos spawnPos = sourcePos.above();
        return level.getBlockState(spawnPos).isAir() && level.getBlockState(spawnPos.above()).getCollisionShape(level, spawnPos.above()).isEmpty();
    }

    private static boolean isRedstoneCircuitPosition(ServerLevel level, BlockPos pos) {
        if (level.hasNeighborSignal(pos) || isRedstoneComponent(level.getBlockState(pos))) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (isRedstoneComponent(neighbor) || neighbor.isSignalSource()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRedstoneComponent(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.REDSTONE_WIRE
                || block == Blocks.REDSTONE_TORCH
                || block == Blocks.REDSTONE_WALL_TORCH
                || block == Blocks.REDSTONE_BLOCK
                || block == Blocks.REPEATER
                || block == Blocks.COMPARATOR
                || block == Blocks.OBSERVER
                || block == Blocks.LEVER
                || block == Blocks.STONE_BUTTON
                || block == Blocks.OAK_BUTTON
                || block == Blocks.STONE_PRESSURE_PLATE
                || block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE
                || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE
                || block == Blocks.TRIPWIRE
                || block == Blocks.TRIPWIRE_HOOK
                || block == Blocks.PISTON
                || block == Blocks.STICKY_PISTON
                || block == Blocks.DISPENSER
                || block == Blocks.DROPPER
                || block == Blocks.HOPPER
                || block == Blocks.TARGET
                || block == Blocks.SCULK_SENSOR
                || block == Blocks.CALIBRATED_SCULK_SENSOR;
    }

    private static void discardCandidate(ServerLevel level, BlockPos pos) {
        try {
            BlockPartyDB.get(level).deleteTsukumogamiCandidate(level, pos);
        } catch (SQLException ignored) {
        }
    }
}
