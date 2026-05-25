package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import java.sql.SQLException;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeLifecycleGameTests {
    private MoeLifecycleGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void validSpawnEggUseCreatesMoeShell(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        UUID owner = new UUID(11L, 22L);
        BlockState sourceState = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, sourceState, 3);
        int rowsBefore = countNpcRows(helper);

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected valid tagged block to spawn Moe");
            return;
        }
        assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(source), "removed source block");
        assertEquals(helper, rowsBefore + 1, countNpcRows(helper), "NPC row count after valid spawn");
        NPC row = findNpc(helper, moe.getDatabaseID());
        if (row == null) {
            return;
        }
        assertEquals(helper, row.databaseId(), moe.getDatabaseID(), "spawned Moe database ID");
        assertEquals(helper, owner, moe.getOwnerUUID(), "spawned Moe owner UUID");
        assertEquals(helper, owner, row.playerUuid(), "spawned row owner UUID");
        assertEquals(helper, sourceState, moe.getBlockState(), "spawned Moe source block state");
        assertEquals(helper, sourceState, row.blockState(), "spawned row block state");
        assertEquals(helper, helper.absolutePos(new BlockPos(1, 2, 1)), moe.blockPosition(), "spawned Moe position");

        Moe loaded = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        loaded.load(moe.saveWithoutId(new CompoundTag()));
        assertEquals(helper, owner, loaded.getOwnerUUID(), "spawned Moe persisted owner UUID");
        assertEquals(helper, sourceState, loaded.getBlockState(), "spawned Moe persisted source block state");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void uniquePersonalitySpawnReusesExistingVisibleBlockMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(12L, 23L);
        BlockPos firstSource = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos secondSource = helper.absolutePos(new BlockPos(3, 1, 1));
        level.setBlock(firstSource, Blocks.BELL.defaultBlockState(), 3);
        level.setBlock(secondSource, Blocks.BELL.defaultBlockState(), 3);
        int rowsBefore = countNpcRows(helper);

        Moe first = CustomSpawnEggItem.spawnMoe(level, firstSource, Direction.UP, owner);
        Moe second = CustomSpawnEggItem.spawnMoe(level, secondSource, Direction.UP, owner);
        if (first == null || second == null) {
            helper.fail("Expected tagged unique bell personalities to spawn");
            return;
        }

        assertEquals(helper, first.getDatabaseID(), second.getDatabaseID(), "unique personality database ID");
        assertUnchangedOrOneNewRow(helper, rowsBefore, countNpcRows(helper), "duplicate unique personality spawn");
        assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(firstSource), "first unique source removed");
        assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(secondSource), "second unique source removed");
        assertEquals(helper, helper.absolutePos(new BlockPos(3, 2, 1)), second.blockPosition(), "unique Moe moved to second spawn position");
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(firstSource).inflate(8.0),
                moe -> moe.getDatabaseID() == first.getDatabaseID());
        assertEquals(helper, 1, moes.size(), "loaded unique personality entity count");
        assertEquals(helper, 1, countOwnerListEntries(BlockPartyDB.get(level), owner, second.getDatabaseID()), "unique personality relationship entries");
        helper.kill(second);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void individualPersonalitySpawnCreatesSeparateRows(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(13L, 24L);
        BlockPos firstSource = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos secondSource = helper.absolutePos(new BlockPos(3, 1, 1));
        BlockState sourceState = CustomBlocks.SAKURA_PLANKS.get().defaultBlockState();
        level.setBlock(firstSource, sourceState, 3);
        level.setBlock(secondSource, sourceState, 3);
        int rowsBefore = countNpcRows(helper);

        Moe first = CustomSpawnEggItem.spawnMoe(level, firstSource, Direction.UP, owner);
        Moe second = CustomSpawnEggItem.spawnMoe(level, secondSource, Direction.UP, owner);
        if (first == null || second == null) {
            helper.fail("Expected individual personalities to spawn");
            return;
        }

        if (first.getDatabaseID() == second.getDatabaseID()) {
            helper.fail("Expected individual personality spawns to create separate database IDs");
            return;
        }
        assertEquals(helper, rowsBefore + 2, countNpcRows(helper), "NPC row count after individual personality spawn");
        helper.kill(first);
        helper.kill(second);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void invalidSpawnEggUseFailsSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockState invalidState = Blocks.BEDROCK.defaultBlockState();
        level.setBlock(source, invalidState, 3);
        int rowsBefore = countNpcRows(helper);

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, new UUID(33L, 44L));
        if (moe != null) {
            helper.fail("Expected invalid block to produce no Moe");
            return;
        }
        assertEquals(helper, invalidState, level.getBlockState(source), "invalid source block state");
        assertEquals(helper, rowsBefore, countNpcRows(helper), "NPC row count after invalid spawn helper");
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(source).inflate(3.0));
        if (!moes.isEmpty()) {
            helper.fail("Expected invalid spawn to leave no nearby Moe entities");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void invalidSpawnEggUseChangesNoBlockConsumesNoItemAndInsertsNoRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockState invalidState = Blocks.BEDROCK.defaultBlockState();
        level.setBlock(source, invalidState, 3);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(block_party.registry.CustomItems.MOE_SPAWN_EGG.get());
        int rowsBefore = countNpcRows(helper);

        InteractionResult result = useSpawnEgg(level, player, stack, source, Direction.UP);

        assertEquals(helper, InteractionResult.FAIL, result, "invalid use result");
        assertEquals(helper, invalidState, level.getBlockState(source), "invalid use source block");
        assertEquals(helper, 1, stack.getCount(), "invalid use item count");
        assertEquals(helper, rowsBefore, countNpcRows(helper), "NPC row count after invalid item use");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void survivalSpawnEggUseConsumesItem(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(source, CustomBlocks.SAKURA_PLANKS.get().defaultBlockState(), 3);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(block_party.registry.CustomItems.MOE_SPAWN_EGG.get());

        InteractionResult result = useSpawnEgg(level, player, stack, source, Direction.UP);

        assertEquals(helper, InteractionResult.CONSUME, result, "survival use result");
        assertEquals(helper, 0, stack.getCount(), "survival spawn egg count");
        killNearbyMoes(level, source);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void creativeSpawnEggUseDoesNotConsumeItem(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(source, CustomBlocks.GINKGO_PLANKS.get().defaultBlockState(), 3);
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        ItemStack stack = new ItemStack(block_party.registry.CustomItems.MOE_SPAWN_EGG.get());

        InteractionResult result = useSpawnEgg(level, player, stack, source, Direction.UP);

        assertEquals(helper, InteractionResult.CONSUME, result, "creative use result");
        assertEquals(helper, 1, stack.getCount(), "creative spawn egg count");
        killNearbyMoes(level, source);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blockEntityPersistentDataIsCapturedOnSpawn(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(source, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity blockEntity = level.getBlockEntity(source);
        if (blockEntity == null) {
            helper.fail("Expected chest block entity setup");
            return;
        }
        blockEntity.getPersistentData().putString("BlockPartyTestValue", "captured");

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, new UUID(99L, 101L));
        if (moe == null) {
            helper.fail("Expected chest to spawn Moe from block tag");
            return;
        }

        assertEquals(helper, "captured", moe.getTileEntityData().getString("BlockPartyTestValue"), "spawned Moe block entity data");
        Moe loaded = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        loaded.load(moe.saveWithoutId(new CompoundTag()));
        assertEquals(helper, "captured", loaded.getTileEntityData().getString("BlockPartyTestValue"), "persisted Moe block entity data");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownerListReceivesOneEntryAndDoesNotDuplicate(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        UUID owner = new UUID(202L, 303L);
        level.setBlock(source, CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected owned spawn to succeed");
            return;
        }
        db.addTo(owner, moe.getDatabaseID());

        int count = 0;
        for (long id : db.listNpcIds(owner)) {
            if (id == moe.getDatabaseID()) {
                ++count;
            }
        }
        assertEquals(helper, 1, count, "owner list entries for spawned NPC");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hideCreatesBlockHiddenEntityAndHidingSpot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(55L, 66L);
        BlockState sourceState = CustomBlocks.SAKURA_PLANKS.get().defaultBlockState();
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.moveToBlock(pos);
        moe.setOwnerUUID(owner);
        moe.setBlockState(sourceState);
        NPC row = createNpc(helper, level, moe);
        if (row == null) {
            return;
        }
        row.applyTo(moe);
        level.addFreshEntity(moe);

        MoeInHiding hidden = moe.hide(HideUntil.ONE_SECOND_PASSES);
        if (hidden == null) {
            helper.fail("Expected Moe hide to create a MoeInHiding shell");
            return;
        }
        assertEquals(helper, sourceState, level.getBlockState(pos), "hidden block state");
        assertEquals(helper, row.databaseId(), hidden.getDatabaseID(), "hidden database ID");
        assertEquals(helper, owner, hidden.getOwnerUUID(), "hidden owner UUID");
        assertEquals(helper, pos, hidden.getAttachPos(), "hidden attach position");
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, hidden.getHideUntil(), "hidden HideUntil");
        assertLong(helper, row.databaseId(), HidingSpots.get(level).find(pos), "HidingSpots record");
        NPC updated = findNpc(helper, row.databaseId());
        if (updated == null) {
            return;
        }
        assertEquals(helper, true, updated.hiding(), "hidden row hiding flag");
        assertEquals(helper, pos, updated.hiddenPos(), "hidden row position");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void corporealDeathHidesMoeInsteadOfLosingRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(551L, 661L);
        BlockState sourceState = CustomBlocks.SAKURA_PLANKS.get().defaultBlockState();
        Moe moe = createPersistedMoe(helper, level, pos, owner, sourceState);
        if (moe == null) {
            return;
        }
        moe.setCorporeal(true);
        long databaseId = moe.getDatabaseID();

        moe.hurtServer(level, level.damageSources().generic(), 1000.0F);

        assertEquals(helper, sourceState, level.getBlockState(pos), "corporeal death hidden block state");
        assertLong(helper, databaseId, HidingSpots.get(level).find(pos), "corporeal death HidingSpots record");
        NPC updated = findNpc(helper, databaseId);
        if (updated == null) {
            return;
        }
        assertEquals(helper, true, updated.hiding(), "corporeal death row hiding flag");
        assertEquals(helper, false, updated.dead(), "corporeal death row dead flag");
        if (findMoe(level, databaseId, pos) != null) {
            helper.fail("Expected corporeal death to remove the live Moe shell");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void etherealDeathDoesNotCreateHiddenSpot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        Moe moe = createPersistedMoe(helper, level, pos, new UUID(552L, 662L), CustomBlocks.GINKGO_PLANKS.get().defaultBlockState());
        if (moe == null) {
            return;
        }
        moe.setCorporeal(false);
        long databaseId = moe.getDatabaseID();

        moe.hurtServer(level, level.damageSources().generic(), 1000.0F);

        assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(pos), "ethereal death block state");
        if (HidingSpots.get(level).find(pos).isPresent()) {
            helper.fail("Expected ethereal death not to record a hiding spot");
            return;
        }
        NPC updated = findNpc(helper, databaseId);
        if (updated == null) {
            return;
        }
        assertEquals(helper, false, updated.hiding(), "ethereal death row hiding flag");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void deathDropsMoeInventoryContents(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        Moe moe = createPersistedMoe(helper, level, pos, new UUID(553L, 663L), CustomBlocks.GINKGO_PLANKS.get().defaultBlockState());
        if (moe == null) {
            return;
        }
        moe.setCorporeal(false);
        moe.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));

        moe.hurtServer(level, level.damageSources().generic(), 1000.0F);

        List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(4.0),
                item -> item.getItem().is(Items.DIAMOND));
        if (drops.size() != 1) {
            helper.fail("Expected one diamond inventory drop, got " + drops.size());
            return;
        }
        assertEquals(helper, 3, drops.getFirst().getItem().getCount(), "diamond inventory drop count");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void manualRevealRestoresSameIdentityShell(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(77L, 88L);
        BlockState sourceState = CustomBlocks.GINKGO_PLANKS.get().defaultBlockState();
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.moveToBlock(pos);
        moe.setOwnerUUID(owner);
        moe.setBlockState(sourceState);
        NPC row = createNpc(helper, level, moe);
        if (row == null) {
            return;
        }
        row.applyTo(moe);
        level.addFreshEntity(moe);
        MoeInHiding hidden = moe.hide(HideUntil.EXPOSED);
        if (hidden == null) {
            helper.fail("Expected hidden shell before reveal");
            return;
        }

        Moe revealed = HidingSpots.reveal(level, pos);
        if (revealed == null) {
            helper.fail("Expected manual reveal to restore Moe shell");
            return;
        }
        assertEquals(helper, row.databaseId(), revealed.getDatabaseID(), "revealed database ID");
        assertEquals(helper, owner, revealed.getOwnerUUID(), "revealed owner UUID");
        assertEquals(helper, sourceState, revealed.getBlockState(), "revealed block state");
        NPC updated = findNpc(helper, row.databaseId());
        if (updated == null) {
            return;
        }
        assertEquals(helper, false, updated.hiding(), "revealed row hiding flag");
        if (HidingSpots.get(level).find(pos).isPresent()) {
            helper.fail("Expected reveal to clear HidingSpots record");
            return;
        }
        helper.kill(revealed);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void timedRevealRestoresSameIdentityShell(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(771L, 881L);
        BlockState sourceState = CustomBlocks.GINKGO_PLANKS.get().defaultBlockState();
        Moe moe = createPersistedMoe(helper, level, pos, owner, sourceState);
        if (moe == null) {
            return;
        }
        MoeInHiding hidden = moe.hide(HideUntil.ONE_SECOND_PASSES);
        if (hidden == null) {
            helper.fail("Expected hidden shell before timed reveal");
            return;
        }
        long databaseId = hidden.getDatabaseID();

        helper.runAfterDelay(30, () -> {
            if (!hidden.isRemoved()) {
                helper.fail("Expected timed hidden marker to be removed after reveal");
                return;
            }
            Moe revealed = findMoe(level, databaseId, pos);
            if (revealed == null) {
                helper.fail("Expected timed reveal to restore Moe shell");
                return;
            }
            assertEquals(helper, owner, revealed.getOwnerUUID(), "timed revealed owner UUID");
            assertEquals(helper, sourceState, revealed.getBlockState(), "timed revealed source block state");
            assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(pos), "timed revealed source block removal");
            helper.kill(revealed);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void breakStartEventRevealsHiddenMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        MoeInHiding hidden = createHiddenMoe(helper, level, pos, HideUntil.EXPOSED);
        if (hidden == null) {
            return;
        }
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        HidingSpots.onBreakStart(new PlayerInteractEvent.LeftClickBlock(
                player, pos, Direction.UP, PlayerInteractEvent.LeftClickBlock.Action.START));

        assertRevealedFromEvent(helper, level, pos, hidden.getDatabaseID(), "break start");
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void breakEndEventRevealsHiddenMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        MoeInHiding hidden = createHiddenMoe(helper, level, pos, HideUntil.EXPOSED);
        if (hidden == null) {
            return;
        }
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        HidingSpots.onBreakEnd(new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player));

        assertRevealedFromEvent(helper, level, pos, hidden.getDatabaseID(), "break end");
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void pistonPreEventRevealsHiddenMoeInFrontOfPiston(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos piston = helper.absolutePos(new BlockPos(1, 1, 2));
        BlockPos pos = piston.east();
        level.setBlock(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.EAST), 3);
        MoeInHiding hidden = createHiddenMoe(helper, level, pos, HideUntil.EXPOSED);
        if (hidden == null) {
            return;
        }

        HidingSpots.onPistonPush(new PistonEvent.Pre(level, piston, Direction.EAST, PistonEvent.PistonMoveType.EXTEND));

        assertRevealedFromEvent(helper, level, pos, hidden.getDatabaseID(), "piston pre");
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void fallingBlockJoinEventRevealsAndCancelsHiddenMoeBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        MoeInHiding hidden = createHiddenMoe(helper, level, pos, HideUntil.EXPOSED);
        if (hidden == null) {
            return;
        }
        FallingBlockEntity falling = new FallingBlockEntity(EntityType.FALLING_BLOCK, level);
        falling.setStartPos(pos);
        EntityJoinLevelEvent event = new EntityJoinLevelEvent(falling, level);

        HidingSpots.onFalling(event);

        if (!event.isCanceled()) {
            helper.fail("Expected falling block join event to be canceled after reveal");
            return;
        }
        assertRevealedFromEvent(helper, level, pos, hidden.getDatabaseID(), "falling block");
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void blockEntityPersistentDataSurvivesHideReveal(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(909L, 1001L);
        Moe moe = createPersistedMoe(helper, level, pos, owner, Blocks.CHEST.defaultBlockState());
        if (moe == null) {
            return;
        }
        CompoundTag tileEntity = new CompoundTag();
        tileEntity.putString("BlockPartyHideRevealValue", "round-trip");
        moe.setTileEntityData(tileEntity);

        MoeInHiding hidden = moe.hide(HideUntil.EXPOSED);
        if (hidden == null) {
            helper.fail("Expected hide with block entity data to succeed");
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            helper.fail("Expected hidden chest block entity");
            return;
        }
        assertEquals(helper, "round-trip", blockEntity.getPersistentData().getString("BlockPartyHideRevealValue"), "hidden block entity data");

        Moe revealed = HidingSpots.reveal(level, pos);
        if (revealed == null) {
            helper.fail("Expected reveal with block entity data to succeed");
            return;
        }
        assertEquals(helper, "round-trip", revealed.getTileEntityData().getString("BlockPartyHideRevealValue"), "revealed Moe block entity data");
        helper.kill(revealed);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingRowRevealFailsSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        long missingId = Long.MAX_VALUE - 123L;
        level.setBlock(pos, CustomBlocks.GINKGO_PLANKS.get().defaultBlockState(), 3);

        MoeInHiding hidden = new MoeInHiding(block_party.registry.CustomEntities.MOE_IN_HIDING.get(), level);
        hidden.setDatabaseID(missingId);
        hidden.setAttachPos(pos);
        hidden.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(hidden);
        HidingSpots.get(level).put(pos, missingId);

        Moe revealed = HidingSpots.reveal(level, pos);
        if (revealed != null) {
            helper.fail("Expected reveal with missing SQLite row to no-op");
            return;
        }
        if (!hidden.isAlive()) {
            helper.fail("Expected hidden marker to remain when row-backed reveal fails");
            return;
        }
        helper.kill(hidden);
        HidingSpots.get(level).remove(pos);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingHiddenSpotRevealNoOps(GameTestHelper helper) {
        Moe revealed = HidingSpots.reveal(helper.getLevel(), helper.absolutePos(new BlockPos(4, 1, 4)));
        if (revealed != null) {
            helper.fail("Expected missing hidden spot reveal to no-op");
            return;
        }
        helper.succeed();
    }

    private static void assertLong(GameTestHelper helper, long expected, OptionalLong actual, String label) {
        if (actual.isEmpty() || actual.getAsLong() != expected) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertRevealedFromEvent(GameTestHelper helper, ServerLevel level, BlockPos pos, long databaseId, String label) {
        Moe revealed = findMoe(level, databaseId, pos);
        if (revealed == null) {
            helper.fail("Expected " + label + " event to reveal Moe " + databaseId);
            return;
        }
        assertEquals(helper, Blocks.AIR.defaultBlockState(), level.getBlockState(pos), label + " revealed block state");
        if (HidingSpots.get(level).find(pos).isPresent()) {
            helper.fail("Expected " + label + " event to clear HidingSpots record");
            return;
        }
        helper.kill(revealed);
        helper.succeed();
    }

    private static MoeInHiding createHiddenMoe(GameTestHelper helper, ServerLevel level, BlockPos pos, HideUntil hideUntil) {
        Moe moe = createPersistedMoe(helper, level, pos, new UUID(pos.asLong(), pos.asLong() + 1L),
                CustomBlocks.SAKURA_PLANKS.get().defaultBlockState());
        if (moe == null) {
            return null;
        }
        MoeInHiding hidden = moe.hide(hideUntil);
        if (hidden == null) {
            helper.fail("Expected hidden Moe setup to succeed");
        }
        return hidden;
    }

    private static Moe createPersistedMoe(GameTestHelper helper, ServerLevel level, BlockPos pos, UUID owner, BlockState sourceState) {
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.moveToBlock(pos);
        moe.setOwnerUUID(owner);
        moe.setBlockState(sourceState);
        NPC row = createNpc(helper, level, moe);
        if (row == null) {
            return null;
        }
        row.applyTo(moe);
        if (!level.addFreshEntity(moe)) {
            helper.fail("Expected persisted Moe setup entity to spawn");
            return null;
        }
        return moe;
    }

    private static Moe findMoe(ServerLevel level, long databaseId, BlockPos pos) {
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(pos).inflate(1.0), moe -> moe.getDatabaseID() == databaseId);
        return moes.isEmpty() ? null : moes.getFirst();
    }

    private static NPC createNpc(GameTestHelper helper, ServerLevel level, Moe moe) {
        try {
            return BlockPartyDB.get(level).createNpc(level, moe);
        } catch (SQLException exception) {
            helper.fail("Expected NPC row creation to succeed: " + exception.getMessage());
            return null;
        }
    }

    private static NPC findNpc(GameTestHelper helper, long id) {
        try {
            return BlockPartyDB.get(helper.getLevel()).findNpc(id).orElseGet(() -> {
                helper.fail("Expected NPC row " + id + " to exist");
                return null;
            });
        } catch (SQLException exception) {
            helper.fail("Expected NPC row lookup to succeed: " + exception.getMessage());
            return null;
        }
    }

    private static InteractionResult useSpawnEgg(ServerLevel level, Player player, ItemStack stack, BlockPos source, Direction face) {
        Vec3 hitLocation = Vec3.atCenterOf(source);
        BlockHitResult hit = new BlockHitResult(hitLocation, face, source, false);
        return block_party.registry.CustomItems.MOE_SPAWN_EGG.get().useOn(
                new net.minecraft.world.item.context.UseOnContext(level, player, InteractionHand.MAIN_HAND, stack, hit));
    }

    private static int countNpcRows(GameTestHelper helper) {
        BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
        try {
            Connection connection = db.openConnection();
            try (ResultSet result = connection.createStatement().executeQuery("SELECT COUNT(*) FROM NPCs;")) {
                return result.next() ? result.getInt(1) : 0;
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected NPC row count to succeed: " + exception.getMessage());
            return -1;
        }
    }

    private static int countOwnerListEntries(BlockPartyDB db, UUID owner, long databaseId) {
        int count = 0;
        for (long id : db.getFrom(owner)) {
            if (id == databaseId) {
                ++count;
            }
        }
        return count;
    }

    private static void assertUnchangedOrOneNewRow(GameTestHelper helper, int before, int after, String label) {
        if (after != before && after != before + 1) {
            helper.fail("Expected " + label + " to reuse an existing row or create one row, started with "
                    + before + " and got " + after);
        }
    }

    private static void killNearbyMoes(ServerLevel level, BlockPos source) {
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(source).inflate(3.0));
        moes.forEach(Moe::discard);
    }
}
