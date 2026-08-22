package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.AttentionRecord;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.MoeSpawner;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.entities.movement.RoutineIntent;
import block_party.entities.chores.CardinalForestChore;
import block_party.entities.chores.PlaceBlockChores;
import block_party.scene.SceneVariables;
import block_party.world.progression.WoodFamilyProgression;
import block_party.world.progression.SamuraiProgression;
import block_party.world.progression.ArrivalService;
import block_party.world.progression.PlayerProgressionCounters;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomTags;
import block_party.registry.resources.ArrivalReloadListener;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.scene.actions.ResetProgressionCountersAction;
import block_party.registry.CustomEntities;
import block_party.scene.SceneObservation;
import block_party.scene.SceneObservationFactories;
import block_party.world.Attention;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
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

    @GameTest(template = "empty", timeoutTicks = 20, batch = "wood_arrival")
    public static void oakArrivalRequiresSuzuLogsToriiAndSapling(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID player = new UUID(1990L, 2990L);
        BlockPos sapling = helper.absolutePos(new BlockPos(3, 1, 3));
        level.setBlock(sapling.below(), Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(sapling, Blocks.OAK_SAPLING.defaultBlockState(), 3);
        SceneVariables.get(level).worldCookies().delete(SamuraiProgression.TORII_GATE_OPENED);

        ArrivalService.recordCollectedItem(level, player, new ItemStack(Items.OAK_LOG), 64);
        if (ArrivalService.tryArrival(level, sapling, level.getBlockState(sapling), player)) {
            helper.fail("Expected Oak arrival to wait for Suzu's gate");
            return;
        }
        SceneVariables.get(level).worldCookies().set(SamuraiProgression.TORII_GATE_OPENED, "true");
        SceneVariables.get(level).playerCounters(player).delete("progression/items/minecraft:oak_log");
        ArrivalService.recordCollectedItem(level, player, new ItemStack(Items.OAK_LOG), 63);
        if (ArrivalService.tryArrival(level, sapling, level.getBlockState(sapling), player)) {
            helper.fail("Expected Oak arrival to wait for 64 collected logs");
            return;
        }
        ArrivalService.recordCollectedItem(level, player, new ItemStack(Items.OAK_LOG), 1);
        BlockPos shrinePos = helper.absolutePos(new BlockPos(6, 1, 3));
        level.setBlock(shrinePos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ((ShrineTabletBlockEntity) level.getBlockEntity(shrinePos)).markClaimed(player);
        if (!ArrivalService.tryArrival(level, sapling, level.getBlockState(sapling), player)) {
            helper.fail("Expected valid Oak planting to trigger arrival; tracked logs="
                    + PlayerProgressionCounters.countItem(level, player, Items.OAK_LOG)
                    + ", gate=" + SceneVariables.get(level).worldCookies().get(SamuraiProgression.TORII_GATE_OPENED));
            return;
        }
        List<Moe> arrivals = level.getEntitiesOfClass(Moe.class, new AABB(sapling).inflate(4.0D),
                moe -> moe.getVisibleBlockState().is(Blocks.OAK_LOG));
        if (arrivals.size() != 1 || !player.equals(arrivals.getFirst().getDialogueTarget())) {
            helper.fail("Expected one Oak cardinal encounter targeting the planting player, got " + arrivals);
            return;
        }
        if (PlayerProgressionCounters.countItem(level, player, Items.OAK_LOG) != 64) {
            helper.fail("Expected eligibility and spawning to leave the encounter counter intact");
            return;
        }
        new ResetProgressionCountersAction(
                ResetProgressionCountersAction.Kind.ITEM,
                Items.OAK_LOG.builtInRegistryHolder().key().location()).apply(arrivals.getFirst());
        if (PlayerProgressionCounters.countItem(level, player, Items.OAK_LOG) != 0) {
            helper.fail("Expected the Oak encounter to consume the matching progression ledger entries");
            return;
        }
        helper.kill(arrivals.getFirst());
        if (ArrivalService.tryArrival(level, sapling, level.getBlockState(sapling), player)) {
            helper.fail("Expected another Oak encounter to require another 64 collected logs");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void corporealArrivalClaimsSafeMatchingBlockAndReturnsToHide(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID player = new UUID(1991L, 2991L);
        BlockPos trigger = helper.absolutePos(new BlockPos(3, 2, 3));
        BlockPos home = helper.absolutePos(new BlockPos(6, 1, 3));
        BlockPos shrine = helper.absolutePos(new BlockPos(8, 1, 3));
        level.setBlock(trigger.below(), Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(trigger, Blocks.COBBLESTONE.defaultBlockState(), 3);
        level.setBlock(home.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(home, Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(shrine, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ((ShrineTabletBlockEntity) level.getBlockEntity(shrine)).markClaimed(player);
        ArrivalService.recordCollectedItem(level, player, new ItemStack(Items.DIRT), 64);
        var definition = ArrivalReloadListener.parse(ResourceLocation.fromNamespaceAndPath(BlockParty.ID, "test_dirt"),
                JsonParser.parseString("""
                        {"collected":{"item":"minecraft:dirt"},"threshold":64,
                         "placed":{"any":true},"support":{"block":"minecraft:dirt"},
                         "result":"minecraft:dirt","exclusion_radius":0,"home_search_radius":8}
                        """).getAsJsonObject());

        if (!ArrivalService.tryArrival(level, trigger, level.getBlockState(trigger), definition, player)) {
            helper.fail("Expected corporeal arrival to find a safe nearby dirt home");
            return;
        }
        List<Moe> arrivals = level.getEntitiesOfClass(Moe.class, new AABB(home).inflate(1.0D));
        if (arrivals.size() != 1 || arrivals.getFirst().isCardinal()
                || !arrivals.getFirst().getHome().getPos().equals(home)
                || arrivals.getFirst().getRoutineIntent() != RoutineIntent.SLEEP
                || !level.isEmptyBlock(home)) {
            helper.fail("Expected corporeal Moe to emerge from and claim the matching dirt block");
            return;
        }
        Moe moe = arrivals.getFirst();
        moe.routine().updateMovement();
        if (!moe.isRemoved() || !level.getBlockState(home).is(Blocks.DIRT)
                || HidingSpots.get(level).find(home).isEmpty()) {
            helper.fail("Expected resting corporeal Moe to restore and hide in its claimed dirt block");
            return;
        }
        helper.killAllEntitiesOfClass(MoeInHiding.class);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void oreArrivalUsesDropCounterAndNaturalCaveSupport(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID player = new UUID(1993L, 2993L);
        BlockPos torch = helper.absolutePos(new BlockPos(3, 2, 3));
        BlockPos shrine = helper.absolutePos(new BlockPos(6, 1, 3));
        level.setBlock(torch.below(), Blocks.DEEPSLATE.defaultBlockState(), 3);
        level.setBlock(torch, Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(shrine, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ((ShrineTabletBlockEntity) level.getBlockEntity(shrine)).markClaimed(player);
        PlayerProgressionCounters.resetItems(level, player, CustomTags.Items.PROGRESSION_COUNTER_ITEMS);
        SceneVariables.get(level).playerCookies(player).delete(SamuraiProgression.LEGS_OBTAINED);
        ArrivalService.recordCollectedItem(level, player, new ItemStack(Items.COAL), 64);

        if (ArrivalService.tryArrival(level, torch, level.getBlockState(torch), player)) {
            helper.fail("Expected ore cardinal arrival to wait for the samurai legs gate");
            return;
        }
        SceneVariables.get(level).playerCookies(player).set(SamuraiProgression.LEGS_OBTAINED, "true");
        if (!ArrivalService.tryArrival(level, torch, level.getBlockState(torch), player)) {
            helper.fail("Expected ordinary cave-lighting after 64 coal pickups to summon canonical Coal");
            return;
        }
        List<Moe> arrivals = level.getEntitiesOfClass(Moe.class, new AABB(torch).inflate(4.0D),
                moe -> moe.getVisibleBlockState().is(Blocks.COAL_ORE));
        if (arrivals.size() != 1 || !arrivals.getFirst().isCardinal()
                || !player.equals(arrivals.getFirst().getDialogueTarget())) {
            helper.fail("Expected one canonical Coal cardinal selected by its eligible collection counter");
            return;
        }
        helper.kill(arrivals.getFirst());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void cardinalRestPoofsWithoutCreatingHidingBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(3, 1, 3));
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        Moe cardinal = MoeSpawner.spawn(level, pos, Blocks.STONE.defaultBlockState(), new UUID(1992L, 2992L),
                new CompoundTag(), moe -> {});
        if (cardinal == null) {
            helper.fail("Expected cardinal test Moe");
            return;
        }
        cardinal.setBlockState(Blocks.BELL.defaultBlockState());
        cardinal.setRoutineIntent(RoutineIntent.SLEEP);
        long databaseId = cardinal.getDatabaseID();
        if (!cardinal.sleepAtHome(HideUntil.EXPOSED)) {
            helper.fail("Expected cardinal rest operation to succeed");
            return;
        }
        if (!cardinal.isRemoved() || !level.isEmptyBlock(pos) || HidingSpots.get(level).find(pos).isPresent()) {
            helper.fail("Expected resting cardinal Moe to poof without becoming a block");
            return;
        }
        try {
            if (BlockPartyDB.get(level).findNpc(databaseId).isEmpty()) {
                helper.fail("Expected the cardinal identity to remain persisted after poofing");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected persisted cardinal identity lookup: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void oakSaplingDropsRecordAttentionWithoutSummoningVisitor(GameTestHelper helper) {
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

        if (!level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(4.0D), moe ->
                player.equals(moe.getDialogueTarget()) && moe.getVisibleBlockState().is(Blocks.OAK_LOG)).isEmpty()) {
            helper.fail("Expected Oak's migrated planting arrival not to summon from sapling drops");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void repeatOakAttentionDoesNotSummonVisitor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1913L, 2913L);
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, pos, Blocks.OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.OAK_SAPLING)))
                || !Attention.noticeDrops(level, pos.east(), Blocks.OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.OAK_SAPLING)))) {
            helper.fail("Expected repeated oak sapling attention to record");
            return;
        }

        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(8.0D));
        if (!moes.isEmpty()) {
            helper.fail("Expected repeated Oak attention to remain record-only, got " + moes.size());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void birchSaplingDropsUseGeneralizedForestAttention(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SceneVariables.get(level).worldCookies().set(SamuraiProgression.TORII_GATE_OPENED, "true");
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1911L, 2911L);
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }

        if (!Attention.noticeDrops(level, pos, Blocks.BIRCH_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.BIRCH_SAPLING, 2)))) {
            helper.fail("Expected birch sapling drops to record forest attention");
            return;
        }

        try {
            AttentionRecord record = db.findAttention(player, "birch_forest", "sapling_drop").orElse(null);
            if (record == null || !"minecraft:birch_sapling".equals(record.itemId()) || record.itemCount() != 2) {
                helper.fail("Expected birch_forest/sapling_drop attention record with birch sapling item, got " + record);
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected attention lookup to succeed: " + exception.getMessage());
            return;
        }

        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(4.0D));
        if (!moes.isEmpty()) {
            helper.fail("Expected Birch attention to remain record-only after planting-arrival migration");
            return;
        }
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

        Moe moe = spawnOakChoreVisitor(level, origin, player);
        moe.clearDialogue();
        moe.moveToBlock(origin);
        ItemEntity sapling = new ItemEntity(level, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, new ItemStack(Items.OAK_SAPLING));
        level.addFreshEntity(sapling);

        if (!moe.chores().tickActive() || sapling.isAlive()) {
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
        if (!moe.chores().tickActive()) {
            helper.fail("Expected oak visitor to plant the collected sapling");
            return;
        }
        if (!level.getBlockState(origin).is(Blocks.OAK_SAPLING) || moe.chores().hasActive(CardinalForestChore.ID)
                || !moe.getInventory().getItem(0).isEmpty()) {
            helper.fail("Expected oak visitor to finish after planting an oak sapling");
            return;
        }
        if (!SceneVariables.get(level).playerCookies(player).has(WoodFamilyProgression.OAK_REPLENISHMENT_SEEN)) {
            helper.fail("Expected oak replenishment to record player conduct");
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
    public static void darkOakAttentionVisitorPlantsFourSaplings(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        SceneVariables.get(level).worldCookies().set(SamuraiProgression.TORII_GATE_OPENED, "true");
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1912L, 2912L);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 1, 2));
        preparePlantingSquare(level, origin, 2, 8);
        try {
            clearAttention(db);
        } catch (SQLException exception) {
            helper.fail("Expected attention cleanup to succeed: " + exception.getMessage());
            return;
        }
        var cookies = SceneVariables.get(level).playerCookies(player);

        if (!Attention.noticeDrops(level, origin, Blocks.DARK_OAK_LEAVES.defaultBlockState(), player, List.of(new ItemStack(Items.DARK_OAK_SAPLING, 4)))) {
            helper.fail("Expected dark oak sapling attention to start");
            return;
        }
        Moe moe = spawnChoreVisitor(level, origin, player, Blocks.DARK_OAK_LOG.defaultBlockState(),
                PlaceBlockChores.Config.DARK_OAK_SAPLING);
        moe.clearDialogue();
        moe.moveToBlock(origin);
        ItemEntity sapling = new ItemEntity(level, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, new ItemStack(Items.DARK_OAK_SAPLING, 4));
        level.addFreshEntity(sapling);

        if (!moe.chores().tickActive() || sapling.isAlive()) {
            helper.fail("Expected dark oak visitor to collect four dropped saplings");
            return;
        }
        if (!moe.chores().tickActive()) {
            helper.fail("Expected dark oak visitor to plant a two-by-two sapling group");
            return;
        }
        for (BlockPos planted : BlockPos.betweenClosed(origin, origin.offset(1, 0, 1))) {
            if (!level.getBlockState(planted).is(Blocks.DARK_OAK_SAPLING)) {
                helper.fail("Expected dark oak visitor to plant at " + planted);
                return;
            }
        }
        if (!cookies.has(WoodFamilyProgression.DARK_OAK_REPLENISHMENT_SEEN)) {
            helper.fail("Expected dark oak replenishment conduct cookie");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void activeChorePersistsWithTypeKey(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID player = new UUID(1908L, 2908L);
        BlockPos origin = helper.absolutePos(new BlockPos(2, 1, 2));
        level.setBlock(origin.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        clearColumn(level, origin, 6);
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.chores().start(CardinalForestChore.oakSapling(level, origin, player));
        CompoundTag saved = moe.saveWithoutId(new CompoundTag());
        CompoundTag chore = saved.getCompound(Moe.NBT_CHORE);
        if (!CardinalForestChore.ID.toString().equals(chore.getString("Type"))) {
            helper.fail("Expected saved chore type key to be " + CardinalForestChore.ID + ", got " + chore);
            return;
        }
        if (!chore.hasUUID("Player") || !player.equals(chore.getUUID("Player"))) {
            helper.fail("Expected saved chore to preserve player UUID, got " + chore);
            return;
        }

        Moe loaded = new Moe(CustomEntities.MOE.get(), level);
        loaded.load(saved);
        if (!loaded.chores().hasActive(CardinalForestChore.ID)) {
            helper.fail("Expected keyed chore read to restore active cardinal forest chore");
            return;
        }
        loaded.moveToBlock(origin);
        loaded.getInventory().setItem(0, new ItemStack(Items.OAK_SAPLING));
        if (!loaded.chores().tickActive()) {
            helper.fail("Expected restored chore to plant after reload");
            return;
        }
        if (!SceneVariables.get(level).playerCookies(player).has(WoodFamilyProgression.OAK_REPLENISHMENT_SEEN)) {
            helper.fail("Expected restored chore to record player conduct after reload");
            return;
        }
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

        Moe moe = spawnOakChoreVisitor(level, origin, player);
        moe.clearDialogue();
        moe.moveToBlock(spaced);
        moe.getInventory().setItem(0, new ItemStack(Items.OAK_SAPLING));

        if (!moe.chores().tickActive()) {
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

        Moe moe = spawnOakChoreVisitor(level, origin, player);
        moe.clearDialogue();
        moe.moveToBlock(origin);
        BlockPos trappedPos = origin.above(5);
        level.setBlock(trappedPos, Blocks.OAK_LEAVES.defaultBlockState(), 3);
        ItemEntity trappedSapling = new ItemEntity(level, trappedPos.getX() + 0.5D, trappedPos.getY() + 0.5D, trappedPos.getZ() + 0.5D, new ItemStack(Items.OAK_SAPLING));
        level.addFreshEntity(trappedSapling);

        if (moe.chores().tickActive()) {
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

    private static Moe spawnOakChoreVisitor(ServerLevel level, BlockPos origin, UUID player) {
        return spawnChoreVisitor(level, origin, player, Blocks.OAK_LOG.defaultBlockState(),
                PlaceBlockChores.Config.OAK_SAPLING);
    }

    private static Moe spawnChoreVisitor(ServerLevel level, BlockPos origin, UUID player, BlockState state,
                                         PlaceBlockChores.Config config) {
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(origin);
        moe.setBlockState(state);
        moe.setPlayerUUID(player);
        moe.setDialogueTarget(player);
        moe.chores().start(CardinalForestChore.sapling(level, origin, player, config));
        level.addFreshEntity(moe);
        return moe;
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
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + BlockPartyDB.TABLE_ATTENTION_RECORDS + ";")) {
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

    private static void preparePlantingSquare(ServerLevel level, BlockPos origin, int size, int height) {
        for (BlockPos pos : BlockPos.betweenClosed(origin, origin.offset(size - 1, 0, size - 1))) {
            level.setBlock(pos.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            clearColumn(level, pos, height);
        }
    }
}
