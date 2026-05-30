package block_party.gametest;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.goals.HideUntil;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomEntities;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.SQLException;
import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class EntityDataGameTests {
    private EntityDataGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeEntityTypeCanSpawn(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        if (moe == null || !moe.isAlive()) {
            helper.fail("Expected block_party:moe to spawn");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeIdentityStateRoundTrips(GameTestHelper helper) {
        UUID owner = new UUID(123L, 456L);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setDatabaseID(42L);
        moe.setOwnerUUID(owner);
        moe.setBlockState(Blocks.STONE.defaultBlockState());
        moe.setVisibleBlockState(Blocks.BELL.defaultBlockState());
        moe.setMoeScale(0.75F);
        moe.setFollowing(true);
        moe.setSitting(true);
        moe.setGivenName("Moe");
        moe.setGender("NONBINARY");
        moe.setBloodType("AB");
        moe.setDere("KUUDERE");
        moe.setZodiac("PISCES");
        moe.setEmotion("HAPPY");
        moe.setFoodLevel(12.5F);
        moe.setExhaustion(1.5F);
        moe.setSaturation(4.5F);
        moe.setStress(2.5F);
        moe.setRelaxation(3.5F);
        moe.setLoyalty(8.5F);
        moe.setAffection(9.5F);
        moe.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
        moe.setSlouch(0.25F);
        moe.setAge(2.0F);
        moe.setLastSeen(123456789L);
        moe.setTimeUntilHungry(11);
        moe.setTimeUntilLonely(12);
        moe.setTimeUntilStress(13);
        moe.setTimeSinceSleep(14);
        moe.setHasHome(true);
        moe.setHome(new DimBlockPos(Level.OVERWORLD, new BlockPos(9, 10, 11)));
        moe.setRoutineIntent(RoutineIntent.CHORE);

        Moe loaded = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        loaded.load(moe.saveWithoutId(new CompoundTag()));

        assertEquals(helper, 42L, loaded.getDatabaseID(), "Moe database ID");
        assertEquals(helper, owner, loaded.getOwnerUUID(), "Moe owner UUID");
        assertEquals(helper, Blocks.STONE.defaultBlockState(), loaded.getBlockState(), "Moe block state");
        assertEquals(helper, Blocks.STONE.defaultBlockState(), loaded.getActualBlockState(), "Moe actual block state");
        assertEquals(helper, Blocks.BELL.defaultBlockState(), loaded.getVisibleBlockState(), "Moe visible block state");
        assertFloat(helper, 0.75F, loaded.getMoeScale(), "Moe scale");
        assertEquals(helper, false, loaded.isCorporeal(), "Moe corporeal trait");
        assertEquals(helper, true, loaded.isCardinal(), "Moe cardinal trait");
        assertEquals(helper, true, loaded.isFollowing(), "Moe following flag");
        assertEquals(helper, true, loaded.isSitting(), "Moe sitting flag");
        assertEquals(helper, "Moe", loaded.getGivenName(), "Moe given name");
        assertEquals(helper, "NONBINARY", loaded.getGender(), "Moe gender");
        assertEquals(helper, "AB", loaded.getBloodType(), "Moe blood type");
        assertEquals(helper, "KUUDERE", loaded.getDere(), "Moe dere");
        assertEquals(helper, "PISCES", loaded.getZodiac(), "Moe zodiac");
        assertEquals(helper, "HAPPY", loaded.getEmotion(), "Moe emotion");
        assertFloat(helper, 12.5F, loaded.getFoodLevel(), "Moe food level");
        assertFloat(helper, 1.5F, loaded.getExhaustion(), "Moe exhaustion");
        assertFloat(helper, 4.5F, loaded.getSaturation(), "Moe saturation");
        assertFloat(helper, 2.5F, loaded.getStress(), "Moe stress");
        assertFloat(helper, 3.5F, loaded.getRelaxation(), "Moe relaxation");
        assertFloat(helper, 8.5F, loaded.getLoyalty(), "Moe loyalty");
        assertFloat(helper, 9.5F, loaded.getAffection(), "Moe affection");
        assertFloat(helper, 0.25F, loaded.getSlouch(), "Moe slouch");
        assertFloat(helper, 2.0F, loaded.getAge(), "Moe age");
        assertEquals(helper, 123456789L, loaded.getLastSeen(), "Moe last seen");
        assertEquals(helper, 11, loaded.getTimeUntilHungry(), "Moe time until hungry");
        assertEquals(helper, 12, loaded.getTimeUntilLonely(), "Moe time until lonely");
        assertEquals(helper, 13, loaded.getTimeUntilStress(), "Moe time until stress");
        assertEquals(helper, 14, loaded.getTimeSinceSleep(), "Moe time since sleep");
        assertEquals(helper, true, loaded.hasHome(), "Moe has home");
        assertEquals(helper, new BlockPos(9, 10, 11), loaded.getHome().getPos(), "Moe home pos");
        assertEquals(helper, RoutineIntent.CHORE, loaded.getRoutineIntent(), "Moe routine intent");
        assertEquals(helper, Items.DIAMOND, loaded.getInventory().getItem(0).getItem(), "Moe inventory item");
        assertEquals(helper, 3, loaded.getInventory().getItem(0).getCount(), "Moe inventory count");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeWritesLegacyLayerNbtKeys(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setHome(new DimBlockPos(Level.OVERWORLD, new BlockPos(1, 2, 3)));
        moe.startFollowSession(new UUID(100L, 200L), PlayerMovementIntent.PARTY_INVITE, 123, true, false);
        CompoundTag tag = moe.saveWithoutId(new CompoundTag());
        assertHasKey(helper, tag, Moe.NBT_DATABASE_ID);
        assertHasKey(helper, tag, Moe.NBT_BLOCK_STATE);
        assertHasKey(helper, tag, Moe.NBT_SCALE);
        assertHasKey(helper, tag, Moe.NBT_TILE_ENTITY);
        assertHasKey(helper, tag, Moe.NBT_FOLLOWING);
        assertHasKey(helper, tag, Moe.NBT_SITTING);
        assertHasKey(helper, tag, Moe.NBT_OWNER_UUID);
        assertHasKey(helper, tag, Moe.NBT_GIVEN_NAME);
        assertHasKey(helper, tag, Moe.NBT_GENDER);
        assertHasKey(helper, tag, Moe.NBT_BLOOD_TYPE);
        assertHasKey(helper, tag, Moe.NBT_DERE);
        assertHasKey(helper, tag, Moe.NBT_ZODIAC);
        assertHasKey(helper, tag, Moe.NBT_EMOTION);
        assertHasKey(helper, tag, Moe.NBT_FOOD_LEVEL);
        assertHasKey(helper, tag, Moe.NBT_EXHAUSTION);
        assertHasKey(helper, tag, Moe.NBT_SATURATION);
        assertHasKey(helper, tag, Moe.NBT_STRESS);
        assertHasKey(helper, tag, Moe.NBT_RELAXATION);
        assertHasKey(helper, tag, Moe.NBT_LOYALTY);
        assertHasKey(helper, tag, Moe.NBT_AFFECTION);
        assertHasKey(helper, tag, Moe.NBT_SLOUCH);
        assertHasKey(helper, tag, Moe.NBT_AGE);
        assertHasKey(helper, tag, Moe.NBT_HAS_HOME);
        assertHasKey(helper, tag, Moe.NBT_HOME);
        assertHasKey(helper, tag, Moe.NBT_ROUTINE_INTENT);
        assertHasKey(helper, tag, Moe.NBT_TIME_UNTIL_HUNGRY);
        assertHasKey(helper, tag, Moe.NBT_TIME_UNTIL_LONELY);
        assertHasKey(helper, tag, Moe.NBT_TIME_UNTIL_STRESS);
        assertHasKey(helper, tag, Moe.NBT_TIME_SINCE_SLEEP);
        assertHasKey(helper, tag, Moe.NBT_INVENTORY);
        assertHasKey(helper, tag, Moe.NBT_FOLLOW_SESSION);
        CompoundTag follow = tag.getCompound(Moe.NBT_FOLLOW_SESSION);
        assertHasKey(helper, follow, Moe.NBT_FOLLOW_PLAYER_UUID);
        assertHasKey(helper, follow, Moe.NBT_FOLLOW_INTENT);
        assertHasKey(helper, follow, Moe.NBT_FOLLOW_TICKS_REMAINING);
        assertHasKey(helper, follow, Moe.NBT_FOLLOW_CAN_CHANGE_DIMENSION);
        assertEquals(helper, "00000000-0000-0064-0000-0000000000c8", follow.getString(Moe.NBT_FOLLOW_PLAYER_UUID), "Moe follow player UUID");
        assertEquals(helper, PlayerMovementIntent.PARTY_INVITE.name(), follow.getString(Moe.NBT_FOLLOW_INTENT), "Moe follow intent");
        assertEquals(helper, 123, follow.getInt(Moe.NBT_FOLLOW_TICKS_REMAINING), "Moe follow ticks remaining");
        assertEquals(helper, true, follow.getBoolean(Moe.NBT_FOLLOW_CAN_CHANGE_DIMENSION), "Moe follow dimension flag");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeHasForgeMovementCombatAttributes(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        assertFloat(helper, 20.0F, (float) moe.getAttributeValue(Attributes.MAX_HEALTH), "max health attribute");
        assertFloat(helper, 2.0F, (float) moe.getAttributeValue(Attributes.ATTACK_DAMAGE), "attack damage attribute");
        assertFloat(helper, 0.25F, (float) moe.getAttributeValue(Attributes.MOVEMENT_SPEED), "movement speed attribute");
        assertFloat(helper, 2.0F, (float) moe.getAttributeValue(Attributes.ATTACK_SPEED), "attack speed attribute");
        assertFloat(helper, 1.6F, (float) moe.getAttributeValue(Attributes.FLYING_SPEED), "flying speed attribute");
        assertFloat(helper, 256.0F, (float) moe.getAttributeValue(Attributes.FOLLOW_RANGE), "follow range attribute");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeCombatDelegatesWithoutRecursing(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Moe attacker = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        Moe target = helper.spawn(CustomEntities.MOE.get(), 1, 1, 0);
        float health = target.getHealth();
        if (!attacker.doHurtTarget(level, target)) {
            helper.fail("Expected Moe attack to delegate to the vanilla mob combat hook");
            return;
        }
        if (!(target.getHealth() < health)) {
            helper.fail("Expected delegated Moe attack to damage the target");
            return;
        }
        helper.kill(attacker);
        helper.kill(target);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeSoundHooksResolveDefaultsAndOverrides(GameTestHelper helper) {
        Moe bell = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        bell.setBlockState(Blocks.BELL.defaultBlockState());
        assertSound(helper, bell.getStepSound(), "block_party:moe.bell.step", "bell step override");
        assertSound(helper, bell.getAttackSound(), "block_party:moe.attack", "attack sound");
        assertSound(helper, bell.getHurtSound(helper.getLevel().damageSources().generic()), "block_party:moe.hurt", "hurt sound");
        assertSound(helper, bell.getDeathSound(), "block_party:moe.dead", "death sound");
        assertSound(helper, bell.getSpeakSound(), "block_party:moe.say", "speak sound");

        Moe cat = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        cat.setBlockState(Blocks.ANDESITE.defaultBlockState());
        assertSound(helper, cat.getAmbientSound(), "block_party:moe.meow", "cat ambient sound");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void sourceBlockFlammabilityDrivesFireImmunity(GameTestHelper helper) {
        Moe stone = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        stone.setBlockState(Blocks.STONE.defaultBlockState());
        assertEquals(helper, true, stone.fireImmune(), "non-flammable source block fire immunity");

        Moe log = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        log.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        assertEquals(helper, false, log.fireImmune(), "flammable source block fire immunity");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeHomeAndMoveHelpersUseBlockPositions(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 3));
        moe.moveToBlock(pos);
        moe.setHomeToCurrentPosition();
        assertEquals(helper, pos, moe.blockPosition(), "Moe moved block position");
        assertEquals(helper, true, moe.hasHome(), "Moe home flag");
        assertEquals(helper, pos, moe.getHome().getPos(), "Moe home helper position");
        assertEquals(helper, helper.getLevel().dimension(), moe.getHome().getDim(), "Moe home helper dimension");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void inventoryChangesRecalculateSlouch(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        assertFloat(helper, 0.0F, moe.getSlouch(), "empty inventory slouch");
        moe.getInventory().setItem(0, new ItemStack(Items.STONE));
        assertFloat(helper, 0.0277777778F, moe.getSlouch(), "one occupied slot slouch");
        moe.getInventory().setItem(1, new ItemStack(Items.DIRT));
        assertFloat(helper, 0.0555555556F, moe.getSlouch(), "two occupied slots slouch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void layerFourHumanizingHelpersRemainAvailable(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setMoeScale(0.8F);
        moe.setFoodLevel(10.0F);
        moe.setExhaustion(1.0F);
        moe.setSaturation(2.0F);
        moe.setStress(3.0F);
        moe.setRelaxation(4.0F);
        moe.setLoyalty(5.0F);
        moe.setAffection(6.0F);
        moe.setAge(7.0F);

        moe.addFoodLevel(1.0F);
        moe.addExhaustion(1.0F);
        moe.addSaturation(1.0F);
        moe.addStress(1.0F);
        moe.addRelaxation(1.0F);
        moe.addLoyalty(1.0F);
        moe.addAffection(1.0F);
        moe.addAge(1.0F);

        assertFloat(helper, 11.0F, moe.getFoodLevel(), "add food");
        assertFloat(helper, 2.0F, moe.getExhaustion(), "add exhaustion");
        assertFloat(helper, 3.0F, moe.getSaturation(), "add saturation");
        assertFloat(helper, 4.0F, moe.getStress(), "add stress");
        assertFloat(helper, 5.0F, moe.getRelaxation(), "add relaxation");
        assertFloat(helper, 6.0F, moe.getLoyalty(), "add loyalty");
        assertFloat(helper, 7.0F, moe.getAffection(), "add affection");
        assertFloat(helper, 8.0F, moe.getAge(), "add age");
        moe.setBlockState(Blocks.BELL.defaultBlockState());
        assertEquals(helper, "Suzu", moe.getFamilyName(), "family name");
        assertEquals(helper, "Suzu", moe.getFamilyNameComponent().getString(), "family name component");
        assertEquals(helper, "Suzu", moe.getDisplayName().getString(), "cardinal display name");

        Moe corporeal = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        corporeal.setBlockState(Blocks.DIRT.defaultBlockState());
        corporeal.setGivenName("Akemi");
        assertEquals(helper, "Akemi", corporeal.getDisplayName().getString(), "corporeal display name");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void layerSixMenuHooksRemainAvailable(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        moe.setPlayer(player);

        AbstractContainerMenu menu = moe.createMenu(1, player.getInventory(), player);
        if (!(menu instanceof ChestMenu chest) || !chest.getContainer().equals(moe.getInventory())) {
            helper.fail("Expected Moe chest menu hook to expose the Moe inventory");
            return;
        }
        if (moe.openSpecialMenuFor(player)) {
            helper.fail("Expected default special menu hook to remain false");
            return;
        }
        assertEquals(helper, player.getUUID(), moe.getOwnerUUID(), "setPlayer owner UUID");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blockAliasesAndVolumeScaleApplyFromSourceBlock(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setBlockState(CustomBlocks.ENTRIES.get("sakura_wood").get().defaultBlockState());
        assertEquals(helper, CustomBlocks.ENTRIES.get("sakura_wood").get().defaultBlockState(), moe.getActualBlockState(), "actual source block state");
        assertEquals(helper, CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), moe.getVisibleBlockState(), "aliased visible block state");
        assertFloat(helper, 0.9375F, moe.getMoeScale(), "full block volume scale");

        Moe air = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        air.setBlockState(Blocks.AIR.defaultBlockState());
        assertFloat(helper, 0.9375F, air.getMoeScale(), "empty shape fallback scale");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void npcRowPersistsLayerTwoThroughFiveFields(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        db.configureDatabase(level.getServer());
        try {
            NPC.createTable(db);
        } catch (SQLException exception) {
            helper.fail("Expected NPC table setup to succeed: " + exception.getMessage());
            return;
        }

        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(helper.absolutePos(new BlockPos(1, 1, 1)));
        moe.setOwnerUUID(new UUID(222L, 333L));
        moe.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        moe.setVisibleBlockState(Blocks.STRIPPED_OAK_LOG.defaultBlockState());
        moe.setGivenName("RowMoe");
        moe.setGender("FEMALE");
        moe.setBloodType("A");
        moe.setDere("HIMEDERE");
        moe.setZodiac("LEO");
        moe.setEmotion("SNOOTY");
        moe.setMoeScale(1.25F);
        moe.setHealth(13.0F);
        moe.setFoodLevel(18.0F);
        moe.setExhaustion(0.75F);
        moe.setSaturation(5.25F);
        moe.setStress(1.25F);
        moe.setRelaxation(2.25F);
        moe.setLoyalty(7.25F);
        moe.setAffection(3.25F);
        moe.setSlouch(0.5F);
        moe.setAge(4.0F);
        moe.setLastSeen(4444L);
        moe.setHasHome(true);
        moe.setHome(new DimBlockPos(level.dimension(), helper.absolutePos(new BlockPos(2, 1, 2))));

        try {
            NPC row = NPC.create(db, level, moe);
            moe.setDatabaseID(row.databaseId());
            row.updateFromMoe(db, level, moe);
            Moe loaded = new Moe(CustomEntities.MOE.get(), level);
            db.findNpc(row.databaseId()).orElseThrow().applyTo(loaded);
            assertEquals(helper, row.databaseId(), loaded.getDatabaseID(), "row database ID");
            assertEquals(helper, moe.getOwnerUUID(), loaded.getOwnerUUID(), "row owner");
            assertEquals(helper, moe.getBlockState(), loaded.getBlockState(), "row block state");
            assertEquals(helper, moe.getVisibleBlockState(), loaded.getVisibleBlockState(), "row visible block state");
            assertEquals(helper, "RowMoe", loaded.getGivenName(), "row name");
            assertEquals(helper, "FEMALE", loaded.getGender(), "row gender");
            assertEquals(helper, "A", loaded.getBloodType(), "row blood type");
            assertEquals(helper, "HIMEDERE", loaded.getDere(), "row dere");
            assertEquals(helper, "LEO", loaded.getZodiac(), "row zodiac");
            assertEquals(helper, "SNOOTY", loaded.getEmotion(), "row emotion");
            assertFloat(helper, 1.25F, loaded.getMoeScale(), "row scale");
            assertEquals(helper, true, loaded.isCorporeal(), "row corporeal");
            assertFloat(helper, 13.0F, loaded.getHealth(), "row health");
            assertFloat(helper, 18.0F, loaded.getFoodLevel(), "row food");
            assertFloat(helper, 0.75F, loaded.getExhaustion(), "row exhaustion");
            assertFloat(helper, 5.25F, loaded.getSaturation(), "row saturation");
            assertFloat(helper, 1.25F, loaded.getStress(), "row stress");
            assertFloat(helper, 2.25F, loaded.getRelaxation(), "row relaxation");
            assertFloat(helper, 7.25F, loaded.getLoyalty(), "row loyalty");
            assertFloat(helper, 3.25F, loaded.getAffection(), "row affection");
            assertFloat(helper, 0.5F, loaded.getSlouch(), "row slouch");
            assertFloat(helper, 4.0F, loaded.getAge(), "row age");
            assertEquals(helper, 4444L, loaded.getLastSeen(), "row last seen");
            assertEquals(helper, true, loaded.hasHome(), "row has home");
            assertEquals(helper, moe.getHome().getPos(), loaded.getHome().getPos(), "row home position");
        } catch (SQLException exception) {
            helper.fail("Expected NPC row field persistence to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void npcRowHealthUpdatesAfterDamage(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        db.configureDatabase(level.getServer());
        try {
            NPC.createTable(db);
        } catch (SQLException exception) {
            helper.fail("Expected NPC table setup to succeed: " + exception.getMessage());
            return;
        }

        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(helper.absolutePos(new BlockPos(1, 1, 1)));
        moe.setOwnerUUID(new UUID(224L, 335L));
        moe.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        try {
            NPC row = NPC.create(db, level, moe);
            moe.setDatabaseID(row.databaseId());
            level.addFreshEntity(moe);
            float before = moe.getHealth();
            if (!moe.hurtServer(level, level.damageSources().generic(), 2.0F)) {
                helper.fail("Expected persisted Moe damage to be applied");
                return;
            }
            NPC updated = db.findNpc(row.databaseId()).orElseThrow();
            if (!(updated.health() < before)) {
                helper.fail("Expected row health to update after damage, got " + updated.health() + " from " + before);
                return;
            }
            helper.kill(moe);
        } catch (SQLException exception) {
            helper.fail("Expected NPC row health sync test to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void npcRowFoodStatsUpdateAfterLiveMutation(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        db.configureDatabase(level.getServer());
        try {
            NPC.createTable(db);
        } catch (SQLException exception) {
            helper.fail("Expected NPC table setup to succeed: " + exception.getMessage());
            return;
        }

        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(helper.absolutePos(new BlockPos(1, 1, 1)));
        moe.setOwnerUUID(new UUID(225L, 336L));
        moe.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        moe.setFoodLevel(10.0F);
        moe.setExhaustion(1.0F);
        moe.setSaturation(2.0F);
        try {
            NPC row = NPC.create(db, level, moe);
            moe.setDatabaseID(row.databaseId());
            level.addFreshEntity(moe);
            moe.addFoodLevel(1.0F);
            moe.addExhaustion(0.5F);
            moe.addSaturation(0.25F);
            NPC updated = db.findNpc(row.databaseId()).orElseThrow();
            assertFloat(helper, 11.0F, updated.foodLevel(), "row food after add");
            assertFloat(helper, 1.5F, updated.exhaustion(), "row exhaustion after add");
            assertFloat(helper, 2.25F, updated.saturation(), "row saturation after add");
            helper.kill(moe);
        } catch (SQLException exception) {
            helper.fail("Expected NPC row food sync test to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeFieldSettersDoNotOpenSqliteConnections(GameTestHelper helper) {
        BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
        db.configureDatabase(helper.getLevel().getServer());
        int before = db.openConnectionCount();
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setDatabaseID(123L);
        moe.setOwnerUUID(new UUID(1L, 2L));
        moe.setBlockState(Blocks.COBBLESTONE.defaultBlockState());
        moe.setVisibleBlockState(Blocks.MOSSY_COBBLESTONE.defaultBlockState());
        moe.setMoeScale(0.9F);
        moe.setFollowing(true);
        moe.setBloodType("B");
        moe.setDere("DANDERE");
        moe.setZodiac("CANCER");
        moe.setEmotion("TIRED");
        moe.setFoodLevel(10.0F);
        moe.setHasHome(true);
        if (db.openConnectionCount() != before) {
            helper.fail("Expected Moe data setters not to open SQLite connections");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blockTagsApplyProfileTraitsAndFlags(GameTestHelper helper) {
        Moe male = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        male.setBlockState(Blocks.EMERALD_BLOCK.defaultBlockState());
        assertEquals(helper, "MALE", male.getGender(), "male pronoun tag gender");
        if ("Tokumei".equals(male.getGivenName())) {
            helper.fail("Expected Moe name loader to assign a non-default name");
            return;
        }
        if (!BlockPartyDB.get(helper.getLevel()).names().contains(male.getGivenName())) {
            helper.fail("Expected assigned name to be claimed in BlockPartyDB");
            return;
        }

        Moe winged = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        winged.setBlockState(Blocks.GLOWSTONE.defaultBlockState());
        assertEquals(helper, true, winged.hasWings(), "wing trait flag");
        assertEquals(helper, true, winged.hasGlow(), "glow trait flag");
        assertEquals(helper, true, winged.getNavigation() instanceof FlyingPathNavigation, "winged navigation");

        Moe cat = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        cat.setBlockState(Blocks.ANDESITE.defaultBlockState());
        assertEquals(helper, true, cat.hasCatFeatures(), "cat feature flag");
        assertEquals(helper, false, cat.ignoresRain(), "cat rain avoidance remains default");

        Moe grounded = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        grounded.setBlockState(Blocks.STONE.defaultBlockState());
        assertEquals(helper, true, grounded.getNavigation() instanceof GroundPathNavigation, "grounded navigation");

        Moe netherBrick = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        netherBrick.setBlockState(Blocks.NETHER_BRICKS.defaultBlockState());
        assertEquals(helper, false, netherBrick.ignoresRain(), "nether brick rain avoidance remains default");
        assertEquals(helper, true, netherBrick.ignoresDarkness(), "nether brick darkness opt-out flag");
        assertEquals(helper, false, netherBrick.hasCatFeatures(), "nether brick is not catlike");

        Moe ice = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        ice.setBlockState(Blocks.PACKED_ICE.defaultBlockState());
        assertEquals(helper, true, ice.ignoresRain(), "ice rain opt-out flag");

        Moe grass = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        grass.setBlockState(Blocks.GRASS_BLOCK.defaultBlockState());
        assertEquals(helper, true, grass.ignoresRain(), "dirt tag rain opt-out flag");

        Moe sand = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        sand.setBlockState(Blocks.SAND.defaultBlockState());
        assertEquals(helper, false, sand.ignoresRain(), "sand rain avoidance remains default");

        Moe ignoresVolume = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        ignoresVolume.setMoeScale(0.25F);
        ignoresVolume.setBlockState(Blocks.BAMBOO.defaultBlockState());
        assertEquals(helper, true, ignoresVolume.ignoresVolume(), "ignores volume flag");
        assertFloat(helper, 1.0F, ignoresVolume.getMoeScale(), "ignores volume scale");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void invalidTraitValuesFallBackToForgeDefaults(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setGender("invalid");
        moe.setBloodType("invalid");
        moe.setDere("invalid");
        moe.setZodiac("invalid");
        moe.setEmotion("invalid");
        assertEquals(helper, "FEMALE", moe.getGender(), "invalid gender fallback");
        assertEquals(helper, "O", moe.getBloodType(), "invalid blood type fallback");
        assertEquals(helper, "NYANDERE", moe.getDere(), "invalid dere fallback");
        assertEquals(helper, "ARIES", moe.getZodiac(), "invalid zodiac fallback");
        assertEquals(helper, "NORMAL", moe.getEmotion(), "invalid emotion fallback");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeInHidingStateRoundTrips(GameTestHelper helper) {
        BlockPos attachPos = new BlockPos(3, 4, 5);
        UUID owner = new UUID(840L, 480L);
        MoeInHiding hiding = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), helper.getLevel());
        hiding.setDatabaseID(84L);
        hiding.setAttachPos(attachPos);
        hiding.setHideUntil(HideUntil.ONE_SECOND_PASSES);
        hiding.setTicksHidden(17);
        hiding.setOwnerUUID(owner);

        CompoundTag saved = hiding.saveWithoutId(new CompoundTag());
        assertHasKey(helper, saved, MoeInHiding.NBT_DATABASE_ID);
        assertHasKey(helper, saved, MoeInHiding.NBT_ATTACH_POS);
        assertHasKey(helper, saved, MoeInHiding.NBT_HIDE_UNTIL);
        assertHasKey(helper, saved, MoeInHiding.NBT_TICKS_HIDDEN);
        assertHasKey(helper, saved, MoeInHiding.NBT_PLAYER_UUID);
        assertHasKey(helper, saved, MoeInHiding.NBT_OWNER_UUID);

        MoeInHiding loaded = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), helper.getLevel());
        loaded.load(saved);

        assertEquals(helper, 84L, loaded.getDatabaseID(), "MoeInHiding database ID");
        assertEquals(helper, attachPos, loaded.getAttachPos(), "MoeInHiding attached position");
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, loaded.getHideUntil(), "MoeInHiding HideUntil");
        assertEquals(helper, 17, loaded.getTicksHidden(), "MoeInHiding ticksHidden");
        assertEquals(helper, owner, loaded.getOwnerUUID(), "MoeInHiding owner UUID");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void registeredEntityIdsRemainStable(GameTestHelper helper) {
        assertEntityId(helper, CustomEntities.MOE.get(), "moe");
        assertEntityId(helper, CustomEntities.MOE_IN_HIDING.get(), "moe_in_hiding");
        helper.succeed();
    }

    private static void assertEntityId(GameTestHelper helper, EntityType<?> type, String path) {
        ResourceLocation expected = BlockParty.source(path);
        ResourceLocation actual = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (!expected.equals(actual)) {
            helper.fail("Expected entity ID " + expected + ", got " + actual);
        }
    }

    private static void assertSound(GameTestHelper helper, net.minecraft.sounds.SoundEvent sound, String expected, String label) {
        ResourceLocation actual = BuiltInRegistries.SOUND_EVENT.getKey(sound);
        assertEquals(helper, ResourceLocation.parse(expected), actual, label);
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertFloat(GameTestHelper helper, float expected, float actual, String label) {
        if (Math.abs(expected - actual) > 0.0001F) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertHasKey(GameTestHelper helper, CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            helper.fail("Expected Moe NBT to contain " + key + ", got " + tag);
        }
    }
}
