package block_party.world;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.scene.SceneTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Attention {
    public static final String OAK_FOREST_TYPE = "oak_forest";
    public static final String SAPLING_DROP_SOURCE = "sapling_drop";
    public static final Definition OAK_FOREST = new Definition(
            OAK_FOREST_TYPE,
            SAPLING_DROP_SOURCE,
            Items.OAK_SAPLING,
            Blocks.OAK_LOG,
            Blocks.OAK_LOG.defaultBlockState());
    private static final List<Definition> DEFINITIONS = List.of(OAK_FOREST);
    private static final int TREE_CUT_MEMORY_TICKS = 20 * 90;
    private static final double TREE_CUT_MEMORY_RADIUS = 12.0D;
    private static final Map<String, List<TreeCutMemory>> RECENT_TREE_CUTS = new ConcurrentHashMap<>();

    private Attention() {
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        RECENT_TREE_CUTS.clear();
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getPlayer() instanceof ServerPlayer player) {
            rememberBrokenBlock(level, event.getPos(), event.getState(), player.getUUID());
        }
    }

    public static void onBlockDrops(BlockDropsEvent event) {
        UUID playerUuid = event.getBreaker() instanceof ServerPlayer player ? player.getUUID() : null;
        noticeDrops(event.getLevel(), event.getPos(), event.getState(), playerUuid, event.getDrops() == null
                ? List.of()
                : event.getDrops().stream().map(ItemEntity::getItem).toList());
    }

    public static boolean rememberBrokenBlock(ServerLevel level, BlockPos pos, BlockState state, UUID playerUuid) {
        Definition definition = definitionForBrokenBlock(state);
        if (level == null || pos == null || playerUuid == null || definition == null) {
            return false;
        }
        String key = levelKey(level);
        List<TreeCutMemory> memories = RECENT_TREE_CUTS.computeIfAbsent(key, ignored -> new ArrayList<>());
        long now = level.getGameTime();
        memories.removeIf(memory -> now - memory.gameTime() > TREE_CUT_MEMORY_TICKS);
        memories.add(new TreeCutMemory(definition, playerUuid, pos.immutable(), now));
        return true;
    }

    public static boolean noticeDrops(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player, List<ItemEntity> drops) {
        if (player == null) {
            return false;
        }
        return noticeDrops(level, pos, state, player.getUUID(), drops == null
                ? List.of()
                : drops.stream().map(ItemEntity::getItem).toList());
    }

    public static boolean noticeDrops(ServerLevel level, BlockPos pos, BlockState state, UUID playerUuid, List<ItemStack> drops) {
        if (level == null || pos == null || state == null || drops == null) {
            return false;
        }
        DropAttention drop = firstAttentionDrop(drops);
        ItemStack sapling = drop.stack();
        if (sapling.isEmpty()) {
            return false;
        }
        Definition definition = drop.definition();
        UUID attentionPlayer = playerUuid == null ? recentTreeCut(level, pos, definition).map(TreeCutMemory::playerUuid).orElse(null) : playerUuid;
        if (attentionPlayer == null) {
            return false;
        }
        String itemId = BuiltInRegistries.ITEM.getKey(sapling.getItem()).toString();
        try {
            BlockPartyDB.get(level).recordAttention(
                    level,
                    attentionPlayer,
                    definition.type(),
                    definition.source(),
                    pos,
                    state,
                    itemId,
                    sapling.getCount(),
                    level.getGameTime());
        } catch (SQLException exception) {
            return false;
        }
        Moe moe = summonAttentionMoe(level, pos, definition.cardinalState(), attentionPlayer);
        if (moe != null) {
            moe.setDialogueTarget(attentionPlayer);
            definition.startChore(moe, pos, attentionPlayer);
            moe.triggerScene(SceneTrigger.ATTENTION);
        }
        return true;
    }

    private static Optional<TreeCutMemory> recentTreeCut(ServerLevel level, BlockPos pos, Definition definition) {
        String key = levelKey(level);
        List<TreeCutMemory> memories = RECENT_TREE_CUTS.get(key);
        if (memories == null || memories.isEmpty()) {
            return Optional.empty();
        }
        long now = level.getGameTime();
        memories.removeIf(memory -> now - memory.gameTime() > TREE_CUT_MEMORY_TICKS);
        double radiusSqr = TREE_CUT_MEMORY_RADIUS * TREE_CUT_MEMORY_RADIUS;
        return memories.stream()
                .filter(memory -> memory.definition().equals(definition))
                .filter(memory -> memory.pos().distSqr(pos) <= radiusSqr)
                .min(Comparator
                        .comparingDouble((TreeCutMemory memory) -> memory.pos().distSqr(pos))
                        .thenComparingLong(memory -> -memory.gameTime()));
    }

    private static String levelKey(ServerLevel level) {
        return System.identityHashCode(level.getServer()) + ":" + level.dimension().location();
    }

    private static Definition definitionForBrokenBlock(BlockState state) {
        if (state == null) {
            return null;
        }
        for (Definition definition : DEFINITIONS) {
            if (definition.matchesBrokenBlock(state)) {
                return definition;
            }
        }
        return null;
    }

    private static DropAttention firstAttentionDrop(List<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (!stack.isEmpty() && stack.is(ItemTags.SAPLINGS)) {
                for (Definition definition : DEFINITIONS) {
                    if (definition.matchesDrop(stack)) {
                        return new DropAttention(definition, stack);
                    }
                }
            }
        }
        return new DropAttention(null, ItemStack.EMPTY);
    }

    private static Moe summonAttentionMoe(ServerLevel level, BlockPos sourcePos, BlockState state, UUID player) {
        BlockPos spawnPos = findSpawnPos(level, sourcePos);
        return MoeSpawner.spawn(level, spawnPos, state, player, new CompoundTag(), created -> {
            created.setHasHome(true);
            created.setHome(new DimBlockPos(level.dimension(), sourcePos));
        });
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos sourcePos) {
        for (BlockPos candidate : BlockPos.betweenClosed(sourcePos.offset(-2, 0, -2), sourcePos.offset(2, 1, 2))) {
            BlockPos immutable = candidate.immutable();
            if (level.getBlockState(immutable).isAir()
                    && level.getBlockState(immutable.above()).getCollisionShape(level, immutable.above()).isEmpty()) {
                return immutable;
            }
        }
        return sourcePos.above();
    }

    public record Definition(String type, String source, Item dropItem, Block contextBlock, BlockState cardinalState) {
        public boolean matchesDrop(ItemStack stack) {
            return stack.is(this.dropItem);
        }

        public boolean matchesBrokenBlock(BlockState state) {
            return state.is(this.contextBlock);
        }

        public void startChore(Moe moe, BlockPos origin, UUID playerUuid) {
            if (this.equals(OAK_FOREST)) {
                moe.startOakForestAttentionChore(origin, playerUuid);
            }
        }
    }

    private record DropAttention(Definition definition, ItemStack stack) {
    }

    private record TreeCutMemory(Definition definition, UUID playerUuid, BlockPos pos, long gameTime) {
    }
}
