package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.entities.movement.FollowSession;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.movement.MoeAnchorType;
import block_party.entities.movement.PlayerMovementContext;
import block_party.entities.movement.PlayerMovementDecision;
import block_party.entities.movement.PlayerMovementDecisions;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.PlayerMovementRequest;
import block_party.entities.movement.RoutineIntent;
import block_party.items.CustomSpawnEggItem;
import block_party.items.InviteItem;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomEntities;
import block_party.registry.CustomItems;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class PlayerMovementGameTests {
    private PlayerMovementGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void playerMovementContextUsesRelationshipAndLiveWellbeing(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = new UUID(1701L, 2701L);
        Moe moe = spawnMoe(helper, level, player, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setStress(8.0F);
        moe.setRelaxation(3.0F);
        try {
            db.setPlayerFeelings(moe.getDatabaseID(), player, 4.0F, 12.0F);
            NPC row = db.findNpc(moe.getDatabaseID()).orElseThrow();
            PlayerRelationship relationship = db.findPlayerRelationship(moe.getDatabaseID(), player).orElseThrow();
            PlayerMovementRequest request = PlayerMovementRequest.phoneCall(
                    player,
                    moe.getDatabaseID(),
                    level.dimension(),
                    moe.position().add(new Vec3(3.0D, 0.0D, 0.0D)),
                    0.0F);
            PlayerMovementContext context = PlayerMovementContext.from(request, row, relationship, moe);
            if (!context.sameDimension() || context.distanceSqr() <= 0.0D) {
                helper.fail("Expected player movement context to measure same-dimension distance");
                return;
            }
            assertFloat(helper, 4.0F, context.affection(), "relationship affection");
            assertFloat(helper, 12.0F, context.loyalty(), "relationship loyalty");
            assertFloat(helper, 8.0F, context.stress(), "live stress");
            assertFloat(helper, 3.0F, context.relaxation(), "live relaxation");
            if (context.following() || context.followTicksRemaining() != 0 || context.followCanChangeDimension()) {
                helper.fail("Expected idle Moe to have no active follow session in movement context");
                return;
            }

            PlayerMovementRequest netherRequest = PlayerMovementRequest.phoneCall(player, moe.getDatabaseID(), Level.NETHER, moe.position(), 0.0F);
            PlayerMovementContext netherContext = PlayerMovementContext.from(netherRequest, row, relationship, moe);
            if (netherContext.sameDimension() || !Double.isInfinite(netherContext.distanceSqr())) {
                helper.fail("Expected cross-dimension movement context to avoid fake distance");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected movement context setup to succeed: " + exception.getMessage());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void phoneCallDecisionRequiresContactAndLoyalty(GameTestHelper helper) {
        PlayerMovementDecision noContact = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PHONE_CALL, false, true, 20.0F, 0.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.VOICEMAIL, PlayerMovementDecision.Reason.NO_PHONE_CONTACT, noContact, "no contact phone call");

        PlayerMovementDecision lowLoyalty = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PHONE_CALL, true, true, 2.0F, 0.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.VOICEMAIL, PlayerMovementDecision.Reason.LOW_LOYALTY, lowLoyalty, "low loyalty phone call");

        PlayerMovementDecision accepted = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PHONE_CALL, true, true, 12.0F, 3.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.ACCEPTED, PlayerMovementDecision.Reason.NONE, accepted, "loyal phone call");
        if (accepted.followTicks() <= 0 || accepted.canChangeDimension()) {
            helper.fail("Expected same-dimension accepted phone call to set a local follow duration");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void crossDimensionPhoneCallRequiresHigherLoyalty(GameTestHelper helper) {
        PlayerMovementDecision refused = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PHONE_CALL, true, false, 12.0F, 0.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.VOICEMAIL, PlayerMovementDecision.Reason.DIFFERENT_DIMENSION, refused, "cross-dimension moderate loyalty call");

        PlayerMovementDecision accepted = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PHONE_CALL, true, false, 18.0F, 0.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.ACCEPTED, PlayerMovementDecision.Reason.NONE, accepted, "cross-dimension high loyalty call");
        if (!accepted.canChangeDimension()) {
            helper.fail("Expected high-loyalty cross-dimension call to allow dimension change");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void partyInviteDurationScalesWithLoyalty(GameTestHelper helper) {
        PlayerMovementDecision low = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PARTY_INVITE, true, true, 9.0F, 0.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.REFUSED, PlayerMovementDecision.Reason.LOW_LOYALTY, low, "low loyalty party invite");

        PlayerMovementDecision modest = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PARTY_INVITE, true, true, 10.0F, 0.0F, 0.0F));
        PlayerMovementDecision loyal = PlayerMovementDecisions.decide(context(PlayerMovementIntent.PARTY_INVITE, true, true, 18.0F, 6.0F, 0.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.ACCEPTED, PlayerMovementDecision.Reason.NONE, modest, "modest party invite");
        assertDecision(helper, PlayerMovementDecision.Outcome.ACCEPTED, PlayerMovementDecision.Reason.NONE, loyal, "loyal party invite");
        if (loyal.followTicks() <= modest.followTicks()) {
            helper.fail("Expected higher loyalty and affection to produce a longer party duration");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void stressBlocksMovementUnlessLoyaltyIsVeryHigh(GameTestHelper helper) {
        PlayerMovementDecision stressed = PlayerMovementDecisions.decide(context(PlayerMovementIntent.FOLLOW_REQUEST, true, true, 12.0F, 0.0F, 20.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.REFUSED, PlayerMovementDecision.Reason.TOO_STRESSED, stressed, "stressed follow request");

        PlayerMovementDecision loyal = PlayerMovementDecisions.decide(context(PlayerMovementIntent.FOLLOW_REQUEST, true, true, 18.0F, 0.0F, 20.0F));
        assertDecision(helper, PlayerMovementDecision.Outcome.ACCEPTED, PlayerMovementDecision.Reason.NONE, loyal, "high loyalty stressed follow request");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void followSessionTracksPlayerIntentAndDuration(GameTestHelper helper) {
        UUID player = new UUID(1703L, 2703L);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.startFollowSession(player, PlayerMovementIntent.PHONE_CALL, 2, true);
        FollowSession session = moe.getFollowSession();
        if (!moe.isFollowing()
                || !player.equals(session.playerUuid())
                || session.intent() != PlayerMovementIntent.PHONE_CALL
                || session.ticksRemaining() != 2
                || !session.canChangeDimension()) {
            helper.fail("Expected started follow session to mirror player, intent, duration, and dimension permission");
            return;
        }
        moe.tickFollowSession();
        if (!moe.isFollowing() || moe.getFollowTicksRemaining() != 1) {
            helper.fail("Expected follow session to count down while remaining active");
            return;
        }
        moe.tickFollowSession();
        if (moe.isFollowing() || moe.getFollowTicksRemaining() != 0) {
            helper.fail("Expected follow session to clear when duration expires");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void legacyFollowingBooleanCreatesCompatibilitySession(GameTestHelper helper) {
        UUID player = new UUID(1704L, 2704L);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.setPlayerUUID(player);
        moe.setFollowing(true);
        if (!moe.isFollowing()
                || !player.equals(moe.getFollowPlayerUUID())
                || moe.getFollowIntent() != PlayerMovementIntent.FOLLOW_REQUEST
                || moe.getFollowTicksRemaining() <= 0
                || moe.canFollowAcrossDimensions()) {
            helper.fail("Expected legacy following=true to create a local compatibility follow session");
            return;
        }
        moe.setFollowing(false);
        if (moe.isFollowing()) {
            helper.fail("Expected legacy following=false to clear the follow session");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void inviteItemTogglesOpenClosedState(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack invite = new ItemStack(CustomItems.ENTRIES.get("invite").get());
        player.setItemInHand(InteractionHand.MAIN_HAND, invite);
        if (!(invite.getItem() instanceof InviteItem) || InviteItem.isClosed(invite)) {
            helper.fail("Expected invite item to start open");
            return;
        }

        invite.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        if (!InviteItem.isClosed(invite)) {
            helper.fail("Expected invite item use to close the invite");
            return;
        }
        invite.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
        if (InviteItem.isClosed(invite)) {
            helper.fail("Expected invite item use to reopen the invite");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void loyalPlayerInviteStartsPartyFollowSession(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1705L, 2705L);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        try {
            db.ensurePlayerRelationship(moe.getDatabaseID(), player.getUUID());
            db.setPlayerFeelings(moe.getDatabaseID(), player.getUUID(), 2.0F, 12.0F);
        } catch (SQLException exception) {
            helper.fail("Expected party invite relationship setup to succeed: " + exception.getMessage());
            return;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(CustomItems.ENTRIES.get("invite").get()));

        InteractionResult result = moe.interactAt(player, Vec3.ZERO, InteractionHand.MAIN_HAND);

        assertEquals(helper, InteractionResult.SUCCESS, result, "party invite interaction result");
        if (!moe.isFollowing()
                || moe.getFollowIntent() != PlayerMovementIntent.PARTY_INVITE
                || !player.getUUID().equals(moe.getFollowPlayerUUID())
                || moe.getFollowTicksRemaining() <= 0) {
            helper.fail("Expected loyal party invite to start a player-bound party follow session");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void lowLoyaltyInviteDoesNotStartPartyFollowSession(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1706L, 2706L);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = spawnMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        try {
            db.ensurePlayerRelationship(moe.getDatabaseID(), player.getUUID());
            db.setPlayerFeelings(moe.getDatabaseID(), player.getUUID(), 0.0F, 3.0F);
        } catch (SQLException exception) {
            helper.fail("Expected low-loyalty party invite setup to succeed: " + exception.getMessage());
            return;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(CustomItems.ENTRIES.get("invite").get()));

        moe.interactAt(player, Vec3.ZERO, InteractionHand.MAIN_HAND);

        if (moe.isFollowing() || moe.getFollowIntent() == PlayerMovementIntent.PARTY_INVITE) {
            helper.fail("Expected low-loyalty party invite to be refused without joining");
            return;
        }
        if (!moe.hasDialogue() || !moe.getDialogue().text().contains("Maybe")) {
            helper.fail("Expected low-loyalty party invite to open refusal dialogue");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void partyFollowDriftsTowardRoutineWhenSessionIsThin(GameTestHelper helper) {
        UUID owner = new UUID(1707L, 2707L);
        UUID playerId = new UUID(1708L, 2708L);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)), 0.0F, 0.0F);
        player.moveTo(helper.absolutePos(new BlockPos(4, 1, 1)), 0.0F, 0.0F);
        moe.setPlayerUUID(owner);
        moe.setHome(new block_party.db.DimBlockPos(helper.getLevel().dimension(), helper.absolutePos(new BlockPos(8, 1, 1))));
        moe.setHasHome(true);
        moe.startFollowSession(playerId, PlayerMovementIntent.PARTY_INVITE, 20 * 20, false, false);

        if (moe.followRoutineDriftWeight() < 0.35F) {
            helper.fail("Expected ending party session to develop routine drift");
            return;
        }
        Vec3 destination = moe.routineAwareFollowDestination(player);
        if (destination == null || destination.distanceToSqr(Vec3.atBottomCenterOf(moe.getHome().getPos())) > 0.0001D) {
            helper.fail("Expected nearby player with thin party session to let Moe drift toward routine");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void partyFollowPrioritizesCatchingUpOverRoutine(GameTestHelper helper) {
        UUID owner = new UUID(1709L, 2709L);
        UUID playerId = new UUID(1710L, 2710L);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = new Moe(CustomEntities.MOE.get(), helper.getLevel());
        moe.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)), 0.0F, 0.0F);
        player.moveTo(helper.absolutePos(new BlockPos(40, 1, 1)), 0.0F, 0.0F);
        moe.setPlayerUUID(owner);
        moe.setHome(new block_party.db.DimBlockPos(helper.getLevel().dimension(), helper.absolutePos(new BlockPos(8, 1, 1))));
        moe.setHasHome(true);
        moe.startFollowSession(playerId, PlayerMovementIntent.PARTY_INVITE, 20 * 20, false, false);

        Vec3 destination = moe.routineAwareFollowDestination(player);
        if (destination == null || destination.distanceToSqr(player.position()) > 0.0001D) {
            helper.fail("Expected far player to override routine drift");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void routineAnchorResolverPrefersActiveLocationSchedule(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1711L, 2711L);
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)), 0.0F, 0.0F);
        moe.setPlayerUUID(owner);
        moe.setHome(new block_party.db.DimBlockPos(level.dimension(), helper.absolutePos(new BlockPos(2, 1, 1))));
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
            insertSimpleDataBlock(db, "GardenLanterns", owner, level, helper.absolutePos(new BlockPos(5, 1, 1)));
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
            insertSimpleDataBlock(db, "GardenLanterns", owner, level, anchorPos);
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
            insertSimpleDataBlock(db, "GardenLanterns", owner, level, anchorPos);
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
            insertSimpleDataBlock(db, "GardenLanterns", owner, level, helper.absolutePos(new BlockPos(10, 1, 1)));
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
            insertSimpleDataBlock(db, "Saplings", owner, level, helper.absolutePos(new BlockPos(12, 1, 1)));
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
            insertSimpleDataBlock(db, "GardenLanterns", owner, level, helper.absolutePos(new BlockPos(6, 1, 1)));
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

    private static Moe spawnMoe(GameTestHelper helper, ServerLevel level, UUID player, BlockPos relativeSource) {
        BlockPos source = helper.absolutePos(relativeSource);
        BlockState state = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, state, 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, player);
        if (moe == null) {
            helper.fail("Expected test Moe to spawn");
        }
        return moe;
    }

    private static void assertFloat(GameTestHelper helper, float expected, float actual, String label) {
        if (Math.abs(expected - actual) > 0.0001F) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void insertLocation(BlockPartyDB db, UUID owner, ServerLevel level, BlockPos pos, String condition, int priority) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO Locations (PosDim, PosX, PosY, PosZ, PlayerUUID, RequiredCondition, Priority)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """)) {
            bindPosition(statement, level, pos);
            statement.setString(5, owner.toString());
            statement.setString(6, condition);
            statement.setInt(7, priority);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static void insertSimpleDataBlock(BlockPartyDB db, String table, UUID owner, ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (PosDim, PosX, PosY, PosZ, PlayerUUID)
                VALUES (?, ?, ?, ?, ?);
                """.formatted(table))) {
            bindPosition(statement, level, pos);
            statement.setString(5, owner.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static void bindPosition(PreparedStatement statement, ServerLevel level, BlockPos pos) throws SQLException {
        statement.setString(1, level.dimension().location().toString());
        statement.setInt(2, pos.getX());
        statement.setInt(3, pos.getY());
        statement.setInt(4, pos.getZ());
    }

    private static PlayerMovementContext context(PlayerMovementIntent intent, boolean phoneContact, boolean sameDimension,
                                                 float loyalty, float affection, float stress) {
        return new PlayerMovementContext(
                new PlayerMovementRequest(intent, new UUID(1702L, 2702L), 1702L, Level.OVERWORLD, Vec3.ZERO, 0.0F),
                true,
                true,
                phoneContact,
                sameDimension,
                sameDimension ? 9.0D : Double.POSITIVE_INFINITY,
                affection,
                loyalty,
                stress,
                0.0F,
                false,
                0,
                false,
                false,
                false);
    }

    private static void assertDecision(GameTestHelper helper, PlayerMovementDecision.Outcome outcome,
                                       PlayerMovementDecision.Reason reason, PlayerMovementDecision actual, String label) {
        if (actual.outcome() != outcome || actual.reason() != reason) {
            helper.fail("Expected " + label + " to be " + outcome + "/" + reason + ", got " + actual);
        }
    }
}
