package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.AttentionRecord;
import block_party.entities.Moe;
import block_party.scene.Response;
import block_party.scene.SceneObservation;
import block_party.scene.SceneObservationFactories;
import block_party.scene.SceneTrigger;
import block_party.world.Attention;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class AttentionGameTests {
    private AttentionGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void saplingDropsRecordForestAttentionAndSummonVisitor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1901L, 2901L);
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        BlockState state = Blocks.OAK_LEAVES.defaultBlockState();
        level.setBlock(pos, state, 3);
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, pos, state, player, List.of(new ItemStack(Items.OAK_SAPLING, 2)))) {
            helper.fail("Expected oak sapling drops to record forest attention");
            return;
        }

        try {
            AttentionRecord record = db.findAttention(player, "oak_forest", "sapling_drop").orElse(null);
            if (record == null || record.count() != 1 || !record.blockState().equals(state)
                    || !"minecraft:oak_sapling".equals(record.itemId()) || record.itemCount() != 2) {
                helper.fail("Expected oak_forest/sapling_drop attention record with oak sapling item, got " + record);
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected attention lookup to succeed: " + exception.getMessage());
            return;
        }

        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(4.0D));
        if (moes.size() != 1 || !player.equals(moes.getFirst().getDialogueTarget())) {
            helper.fail("Expected one attention Moe targeting the player, got " + moes);
            return;
        }
        Moe moe = moes.getFirst();
        if (!moe.isCardinal() || !moe.getVisibleBlockState().equals(Blocks.OAK_LOG.defaultBlockState())
                || !moe.hasCardinalForestChoreForTests()) {
            helper.fail("Expected oak sapling attention to summon an oak log cardinal chore visitor");
            return;
        }
        moe.sceneManager().tick();
        if (moe.hasDialogue()) {
            helper.fail("Expected oak forest attention to wait for player interaction before opening dialogue");
            return;
        }
        moe.triggerScene(SceneTrigger.RIGHT_CLICK);
        moe.sceneManager().tick();
        if (!moe.hasDialogue() || !moe.getDialogue().text().toLowerCase(java.util.Locale.ROOT).contains("oak sapling")
                || !moe.getDialogue().responses().containsKey(Response.NEXT_RESPONSE)) {
            helper.fail("Expected oak forest attention scene dialogue, got " + (moe.hasDialogue() ? moe.getDialogue().text() : "<none>"));
            return;
        }
        if (!filter("has_attention", json()).verify(moe)
                || !filter("attention_type", json("type", "oak_forest")).verify(moe)
                || !filter("attention_source", json("source", "sapling_drop")).verify(moe)
                || !filter("attention_item", json("item", "minecraft:oak_sapling")).verify(moe)
                || !filter("attention_block", json("block", "minecraft:oak_leaves")).verify(moe)) {
            helper.fail("Expected attention scene filters to match oak forest sapling attention");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void oakAttentionVisitorCollectsAndPlantsDroppedSaplings(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1903L, 2903L);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 1, 2));
        level.setBlock(origin.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        clearColumn(level, origin, 6);
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, origin, Blocks.OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected oak sapling attention to start");
            return;
        }
        Moe moe = level.getEntitiesOfClass(Moe.class, new AABB(origin).inflate(4.0D)).getFirst();
        moe.clearDialogue();
        moe.moveToBlock(origin);
        ItemEntity sapling = new ItemEntity(level, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, new ItemStack(Items.OAK_SAPLING));
        level.addFreshEntity(sapling);

        if (!moe.tickCardinalForestChoreForTests() || sapling.isAlive()) {
            helper.fail("Expected oak visitor to collect the dropped sapling");
            return;
        }
        if (!moe.getInventory().getItem(0).is(Items.OAK_SAPLING)) {
            helper.fail("Expected collected oak sapling to enter Moe inventory");
            return;
        }
        if (!moe.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.OAK_SAPLING)) {
            helper.fail("Expected collected oak sapling to appear in Moe hand");
            return;
        }
        if (!moe.tickCardinalForestChoreForTests()) {
            helper.fail("Expected oak visitor to plant the collected sapling");
            return;
        }
        if (!level.getBlockState(origin).is(Blocks.OAK_SAPLING) || moe.hasCardinalForestChoreForTests()
                || !moe.getInventory().getItem(0).isEmpty()) {
            helper.fail("Expected oak visitor to finish after planting an oak sapling");
            return;
        }
        if (!moe.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            helper.fail("Expected Moe hand to clear after planting the last oak sapling");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void oakAttentionVisitorSpacesSaplingPlanting(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1906L, 2906L);
        BlockPos origin = helper.absolutePos(new BlockPos(4, 1, 4));
        level.setBlock(origin.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        level.setBlock(origin, Blocks.OAK_SAPLING.defaultBlockState(), 3);
        BlockPos spaced = origin.east(5);
        level.setBlock(spaced.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        clearColumn(level, spaced, 6);
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, origin, Blocks.OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected oak sapling attention to start");
            return;
        }
        Moe moe = level.getEntitiesOfClass(Moe.class, new AABB(origin).inflate(8.0D)).getFirst();
        moe.clearDialogue();
        moe.moveToBlock(spaced);
        moe.getInventory().setItem(0, new ItemStack(Items.OAK_SAPLING));

        if (!moe.tickCardinalForestChoreForTests()) {
            helper.fail("Expected oak visitor to plant at a spaced sapling spot");
            return;
        }
        if (!level.getBlockState(spaced).is(Blocks.OAK_SAPLING)) {
            helper.fail("Expected oak visitor to skip close sapling spots and plant at the spaced spot");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void oakAttentionVisitorIgnoresUnreachableSaplingDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1907L, 2907L);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 1, 2));
        level.setBlock(origin.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, origin, Blocks.OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected oak sapling attention to start");
            return;
        }
        Moe moe = level.getEntitiesOfClass(Moe.class, new AABB(origin).inflate(4.0D)).getFirst();
        moe.clearDialogue();
        moe.moveToBlock(origin);
        BlockPos trappedPos = origin.above(5);
        level.setBlock(trappedPos, Blocks.OAK_LEAVES.defaultBlockState(), 3);
        ItemEntity trappedSapling = new ItemEntity(level, trappedPos.getX() + 0.5D, trappedPos.getY() + 0.5D, trappedPos.getZ() + 0.5D, new ItemStack(Items.OAK_SAPLING));
        level.addFreshEntity(trappedSapling);

        if (moe.tickCardinalForestChoreForTests()) {
            helper.fail("Expected oak visitor to ignore an unreachable sapling drop");
            return;
        }
        if (!trappedSapling.isAlive() || !moe.getInventory().getItem(0).isEmpty()) {
            helper.fail("Expected unreachable sapling to remain uncollected");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void naturalSaplingDropUsesRecentOakLogBreakAttentionContext(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1904L, 2904L);
        BlockPos log = helper.absolutePos(new BlockPos(2, 1, 2));
        BlockPos leaves = helper.absolutePos(new BlockPos(3, 2, 2));
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.rememberBrokenBlock(level, log, Blocks.OAK_LOG.defaultBlockState(), player)) {
            helper.fail("Expected oak log break to create attention context");
            return;
        }
        if (!Attention.noticeDrops(level, leaves, Blocks.OAK_LEAVES.defaultBlockState(), (UUID) null, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected natural oak sapling drop near recent oak log break to record attention");
            return;
        }

        try {
            AttentionRecord record = db.findAttention(player, "oak_forest", "sapling_drop").orElse(null);
            if (record == null || !"minecraft:oak_sapling".equals(record.itemId())) {
                helper.fail("Expected natural sapling drop to attach to recent oak log cutter, got " + record);
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected attention lookup to succeed: " + exception.getMessage());
            return;
        }
        killNearbyMoes(level, leaves);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void nonSaplingDropsDoNotRecordAttention(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1902L, 2902L);
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (Attention.noticeDrops(level, pos, Blocks.STONE.defaultBlockState(), player, List.of(new ItemStack(Items.COBBLESTONE)))) {
            helper.fail("Expected non-sapling drop to be ignored by forest attention");
            return;
        }
        try {
            if (db.latestAttention(player).isPresent()) {
                helper.fail("Expected no attention records for ignored drops");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected attention lookup to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void naturalSaplingDropWithoutRecentLogBreakIsIgnored(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1905L, 2905L);
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (Attention.noticeDrops(level, pos, Blocks.OAK_LEAVES.defaultBlockState(), (UUID) null, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected unattributed natural oak sapling drop to be ignored");
            return;
        }
        try {
            if (db.latestAttention(player).isPresent()) {
                helper.fail("Expected no attention records without recent tree cut context");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected attention lookup to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    private static SceneObservation filter(String path, JsonObject json) {
        return SceneObservationFactories.build(BlockParty.source(path), json);
    }

    private static JsonObject json() {
        return new JsonObject();
    }

    private static JsonObject json(String key, String value) {
        JsonObject object = new JsonObject();
        object.addProperty(key, value);
        return object;
    }

    private static void clearAttention(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM AttentionRecords;")) {
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static void killNearbyMoes(ServerLevel level, BlockPos pos) {
        for (Moe moe : level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(8.0D))) {
            moe.discard();
        }
    }

    private static void clearColumn(ServerLevel level, BlockPos pos, int height) {
        for (int y = 0; y <= height; ++y) {
            level.setBlock(pos.above(y), Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
