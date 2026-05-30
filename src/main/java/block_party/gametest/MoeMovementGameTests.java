package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.environment.MoeEnvironmentalObservation;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.movement.MoeAnchorType;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomEntities;
import java.sql.SQLException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static block_party.gametest.MovementGameTestSupport.buildLitDoorShelter;
import static block_party.gametest.MovementGameTestSupport.insertLocation;
import static block_party.gametest.MovementGameTestSupport.insertSimpleDataBlock;
import static block_party.gametest.MovementGameTestSupport.spawnMoe;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeMovementGameTests {
    private MoeMovementGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void routineAnchorResolverPrefersActiveLocationSchedule(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1711L, 2711L);
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)), 0.0F, 0.0F);
        moe.setPlayerUUID(owner);
        moe.setHome(new DimBlockPos(level.dimension(), helper.absolutePos(new BlockPos(2, 1, 1))));
        moe.setHasHome(true);
        try {
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(8, 1, 1)), "if_morning", 2);
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(12, 1, 1)), "if_night", 8);
        } catch (SQLException exception) {
            helper.fail("Expected routine location setup to succeed: " + exception.getMessage());
            return;
        }

        MoeAnchor anchor = moe.currentRoutineAnchor().orElse(null);
        if (anchor == null || anchor.type() != MoeAnchorType.LOCATION || !anchor.dimPos().getPos().equals(helper.absolutePos(new BlockPos(8, 1, 1)))) {
            helper.fail("Expected active morning location to outrank inactive night location and home");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void routineAnchorResolverUsesGardenWhenNoScheduleMatches(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1712L, 2712L);
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)), 0.0F, 0.0F);
        moe.setPlayerUUID(owner);
        try {
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(10, 1, 1)), "if_night", 9);
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, helper.absolutePos(new BlockPos(5, 1, 1)));
        } catch (SQLException exception) {
            helper.fail("Expected garden anchor setup to succeed: " + exception.getMessage());
            return;
        }

        MoeAnchor anchor = moe.currentRoutineAnchor().orElse(null);
        if (anchor == null || anchor.type() != MoeAnchorType.GARDEN || !anchor.dimPos().getPos().equals(helper.absolutePos(new BlockPos(5, 1, 1)))) {
            helper.fail("Expected garden anchor when scheduled location is inactive");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void idleRoutineMovementDriftsTowardNearbyAnchor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1713L, 2713L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos anchorPos = helper.absolutePos(new BlockPos(12, 1, 1));
        try {
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, anchorPos);
        } catch (SQLException exception) {
            helper.fail("Expected idle anchor setup to succeed: " + exception.getMessage());
            return;
        }

        Vec3 destination = moe.idleRoutineDestination();
        if (destination == null) {
            helper.fail("Expected idle Moe to find a nearby routine anchor destination");
            return;
        }
        Vec3 anchor = Vec3.atBottomCenterOf(anchorPos);
        if (destination.distanceToSqr(anchor) > 0.0001D) {
            helper.fail("Expected far idle anchor destination to aim at the anchor center");
            return;
        }
        moe.startFollowSession(owner, PlayerMovementIntent.PARTY_INVITE, 80, false, false);
        if (moe.idleRoutineDestination() != null) {
            helper.fail("Expected party Moes to skip idle routine movement");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void idleRoutineMovementOrbitsNearbySocialGroupBeforeAnchor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1714L, 2714L);
        Moe observer = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        Moe neighbor = spawnMoe(helper, level, owner, new BlockPos(4, 1, 1));
        BlockPos anchorPos = helper.absolutePos(new BlockPos(10, 1, 1));
        try {
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, anchorPos);
        } catch (SQLException exception) {
            helper.fail("Expected idle social setup to succeed: " + exception.getMessage());
            return;
        }

        Vec3 destination = observer.idleRoutineDestination();
        if (destination == null) {
            helper.fail("Expected idle Moe to orbit nearby social group");
            return;
        }
        Vec3 center = observer.position().add(neighbor.position()).scale(0.5D);
        double distanceToGroup = destination.distanceToSqr(center);
        double distanceToAnchor = destination.distanceToSqr(Vec3.atBottomCenterOf(anchorPos));
        if (distanceToGroup >= distanceToAnchor) {
            helper.fail("Expected idle social orbit to take priority over anchor drift");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wellbeingRoutineIntentPrefersGardenForStress(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1715L, 2715L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        moe.setStress(18.0F);
        moe.setRelaxation(0.0F);
        try {
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(6, 1, 1)), "always", 2);
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, helper.absolutePos(new BlockPos(10, 1, 1)));
        } catch (SQLException exception) {
            helper.fail("Expected wellbeing routine setup to succeed: " + exception.getMessage());
            return;
        }

        MoeAnchor anchor = moe.currentRoutineAnchor().orElse(null);
        if (moe.getEffectiveRoutineIntent() != RoutineIntent.RELAX || anchor == null || anchor.type() != MoeAnchorType.GARDEN) {
            helper.fail("Expected stressed idle Moe to prefer garden relaxation anchor");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void explicitRoutineIntentPrefersMatchingAnchorRole(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1716L, 2716L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        moe.setRoutineIntent(RoutineIntent.GATHER);
        try {
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(4, 1, 1)), "always", 5);
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_SAPLINGS, owner, level, helper.absolutePos(new BlockPos(12, 1, 1)));
        } catch (SQLException exception) {
            helper.fail("Expected explicit routine setup to succeed: " + exception.getMessage());
            return;
        }

        MoeAnchor anchor = moe.currentRoutineAnchor().orElse(null);
        if (anchor == null || anchor.type() != MoeAnchorType.SAPLING) {
            helper.fail("Expected explicit gather routine intent to prefer sapling anchor");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void spawnedMoeUsesSourceBlockAsHomePosition(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(1717L, 2717L);
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (!moe.hasHome() || !moe.getHome().getPos().equals(source)) {
            helper.fail("Expected spawned Moe home position to be original source block");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void sleepRoutineIntentPrefersHomeAnchor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1718L, 2718L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        moe.setRoutineIntent(RoutineIntent.SLEEP);
        try {
            insertLocation(db, owner, level, helper.absolutePos(new BlockPos(4, 1, 1)), "always", 10);
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, helper.absolutePos(new BlockPos(6, 1, 1)));
        } catch (SQLException exception) {
            helper.fail("Expected sleep routine setup to succeed: " + exception.getMessage());
            return;
        }

        MoeAnchor anchor = moe.currentRoutineAnchor().orElse(null);
        if (anchor == null || anchor.type() != MoeAnchorType.HOME || !anchor.dimPos().getPos().equals(moe.getHome().getPos())) {
            helper.fail("Expected sleep routine intent to prefer home anchor");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shelterScoringPrefersLitDoorShelter(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(1719L, 2719L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));

        BlockPos torchSpot = helper.absolutePos(new BlockPos(4, 1, 1));
        level.setBlock(torchSpot.above(), Blocks.TORCH.defaultBlockState(), 3);

        BlockPos treeSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(treeSpot.above(2), Blocks.OAK_LEAVES.defaultBlockState(), 3);

        BlockPos houseSpot = helper.absolutePos(new BlockPos(12, 1, 1));
        buildLitDoorShelter(level, houseSpot);

        int torchScore = moe.shelterScoreAt(torchSpot).score();
        int treeScore = moe.shelterScoreAt(treeSpot).score();
        int houseScore = moe.shelterScoreAt(houseSpot).score();
        if (houseScore <= treeScore || houseScore <= torchScore || !moe.shelterScoreAt(houseSpot).nearDoor()) {
            helper.fail("Expected lit door shelter to outrank tree and torch shelter: house "
                    + houseScore + ", tree " + treeScore + ", torch " + torchScore);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void rainAvoidanceChoosesStrongShelter(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(0, 6000, true, false);
        level.setRainLevel(1.0F);
        UUID owner = new UUID(1720L, 2720L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 8));

        BlockPos treeSpot = helper.absolutePos(new BlockPos(5, 1, 8));
        level.setBlock(treeSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(treeSpot.above(2), Blocks.OAK_LEAVES.defaultBlockState(), 3);

        BlockPos houseSpot = helper.absolutePos(new BlockPos(9, 1, 8));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);

        Vec3 destination = moe.environmentalRoutineDestination();
        if (destination == null
                || destination.distanceToSqr(Vec3.atBottomCenterOf(houseSpot)) > 9.0D
                || !moe.shelterScoreAt(BlockPos.containing(destination)).nearDoor()) {
            helper.fail("Expected rain avoidance to choose lit door shelter, got " + destination);
            return;
        }

        moe.setBlockState(Blocks.PACKED_ICE.defaultBlockState());
        if (moe.shouldSeekRainShelter()) {
            helper.fail("Expected packed ice Moe to ignore rain shelter movement");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void environmentalMovementPersistsBetweenDecisionTicks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(0, 6000, true, false);
        level.setRainLevel(1.0F);
        UUID owner = new UUID(1722L, 2722L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 8));

        BlockPos houseSpot = helper.absolutePos(new BlockPos(9, 1, 8));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);

        moe.updateActionState();
        if (!moe.hasEnvironmentalMovementIntent()) {
            helper.fail("Expected environmental movement to create a persistent intent");
            return;
        }
        if (!"SHIVER".equals(moe.getAnimationKey())) {
            helper.fail("Expected rain shelter movement to trigger shiver animation, got " + moe.getAnimationKey());
            return;
        }

        moe.getNavigation().stop();
        moe.updateActionState();
        if (!moe.hasEnvironmentalMovementIntent()
                || (!moe.getNavigation().isInProgress() && !moe.getMoveControl().hasWanted())) {
            helper.fail("Expected environmental movement intent to continue between decision ticks");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void temporaryAmbientAnimationExpires(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 1, 1, 1);
        moe.setSitting(true);
        moe.setTemporaryAnimationKey("LOOK_AROUND", 3);
        if (!"LOOK_AROUND".equals(moe.getAnimationKey())) {
            helper.fail("Expected temporary animation to apply immediately");
            return;
        }

        helper.runAfterDelay(8, () -> {
            if (!"DEFAULT".equals(moe.getAnimationKey())) {
                helper.fail("Expected temporary animation to return to default, got " + moe.getAnimationKey());
                return;
            }
            helper.kill(moe);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void beggingEmotionTriggersBegAnimation(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 1, 1, 1);
        moe.setSitting(true);
        moe.setEmotion("BEGGING");
        if (!"BEG".equals(moe.getAnimationKey())) {
            helper.fail("Expected begging emotion to trigger beg animation, got " + moe.getAnimationKey());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void aweAnimationCanBeTriggeredTemporarily(GameTestHelper helper) {
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 1, 1, 1);
        moe.setSitting(true);
        moe.setTemporaryAnimationKey("AWE", 20);
        if (!"AWE".equals(moe.getAnimationKey())) {
            helper.fail("Expected awe animation to apply immediately, got " + moe.getAnimationKey());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void environmentalObservationRemembersTenseBlockAffinity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 1, 1, 1);
        moe.setSitting(true);
        moe.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        BlockPos magma = helper.absolutePos(new BlockPos(4, 1, 1));
        level.setBlock(magma, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);

        MoeEnvironmentalObservation.Observation observation = moe.observeEnvironmentNow().orElse(null);
        if (observation == null || observation.kind() != MoeEnvironmentalObservation.Kind.TENSION || !observation.pos().equals(magma)) {
            helper.fail("Expected oak log Moe to remember magma as a tense environmental observation, got " + observation);
            return;
        }
        if (!"SHIVER".equals(moe.getAnimationKey())) {
            helper.fail("Expected tense environmental observation to trigger shiver animation, got " + moe.getAnimationKey());
            return;
        }
        if (moe.latestEnvironmentalObservation().isEmpty()) {
            helper.fail("Expected latest environmental observation memory to be available");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void environmentalObservationUsesBlockLayerForLocalHangouts(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        Moe moe = helper.spawn(CustomEntities.MOE.get(), 1, 1, 1);
        moe.setSitting(true);
        moe.setBlockState(Blocks.OAK_LOG.defaultBlockState());
        BlockPos log = helper.absolutePos(new BlockPos(4, 1, 1));
        level.setBlock(log, Blocks.SPRUCE_LOG.defaultBlockState(), 3);

        MoeEnvironmentalObservation.Observation observation = moe.observeEnvironmentNow().orElse(null);
        if (observation == null || observation.kind() != MoeEnvironmentalObservation.Kind.AFFINITY || !observation.pos().equals(log)) {
            helper.fail("Expected log Moe to notice nearby logs as a local hangout affinity, got " + observation);
            return;
        }
        if (observation.layeredSignal().block().affinity() <= 0.0F
                || observation.layeredSignal().strongestLayer() != block_party.entities.social.SocialAffinities.RuleLayer.BLOCK) {
            helper.fail("Expected environmental observation to be driven by the block rule layer");
            return;
        }
        if (!"HAPPY_DANCE".equals(moe.getAnimationKey())) {
            helper.fail("Expected affinity environmental observation to trigger happy dance, got " + moe.getAnimationKey());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void placeObservationRemembersComfortableHouse(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1723L, 2723L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos houseSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);

        MoePlaceMemory.Place place = moe.observePlaceNow().orElse(null);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.HOUSE
                || place.pos().distSqr(houseSpot) > 9.0D) {
            helper.fail("Expected Moe to remember lit door shelter as a house, got " + place);
            return;
        }
        if (moe.rememberedPlace().isEmpty() || moe.rememberedPlace().get().pos().distSqr(houseSpot) > 9.0D) {
            helper.fail("Expected Moe remembered place to be the observed house");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void rainAvoidanceUsesRememberedHouseBeforeNearestShelter(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1724L, 2724L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));

        BlockPos rememberedHouse = helper.absolutePos(new BlockPos(11, 1, 1));
        level.setBlock(rememberedHouse.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, rememberedHouse);
        level.setBlock(rememberedHouse.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        MoePlaceMemory.Place remembered = moe.observePlaceNow().orElse(null);
        if (remembered == null || remembered.type() != MoePlaceMemory.PlaceType.HOUSE) {
            helper.fail("Expected setup to produce a remembered house, got " + remembered);
            return;
        }

        BlockPos nearerHouse = helper.absolutePos(new BlockPos(5, 1, 1));
        level.setBlock(nearerHouse.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, nearerHouse);
        level.setBlock(nearerHouse.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setWeatherParameters(0, 6000, true, false);
        level.setRainLevel(1.0F);

        Vec3 destination = moe.environmentalRoutineDestination();
        if (destination == null || destination.distanceToSqr(Vec3.atBottomCenterOf(remembered.pos())) > 0.01D) {
            helper.fail("Expected rain avoidance to use remembered house before nearest shelter, got " + destination);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void placeObservationRejectsOvercrowdedHouseMemory(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1725L, 2725L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos houseSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        spawnMoe(helper, level, owner, new BlockPos(7, 1, 1));
        spawnMoe(helper, level, owner, new BlockPos(8, 1, 2));
        spawnMoe(helper, level, owner, new BlockPos(9, 1, 1));
        spawnMoe(helper, level, owner, new BlockPos(8, 1, 0));

        MoePlaceMemory.Place place = MoePlaceMemory.evaluate(moe, houseSpot);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.HOUSE || !place.overcrowded()) {
            helper.fail("Expected observed house to be considered overcrowded, got " + place);
            return;
        }
        moe.observePlaceNow();
        if (moe.rememberedPlace().isPresent() && moe.rememberedPlace().get().pos().distSqr(houseSpot) <= 16.0D) {
            helper.fail("Expected Moe not to claim the overcrowded house, got " + moe.rememberedPlace().get());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void rememberedPlacePersistsThroughSaveLoad(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1726L, 2726L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos houseSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        MoePlaceMemory.Place place = moe.observePlaceNow().orElse(null);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.HOUSE) {
            helper.fail("Expected setup to remember a house before save, got " + place);
            return;
        }

        Moe loaded = new Moe(CustomEntities.MOE.get(), level);
        loaded.load(moe.saveWithoutId(new CompoundTag()));
        if (loaded.rememberedPlace().isEmpty()
                || loaded.rememberedPlace().get().type() != MoePlaceMemory.PlaceType.HOUSE
                || !loaded.rememberedPlace().get().pos().equals(place.pos())) {
            helper.fail("Expected remembered house to persist through save/load, got " + loaded.rememberedPlace());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void idleRoutineCanRevisitRememberedComfortPlace(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1727L, 2727L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos houseSpot = helper.absolutePos(new BlockPos(9, 1, 1));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        MoePlaceMemory.Place place = moe.observePlaceNow().orElse(null);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.HOUSE) {
            helper.fail("Expected setup to remember a house before idle revisit, got " + place);
            return;
        }

        Vec3 destination = moe.idleRoutineDestination();
        if (destination == null || destination.distanceToSqr(Vec3.atBottomCenterOf(place.pos())) > 0.01D) {
            helper.fail("Expected idle routine to revisit remembered comfort place, got " + destination);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void rememberedPlaceInvalidatesWhenPlaceChanges(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1728L, 2728L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos houseSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(houseSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(3), Blocks.GLOWSTONE.defaultBlockState(), 3);
        MoePlaceMemory.Place place = moe.observePlaceNow().orElse(null);
        if (place == null || moe.rememberedPlace().isEmpty()) {
            helper.fail("Expected setup to remember a place before invalidation");
            return;
        }

        level.setBlock(place.pos().below(), Blocks.AIR.defaultBlockState(), 3);
        Vec3 destination = moe.idleRoutineDestination();
        if (destination != null && destination.distanceToSqr(Vec3.atBottomCenterOf(place.pos())) <= 0.01D) {
            helper.fail("Expected idle routine not to revisit invalid remembered place");
            return;
        }
        if (moe.rememberedPlace().isPresent()) {
            helper.fail("Expected invalid remembered place to be cleared");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void expandedPlaceClassificationDetectsWaterfrontCaveAndFarm(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1729L, 2729L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));

        BlockPos waterfront = helper.absolutePos(new BlockPos(7, 1, 1));
        level.setBlock(waterfront.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        for (int z = -2; z <= 2; z++) {
            level.setBlock(waterfront.east().offset(0, -1, z), Blocks.DIRT.defaultBlockState(), 3);
            level.setBlock(waterfront.east().offset(0, 0, z), Blocks.WATER.defaultBlockState(), 3);
        }
        assertPlaceType(helper, MoePlaceMemory.PlaceType.WATERFRONT, MoePlaceMemory.evaluate(moe, waterfront), "waterfront");

        BlockPos cave = helper.absolutePos(new BlockPos(14, 1, 1));
        level.setBlock(cave.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(cave.above(2), Blocks.STONE.defaultBlockState(), 3);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlock(cave.offset(x, -1, z), Blocks.STONE.defaultBlockState(), 3);
            }
        }
        assertPlaceType(helper, MoePlaceMemory.PlaceType.CAVE, MoePlaceMemory.evaluate(moe, cave), "cave");

        BlockPos farm = helper.absolutePos(new BlockPos(21, 1, 1));
        level.setBlock(farm.below(), Blocks.FARMLAND.defaultBlockState(), 3);
        for (int x = -2; x <= 2; x++) {
            level.setBlock(farm.offset(x, -1, 1), Blocks.FARMLAND.defaultBlockState(), 3);
            level.setBlock(farm.offset(x, 0, 1), Blocks.WHEAT.defaultBlockState(), 3);
        }
        assertPlaceType(helper, MoePlaceMemory.PlaceType.FARM, MoePlaceMemory.evaluate(moe, farm), "farm");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void playerAnchorsActAsStrongPlaceEvidence(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        UUID owner = new UUID(1730L, 2730L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPartyDB db = BlockPartyDB.get(level);

        BlockPos gardenAnchor = helper.absolutePos(new BlockPos(7, 1, 1));
        BlockPos shrineAnchor = helper.absolutePos(new BlockPos(14, 1, 1));
        level.setBlock(gardenAnchor.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        level.setBlock(shrineAnchor.below(), Blocks.STONE.defaultBlockState(), 3);
        try {
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, gardenAnchor);
            insertSimpleDataBlock(db, BlockPartyDB.TABLE_SHRINES, owner, level, shrineAnchor);
        } catch (SQLException exception) {
            helper.fail("Expected anchor evidence setup to succeed: " + exception.getMessage());
            return;
        }

        MoePlaceMemory.Place garden = MoePlaceMemory.evaluate(moe, gardenAnchor);
        assertPlaceType(helper, MoePlaceMemory.PlaceType.GARDEN, garden, "garden anchor");
        if (garden.features().anchorType() != MoeAnchorType.GARDEN) {
            helper.fail("Expected garden place to remember garden anchor evidence, got " + garden.features().anchorType());
            return;
        }

        MoePlaceMemory.Place shrine = MoePlaceMemory.evaluate(moe, shrineAnchor);
        assertPlaceType(helper, MoePlaceMemory.PlaceType.SHRINE, shrine, "shrine anchor");
        if (shrine.features().anchorType() != MoeAnchorType.SHRINE) {
            helper.fail("Expected shrine place to remember shrine anchor evidence, got " + shrine.features().anchorType());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeMovementGoalsRegisterByPriority(GameTestHelper helper) {
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        assertGoalPriority(helper, moe, "FollowSessionGoal", 1);
        assertGoalPriority(helper, moe, "EnvironmentalMovementGoal", 2);
        assertGoalPriority(helper, moe, "SocialReactionGoal", 3);
        assertGoalPriority(helper, moe, "IdleRoutineGoal", 4);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void darknessAvoidanceChoosesLight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setWeatherParameters(6000, 0, false, false);
        level.setRainLevel(0.0F);
        level.setDayTime(18000L);
        UUID owner = new UUID(1721L, 2721L);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        BlockPos origin = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(origin.above(2), Blocks.OAK_PLANKS.defaultBlockState(), 3);

        BlockPos lightSpot = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(lightSpot.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(lightSpot, Blocks.TORCH.defaultBlockState(), 3);

        Vec3 destination = moe.environmentalRoutineDestination();
        if (destination == null || destination.distanceToSqr(Vec3.atBottomCenterOf(lightSpot)) > 0.01D) {
            helper.fail("Expected darkness avoidance to choose light source, got " + destination);
            return;
        }

        moe.setBlockState(Blocks.NETHER_BRICKS.defaultBlockState());
        if (moe.shouldSeekLight()) {
            helper.fail("Expected nether brick Moe to ignore darkness movement");
            return;
        }
        helper.succeed();
    }

    private static void assertGoalPriority(GameTestHelper helper, Moe moe, String goalName, int priority) {
        boolean found = moe.goalSelector.getAvailableGoals().stream()
                .anyMatch(goal -> goal.getPriority() == priority && goal.getGoal().getClass().getSimpleName().equals(goalName));
        if (!found) {
            helper.fail("Expected movement goal " + goalName + " at priority " + priority);
        }
    }

    private static void assertPlaceType(GameTestHelper helper, MoePlaceMemory.PlaceType expected, MoePlaceMemory.Place place, String label) {
        if (place == null || place.type() != expected) {
            helper.fail("Expected " + label + " place type to be " + expected + ", got " + place);
        }
    }
}
