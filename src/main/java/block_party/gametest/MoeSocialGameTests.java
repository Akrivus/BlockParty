package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.social.MoeSocialRules;
import block_party.entities.social.SocialAffinities;
import block_party.registry.CustomEntities;
import java.sql.SQLException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static block_party.gametest.MovementGameTestSupport.insertSimpleDataBlock;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeSocialGameTests {
    private MoeSocialGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void bloodTypeSocialRulesFavorUniversalDonorAndReceiver(GameTestHelper helper) {
        MoeSocialRules.SocialSignal oToAb = MoeSocialRules.bloodSignal("O", "AB");
        MoeSocialRules.SocialSignal aToB = MoeSocialRules.bloodSignal("A", "B");
        MoeSocialRules.SocialSignal bToAb = MoeSocialRules.bloodSignal("B", "AB");

        assertGreater(helper, oToAb.affinity(), aToB.affinity(), "O affinity toward AB over A/B tension");
        assertGreater(helper, oToAb.interest(), aToB.interest(), "O interest toward AB over A/B tension");
        assertGreater(helper, bToAb.affinity(), aToB.affinity(), "B affinity toward AB over A/B tension");
        if (!MoeSocialRules.canDonateTo("O", "A") || !MoeSocialRules.canDonateTo("O", "AB")) {
            helper.fail("Expected O blood type to donate socially to all receivers");
            return;
        }
        if (MoeSocialRules.canDonateTo("AB", "O")) {
            helper.fail("Expected AB blood type to be receiver-focused, not an O donor");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void bloodTypeSocialMovementKeepsLooseGroups(GameTestHelper helper) {
        MoeSocialRules.SocialSignal oToAb = MoeSocialRules.bloodSignal("O", "AB");
        MoeSocialRules.SocialSignal aToB = MoeSocialRules.bloodSignal("A", "B");

        assertEquals(helper, MoeSocialRules.SocialMovement.APPROACH, MoeSocialRules.movementFor(oToAb, 36.0D), "O to AB movement at range");
        assertEquals(helper, MoeSocialRules.SocialMovement.IDLE, MoeSocialRules.movementFor(oToAb, 1.0D), "compatible movement inside personal space");
        assertEquals(helper, MoeSocialRules.SocialMovement.AVOID, MoeSocialRules.movementFor(aToB, 9.0D), "A to B movement when crowded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dereTypesAdjustSocialMovementEnergy(GameTestHelper helper) {
        assertGreater(helper, (float) MoeSocialRules.socialStepDistance("YANDERE"), (float) MoeSocialRules.socialStepDistance("DANDERE"), "active dere step distance");
        assertGreater(helper, (float) MoeSocialRules.socialMoveSpeed("DEREDERE"), (float) MoeSocialRules.socialMoveSpeed("KUUDERE"), "active dere move speed");
        assertGreater(helper, MoeSocialRules.socialTickDelay("DANDERE", 0), MoeSocialRules.socialTickDelay("YANDERE", 0), "quiet dere social delay");
        assertGreater(helper, MoeSocialRules.socialMovementDuration("DANDERE"), MoeSocialRules.socialMovementDuration("YANDERE"), "active dere movement duration");
        assertEquals(helper, "SMITTEN", MoeSocialRules.compatibleEmotion("YANDERE"), "yandere compatible emotion");
        assertEquals(helper, "EMBARRASSED", MoeSocialRules.compatibleEmotion("DANDERE"), "dandere compatible emotion");
        assertEquals(helper, "PSYCHOTIC", MoeSocialRules.tenseEmotion("YANDERE"), "yandere tense emotion");
        assertEquals(helper, "ANGRY", MoeSocialRules.tenseEmotion("TSUNDERE"), "tsundere tense emotion");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialRulesExposeReadableVisualStates(GameTestHelper helper) {
        MoeSocialRules.SocialSignal oToAb = MoeSocialRules.bloodSignal("O", "AB");
        MoeSocialRules.SocialSignal aToA = MoeSocialRules.bloodSignal("A", "A");
        MoeSocialRules.SocialSignal oToA = MoeSocialRules.bloodSignal("O", "A");
        MoeSocialRules.SocialSignal aToB = MoeSocialRules.bloodSignal("A", "B");

        assertEquals(helper, MoeSocialRules.SocialVisual.FAME, MoeSocialRules.visualFor("O", "AB", oToAb), "AB receiver fame visual");
        assertEquals(helper, MoeSocialRules.SocialVisual.AFFINITY, MoeSocialRules.visualFor("A", "A", aToA), "same-type affinity visual");
        assertEquals(helper, MoeSocialRules.SocialVisual.INTEREST, MoeSocialRules.visualFor("O", "A", oToA), "donor interest visual");
        assertEquals(helper, MoeSocialRules.SocialVisual.TENSION, MoeSocialRules.visualFor("A", "B", aToB), "uneasy pair visual");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blockAffinitiesCanOverrideFriendlyBloodSignals(GameTestHelper helper) {
        MoeSocialRules.SocialSignal combined = MoeSocialRules.combine(
                MoeSocialRules.bloodSignal("O", "AB"),
                SocialAffinities.signal(
                        new SocialAffinities.Profile(Blocks.OAK_LOG.defaultBlockState(), "O", "KUUDERE", "ARIES", "FEMALE", "NORMAL"),
                        new SocialAffinities.Profile(Blocks.NETHERRACK.defaultBlockState(), "AB", "DANDERE", "PISCES", "FEMALE", "NORMAL")));

        if (combined.tension() <= 0.0F || combined.interest() <= 0.0F) {
            helper.fail("Expected block social affinity to add tension and interest to blood signal");
            return;
        }
        assertEquals(helper, MoeSocialRules.SocialMovement.AVOID, MoeSocialRules.movementFor(combined, 9.0D), "flammable block near hot block movement");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void traitAffinitiesCombineWithBloodSignals(GameTestHelper helper) {
        MoeSocialRules.SocialSignal bloodOnly = MoeSocialRules.bloodSignal("O", "A");
        MoeSocialRules.SocialSignal combined = MoeSocialRules.combine(
                bloodOnly,
                SocialAffinities.signal(
                        new SocialAffinities.Profile(Blocks.STONE.defaultBlockState(), "O", "DEREDERE", "ARIES", "FEMALE", "HAPPY"),
                        new SocialAffinities.Profile(Blocks.DIRT.defaultBlockState(), "A", "DANDERE", "PISCES", "FEMALE", "NORMAL")));

        assertGreater(helper, combined.affinity(), bloodOnly.affinity(), "trait affinity contribution");
        assertGreater(helper, combined.interest(), bloodOnly.interest(), "trait interest contribution");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dereReactionsSpecializeSocialSignals(GameTestHelper helper) {
        MoeSocialRules.SocialSignal oToAb = MoeSocialRules.bloodSignal("O", "AB");
        MoeSocialRules.SocialSignal aToB = MoeSocialRules.bloodSignal("A", "B");
        MoeSocialRules.SocialVisual fame = MoeSocialRules.visualFor("O", "AB", oToAb);
        MoeSocialRules.SocialVisual tension = MoeSocialRules.visualFor("A", "B", aToB);

        assertEquals(helper, MoeSocialRules.DereReaction.CLING, MoeSocialRules.dereReaction("YANDERE", fame, oToAb, 25.0D), "yandere fame reaction");
        assertEquals(helper, MoeSocialRules.DereReaction.FLUSTER_RETREAT, MoeSocialRules.dereReaction("TSUNDERE", fame, oToAb, 9.0D), "tsundere close fame reaction");
        assertEquals(helper, MoeSocialRules.DereReaction.SHOW_OFF, MoeSocialRules.dereReaction("HIMEDERE", fame, oToAb, 25.0D), "himedere fame reaction");
        assertEquals(helper, MoeSocialRules.DereReaction.SHY_RETREAT, MoeSocialRules.dereReaction("DANDERE", tension, aToB, 9.0D), "dandere tension reaction");
        assertEquals(helper, "SMITTEN", MoeSocialRules.reactionEmotion("YANDERE", MoeSocialRules.DereReaction.CLING, false), "yandere cling emotion");
        assertEquals(helper, "ANGRY", MoeSocialRules.reactionEmotion("TSUNDERE", MoeSocialRules.DereReaction.FLUSTER_RETREAT, true), "tsundere tense retreat emotion");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialSignalsResolveVariedResponseEmotions(GameTestHelper helper) {
        MoeSocialRules.SocialSignal friendly = new MoeSocialRules.SocialSignal(0.7F, 0.0F, 0.35F);
        MoeSocialRules.SocialSignal interesting = new MoeSocialRules.SocialSignal(0.15F, 0.0F, 0.65F);
        MoeSocialRules.SocialSignal mixed = new MoeSocialRules.SocialSignal(0.45F, 0.45F, 0.65F);
        MoeSocialRules.SocialSignal tense = new MoeSocialRules.SocialSignal(0.1F, 0.7F, 0.65F);

        assertEquals(helper, "HAPPY", MoeSocialRules.responseEmotion("DEREDERE", friendly, MoeSocialRules.DereReaction.CELEBRATE, "NORMAL"), "deredere friendly response");
        assertEquals(helper, "NORMAL", MoeSocialRules.responseEmotion("KUUDERE", friendly, MoeSocialRules.DereReaction.OBSERVE, "HAPPY"), "kuudere friendly response");
        assertEquals(helper, "SMITTEN", MoeSocialRules.responseEmotion("YANDERE", friendly, MoeSocialRules.DereReaction.CLING, "SMITTEN"), "yandere smitten response");
        assertEquals(helper, "EMBARRASSED", MoeSocialRules.responseEmotion("DANDERE", interesting, MoeSocialRules.DereReaction.OBSERVE, "NORMAL"), "dandere interest response");
        assertEquals(helper, "EMBARRASSED", MoeSocialRules.responseEmotion("TSUNDERE", mixed, MoeSocialRules.DereReaction.FLUSTER_RETREAT, "NORMAL"), "tsundere mixed response");
        assertEquals(helper, "PSYCHOTIC", MoeSocialRules.responseEmotion("YANDERE", tense, MoeSocialRules.DereReaction.CLING, "ANGRY"), "yandere tense response");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialTickLetsMoeReactToCompatibleNeighbor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        Moe observer = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        Moe receiver = helper.spawn(CustomEntities.MOE.get(), 2, 1, 0);
        observer.setBloodType("O");
        receiver.setBloodType("AB");
        observer.setSitting(true);
        receiver.setSitting(true);
        observer.setEmotion("NORMAL");
        observer.setRelaxation(0.0F);

        observer.updateActionState();

        assertEquals(helper, "HAPPY", observer.getEmotion(), "observer emotion after compatible social tick");
        assertEquals(helper, "HAPPY_DANCE", observer.getAnimationKey(), "observer animation after compatible social tick");
        assertGreater(helper, observer.getRelaxation(), 0.0F, "observer relaxation after compatible social tick");
        helper.kill(observer);
        helper.kill(receiver);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialTickAppliesDereSpecificEmotion(GameTestHelper helper) {
        MoeSocialRules.SocialSignal signal = MoeSocialRules.combine(
                MoeSocialRules.bloodSignal("O", "AB"),
                new MoeSocialRules.SocialSignal(0.0F, 0.0F, 0.1F));
        MoeSocialRules.DereReaction reaction = MoeSocialRules.dereReaction(
                "YANDERE",
                MoeSocialRules.visualFor("O", "AB", signal),
                signal,
                4.0D);

        assertEquals(helper, MoeSocialRules.DereReaction.CLING, reaction, "yandere compatible social reaction");
        assertEquals(helper, "SMITTEN", MoeSocialRules.responseEmotion("YANDERE", signal, reaction, "SMITTEN"), "yandere compatible social emotion");
        assertEquals(helper, "SMITTEN", MoeSocialRules.reactionEmotion("YANDERE", reaction, false), "yandere compatible reaction emotion");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void socialTickStartsApproachTowardCompatibleNeighbor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.setDayTime(1000L);
        level.setWeatherParameters(6000, 0, false, false);
        for (int x = 0; x <= 6; x++) {
            level.setBlock(helper.absolutePos(new BlockPos(x, 0, 0)), Blocks.STONE.defaultBlockState(), 3);
        }
        Moe observer = helper.spawn(CustomEntities.MOE.get(), 0, 1, 0);
        Moe receiver = helper.spawn(CustomEntities.MOE.get(), 5, 1, 0);
        observer.setBloodType("O");
        receiver.setBloodType("AB");
        observer.setFollowing(false);
        observer.setSitting(false);
        double before = observer.distanceToSqr(receiver);

        observer.updateActionState();

        helper.runAfterDelay(20, () -> {
            double after = observer.distanceToSqr(receiver);
            if (after >= before) {
                helper.fail("Expected compatible social tick to move closer; before " + before + ", after " + after);
                return;
            }
            helper.kill(observer);
            helper.kill(receiver);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void idleMoeSharesFriendlyRememberedHangout(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(1603L, 2603L);
        Moe observer = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        Moe friend = spawnMoe(helper, level, owner, new BlockPos(3, 1, 1));
        observer.setBloodType("O");
        observer.setDere("DEREDERE");
        friend.setBloodType("AB");
        friend.setDere("DANDERE");

        BlockPos garden = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(garden.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        try {
            insertSimpleDataBlock(BlockPartyDB.get(level), "GardenLanterns", owner, level, garden);
        } catch (SQLException exception) {
            helper.fail("Expected garden hangout setup to succeed: " + exception.getMessage());
            return;
        }
        MoePlaceMemory.Place place = friend.observePlaceNow().orElse(null);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.GARDEN) {
            helper.fail("Expected friend to remember garden hangout, got " + place);
            return;
        }

        Vec3 destination = observer.idleRoutineDestination();
        if (destination == null || destination.distanceToSqr(Vec3.atBottomCenterOf(place.pos())) > 36.0D) {
            helper.fail("Expected observer to drift toward friend's remembered garden, got " + destination);
            return;
        }
        Moe.MoeSocialPlaceMemory memory = observer.socialPlaceMemoryForTests().orElse(null);
        if (memory == null || memory.behavior() != MoeSocialRules.SocialPlaceBehavior.SHARE) {
            helper.fail("Expected friendly social place behavior to share, got " + memory);
            return;
        }
        helper.kill(observer);
        helper.kill(friend);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void tenseMoeAvoidsFriendsRememberedHangout(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        UUID owner = new UUID(1604L, 2604L);
        Moe observer = spawnMoe(helper, level, owner, new BlockPos(7, 1, 1));
        Moe friend = spawnMoe(helper, level, owner, new BlockPos(5, 1, 1));
        observer.setBloodType("A");
        observer.setDere("DANDERE");
        friend.setBloodType("B");
        friend.setDere("TSUNDERE");

        BlockPos garden = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(garden.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        try {
            insertSimpleDataBlock(BlockPartyDB.get(level), "GardenLanterns", owner, level, garden);
        } catch (SQLException exception) {
            helper.fail("Expected tense hangout setup to succeed: " + exception.getMessage());
            return;
        }
        MoePlaceMemory.Place place = friend.observePlaceNow().orElse(null);
        if (place == null || place.type() != MoePlaceMemory.PlaceType.GARDEN) {
            helper.fail("Expected friend to remember garden hangout, got " + place);
            return;
        }

        Vec3 center = Vec3.atBottomCenterOf(place.pos());
        Vec3 destination = observer.idleRoutineDestination();
        if (destination == null || destination.distanceToSqr(center) <= observer.position().distanceToSqr(center)) {
            helper.fail("Expected tense observer to move away from friend's remembered garden, got " + destination);
            return;
        }
        Moe.MoeSocialPlaceMemory memory = observer.socialPlaceMemoryForTests().orElse(null);
        if (memory == null || memory.behavior() != MoeSocialRules.SocialPlaceBehavior.AVOID) {
            helper.fail("Expected tense social place behavior to avoid, got " + memory);
            return;
        }
        helper.kill(observer);
        helper.kill(friend);
        helper.succeed();
    }

    private static Moe spawnMoe(GameTestHelper helper, ServerLevel level, UUID owner, BlockPos relativeSource) {
        return MovementGameTestSupport.spawnMoe(helper, level, owner, relativeSource);
    }

    private static void assertGreater(GameTestHelper helper, float actual, float threshold, String label) {
        if (actual <= threshold) {
            helper.fail("Expected " + label + " to be greater than " + threshold + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
