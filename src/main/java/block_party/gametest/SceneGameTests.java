package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.goals.HideUntil;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import block_party.registry.resources.ScenesReloadListener;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.scene.Response;
import block_party.scene.SceneAction;
import block_party.scene.SceneTrigger;
import block_party.scene.SceneVariables;
import block_party.scene.actions.OpenInventoryAction;
import block_party.db.voicemail.Voicemails;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class SceneGameTests {
    private SceneGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void rightClickSceneOpensBundledDialogue(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(501L, 1L));

        moe.triggerScene(SceneTrigger.RIGHT_CLICK);
        moe.tick();

        if (!moe.hasDialogue()) {
            helper.fail("Expected right-click scene to open dialogue");
            return;
        }
        if (!moe.getDialogue().text().contains("I'm " + moe.getGivenName())) {
            helper.fail("Expected dialogue text to substitute Moe name, got " + moe.getDialogue().text());
            return;
        }
        if (!moe.getDialogue().responses().containsKey(Response.NEXT_RESPONSE)) {
            helper.fail("Expected bundled dialogue to expose NEXT_RESPONSE");
            return;
        }
        assertEquals(helper, "WAVE", moe.getAnimationKey(), "right-click dialogue animation");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueMarkdownAndVariableSubstitutionAreApplied(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(510L, 10L));
        SceneVariables variables = SceneVariables.get(moe.level());
        variables.cookies(moe.getDatabaseID()).set("mood", "bright");
        variables.counters(moe.getDatabaseID()).set("score", 7);
        SceneAction action = parseAction("""
                {"type":"block_party:send_dialogue","action":{"text":"<color=cyan>Hello</color> @name @mood #score","responses":[{"icon":"block_party:next_response","text":"<b>Go</b>","actions":["block_party:end"]}]}}
                """);

        action.apply(moe);

        assertEquals(helper, "\u00a7bHello\u00a7f " + moe.getGivenName() + " \u00a7b\u00a7lbright\u00a7r\u00a7f \u00a7e\u00a7l7\u00a7r\u00a7f", moe.getDialogue().text(), "markdown dialogue text");
        assertEquals(helper, "\u00a7lGo\u00a7r", moe.getDialogue().responses().get(Response.NEXT_RESPONSE), "markdown response text");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void forgeSceneObservationsFilterScenes(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(511L, 11L));
        moe.setVisibleBlockState(Blocks.BELL.defaultBlockState());
        moe.setDere("KUUDERE");
        moe.setEmotion("HAPPY");
        moe.setGender("NONBINARY");

        block_party.registry.resources.ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":["block_party:is_cardinal","block_party:if_kuudere","block_party:if_happy","block_party:if_nonbinary"],"actions":[]}
                """);
        block_party.registry.resources.ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":["block_party:is_corporeal"],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected restored Forge observations to accept matching Moe state");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected restored Forge observations to reject mismatched Moe state");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void phoneCallSceneTriggerParses(GameTestHelper helper) {
        ScenesReloadListener.ParsedScene parsed = parseScene("""
                {"trigger":"block_party:phone_call","filters":["block_party:always"],"actions":[]}
                """);

        assertEquals(helper, SceneTrigger.PHONE_CALL, parsed.trigger(), "phone call scene trigger");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void movementSceneTriggersParse(GameTestHelper helper) {
        assertEquals(helper, SceneTrigger.FOLLOW_STARTED, parseScene("""
                {"trigger":"block_party:follow_started","filters":["block_party:always"],"actions":[]}
                """).trigger(), "follow started scene trigger");
        assertEquals(helper, SceneTrigger.FOLLOW_ENDED, parseScene("""
                {"trigger":"block_party:follow_ended","filters":["block_party:always"],"actions":[]}
                """).trigger(), "follow ended scene trigger");
        assertEquals(helper, SceneTrigger.PARTY_INVITE, parseScene("""
                {"trigger":"block_party:party_invite","filters":["block_party:always"],"actions":[]}
                """).trigger(), "party invite scene trigger");
        assertEquals(helper, SceneTrigger.GIFT_RECEIVED, parseScene("""
                {"trigger":"block_party:gift_received","filters":["block_party:always"],"actions":[]}
                """).trigger(), "gift received scene trigger");
        assertEquals(helper, SceneTrigger.WAIT, parseScene("""
                {"trigger":"block_party:wait","filters":["block_party:always"],"actions":[]}
                """).trigger(), "wait scene trigger");
        assertEquals(helper, SceneTrigger.DISMISS, parseScene("""
                {"trigger":"block_party:dismiss","filters":["block_party:always"],"actions":[]}
                """).trigger(), "dismiss scene trigger");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void structuredSceneObservationFactoriesMatchActiveMoeState(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(512L, 12L));
        moe.setHealth(12.0F);
        moe.setFoodLevel(9.0F);
        moe.setGivenName("Akemi");
        SceneVariables variables = SceneVariables.get(moe.level());
        variables.cookies(moe.getDatabaseID()).set("mood", "sunny");
        variables.counters(moe.getDatabaseID()).set("score", 3);
        variables.playerCookies(moe.getOwnerUUID()).set("chapter", "intro");
        variables.playerCounters(moe.getOwnerUUID()).set("visits", 2);
        variables.worldCookies().set("festival", "active");
        variables.worldCounters().set("day", 4);

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:health","filter":{"operation":"at_least","value":10}},
                  {"type":"block_party:food_level","filter":{"operation":"less_than","value":10}},
                  {"type":"block_party:counter","filter":{"name":"score","operation":"equals","value":3}},
                  {"type":"block_party:has_cookie","filter":{"name":"mood","operation":"equals","value":"sunny"}},
                  {"type":"block_party:player_counter","filter":{"name":"visits","operation":"equals","value":2}},
                  {"type":"block_party:player_has_cookie","filter":{"name":"chapter","operation":"equals","value":"intro"}},
                  {"type":"block_party:world_counter","filter":{"name":"day","operation":"at_least","value":4}},
                  {"type":"block_party:world_has_cookie","filter":{"name":"festival","operation":"equals","value":"active"}},
                  {"type":"block_party:block","filter":{"name":"block_party:sakura_log"}},
                  {"type":"block_party:name","filter":{"operation":"prefix","value":"Ake"}},
                  {"type":"block_party:self","filter":{"name":"block_party:moe"}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:counter","filter":{"name":"score","operation":"greater_than","value":9}}
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected structured scene filters to accept matching Moe state");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected structured scene filters to reject mismatched Moe state");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void environmentalSceneFiltersMatchPlaceMemoryAndObservation(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(525L, 25L));
        ServerLevel level = helper.getLevel();
        BlockPos houseSpot = helper.absolutePos(new BlockPos(4, 1, 1));
        MovementGameTestSupport.buildLitDoorShelter(level, houseSpot);
        level.setBlock(houseSpot.above(2), Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(houseSpot.north(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.south(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.west(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.east(), Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(houseSpot.west().south(), Blocks.OAK_DOOR.defaultBlockState(), 3);
        level.setBlock(helper.absolutePos(new BlockPos(2, 1, 1)), Blocks.OAK_LOG.defaultBlockState(), 3);

        if (moe.observePlaceNow().isEmpty()) {
            helper.fail("Expected Moe to remember the nearby lit door shelter");
            return;
        }
        if (moe.rememberedPlace().isEmpty()) {
            helper.fail("Expected Moe to remember a place before scene filtering");
            return;
        }
        if (moe.observeEnvironmentNow().isEmpty()) {
            helper.fail("Expected Moe to remember the nearby environmental block observation");
            return;
        }
        if (moe.latestEnvironmentalObservation().isEmpty()) {
            helper.fail("Expected Moe latest environmental observation to be available before scene filtering");
            return;
        }

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  "block_party:if_remembers_place",
                  "block_party:if_has_environmental_observation",
                  {"type":"block_party:remembered_place_score","filter":{"operation":"greater_than","value":40}},
                  {"type":"block_party:remembered_place_capacity","filter":{"operation":"at_least","value":2}},
                  {"type":"block_party:observed_signal_layer","filter":{"value":"block"}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  "block_party:if_remembers_garden"
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected environmental scene filters to accept remembered house and environmental observation");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected environmental scene filters to reject mismatched remembered place type");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void unimplementedSceneObservationIdsFailClosed(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(513L, 13L));
        ScenesReloadListener.ParsedScene unknown = parseScene("""
                {"trigger":"block_party:right_click","filters":["block_party:not_a_real_filter"],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene familyName = parseScene("""
                {"trigger":"block_party:right_click","filters":[{"type":"block_party:family_name","filter":{"value":"Suzu"}}],"actions":[]}
                """);
        moe.setBlockState(Blocks.BELL.defaultBlockState());

        if (unknown.scene().fulfills(moe)) {
            helper.fail("Expected unknown scene filters to fail closed");
            return;
        }
        if (!familyName.scene().fulfills(moe)) {
            helper.fail("Expected family name scene filter to match translated Moe family name");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownerRightClickInteractionTriggersScene(GameTestHelper helper) {
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = spawnMoe(helper, owner.getUUID());

        InteractionResult result = moe.interactAt(owner, Vec3.ZERO, InteractionHand.MAIN_HAND);
        moe.tick();

        assertEquals(helper, InteractionResult.SUCCESS, result, "owner right-click interaction result");
        if (!moe.hasDialogue()) {
            helper.fail("Expected owner right-click interaction to open bundled dialogue");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void movementSceneFiltersMatchFollowSessionAndTargetRelationship(GameTestHelper helper) {
        UUID owner = new UUID(514L, 14L);
        UUID target = new UUID(515L, 15L);
        Moe moe = spawnMoe(helper, owner);
        moe.setDialogueTarget(target);
        moe.startFollowSession(target, PlayerMovementIntent.PHONE_CALL, 120, true, false);
        try {
            BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
            db.setPhoneContact(moe.getDatabaseID(), target, true);
            db.setYearbookSigned(moe.getDatabaseID(), target, true);
            db.setPlayerFeelings(moe.getDatabaseID(), target, 4.0F, 9.0F);
        } catch (SQLException exception) {
            helper.fail("Expected relationship setup to succeed: " + exception.getMessage());
            return;
        }

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:phone_call","filters":[
                  "block_party:is_following",
                  "block_party:can_follow_across_dimensions",
                  {"type":"block_party:follow_intent","filter":{"value":"phone_call"}},
                  {"type":"block_party:follow_ticks_remaining","filter":{"operation":"at_least","value":100}},
                  {"type":"block_party:follow_player_is_target"},
                  {"type":"block_party:target_phone_contact"},
                  {"type":"block_party:target_yearbook_signed"},
                  {"type":"block_party:target_affection","filter":{"operation":"at_least","value":4}},
                  {"type":"block_party:target_loyalty","filter":{"operation":"at_least","value":9}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:phone_call","filters":[
                  {"type":"block_party:target_loyalty","filter":{"operation":"less_than","value":3}}
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected movement scene filters to accept follow session and target relationship state");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected movement scene filters to reject mismatched target relationship state");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void nonOwnerRightClickCreatesRelationshipAndTargetsDialogue(GameTestHelper helper) {
        UUID owner = new UUID(505L, 5L);
        Player other = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = spawnMoe(helper, owner);

        InteractionResult result = moe.interactAt(other, Vec3.ZERO, InteractionHand.MAIN_HAND);
        moe.tick();

        assertEquals(helper, InteractionResult.SUCCESS, result, "non-owner right-click interaction result");
        try {
            if (BlockPartyDB.get(helper.getLevel()).findPlayerRelationship(moe.getDatabaseID(), other.getUUID()).isEmpty()) {
                helper.fail("Expected non-owner right-click to create a player relationship");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected relationship lookup to succeed: " + exception.getMessage());
            return;
        }
        assertEquals(helper, other.getUUID(), moe.getDialogueTarget(), "dialogue target");
        if (!moe.hasDialogue()) {
            helper.fail("Expected non-owner right-click interaction to open bundled dialogue");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void offhandRightClickPassesWithoutTriggeringScene(GameTestHelper helper) {
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        Moe moe = spawnMoe(helper, owner.getUUID());

        InteractionResult result = moe.interactAt(owner, Vec3.ZERO, InteractionHand.OFF_HAND);

        assertEquals(helper, InteractionResult.PASS, result, "offhand right-click interaction result");
        assertEquals(helper, SceneTrigger.NULL, moe.sceneManager().getTriggerForTests(), "offhand trigger state");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownerShiftLeftClickTriggersShiftLeftSceneWithoutDamage(GameTestHelper helper) {
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        owner.setShiftKeyDown(true);
        Moe moe = spawnMoe(helper, owner.getUUID());
        float health = moe.getHealth();

        boolean consumed = moe.skipAttackInteraction(owner);

        assertEquals(helper, true, consumed, "owner shift-left interaction consumed");
        assertEquals(helper, SceneTrigger.SHIFT_LEFT_CLICK, moe.sceneManager().getTriggerForTests(), "owner shift-left trigger");
        assertEquals(helper, health, moe.getHealth(), "owner shift-left health");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void nonOwnerDamageTriggersHurtSceneAfterDamage(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(506L, 6L));
        float health = moe.getHealth();

        boolean hurt = moe.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 1.0F);

        assertEquals(helper, true, hurt, "non-owner damage result");
        if (!(moe.getHealth() < health)) {
            helper.fail("Expected non-owner damage to reduce health");
            return;
        }
        assertEquals(helper, SceneTrigger.HURT, moe.sceneManager().getTriggerForTests(), "non-owner hurt trigger");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void successfulMoeAttackTriggersAttackScene(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Moe attacker = spawnMoe(helper, new UUID(507L, 7L));
        Moe target = spawnMoeAt(helper, new UUID(508L, 8L), new BlockPos(4, 1, 1));

        if (!attacker.doHurtTarget(level, target)) {
            helper.fail("Expected Moe attack to succeed");
            return;
        }

        assertEquals(helper, SceneTrigger.ATTACK, attacker.sceneManager().getTriggerForTests(), "attack trigger");
        helper.kill(attacker);
        helper.kill(target);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void serverAiHooksCanTriggerRandomTickAndStareScenes(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(509L, 9L));

        moe.triggerServerSceneHooks(true, false);
        assertEquals(helper, SceneTrigger.RANDOM_TICK, moe.sceneManager().getTriggerForTests(), "random tick trigger");

        moe.triggerServerSceneHooks(false, true);
        assertEquals(helper, SceneTrigger.STARE, moe.sceneManager().getTriggerForTests(), "stare trigger");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void lowerPriorityTriggerDoesNotInterruptActiveScene(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(502L, 2L));

        moe.triggerScene(SceneTrigger.RIGHT_CLICK);
        moe.triggerScene(SceneTrigger.EVERY_TICK);
        moe.tick();

        if (!moe.hasDialogue()) {
            helper.fail("Expected active right-click scene to survive lower-priority trigger");
            return;
        }
        if (moe.sceneManager().getTriggerForTests() != SceneTrigger.RIGHT_CLICK) {
            helper.fail("Expected active trigger to remain RIGHT_CLICK, got " + moe.sceneManager().getTriggerForTests());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueResponseRunsNestedAction(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(503L, 3L));
        moe.triggerScene(SceneTrigger.RIGHT_CLICK);
        moe.tick();

        moe.setResponse(Response.NEXT_RESPONSE);
        tickScene(moe, 3);

        if (!moe.hasDialogue()) {
            helper.fail("Expected nested response action to open the next dialogue");
            return;
        }
        if (!moe.getDialogue().text().contains("my developer hasn't thought")) {
            helper.fail("Expected second bundled dialogue, got " + moe.getDialogue().text());
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueEndResetsSceneAnimation(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(528L, 28L));
        moe.triggerScene(SceneTrigger.RIGHT_CLICK);
        moe.tick();

        assertEquals(helper, "WAVE", moe.getAnimationKey(), "initial dialogue animation");
        moe.setResponse(Response.NEXT_RESPONSE);
        tickScene(moe, 3);
        moe.setResponse(Response.NEXT_RESPONSE);
        tickScene(moe, 3);
        moe.setResponse(Response.NEXT_RESPONSE);
        tickScene(moe, 3);

        if (moe.hasDialogue()) {
            helper.fail("Expected final dialogue response to close the dialogue");
            return;
        }
        assertEquals(helper, "DEFAULT", moe.getAnimationKey(), "dialogue end animation");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void leftClickSceneHidesMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Moe moe = spawnMoe(helper, new UUID(504L, 4L));
        BlockPos pos = moe.blockPosition();

        moe.triggerScene(SceneTrigger.LEFT_CLICK);
        moe.tick();

        List<MoeInHiding> hidden = level.getEntitiesOfClass(MoeInHiding.class, new AABB(pos).inflate(2.0D));
        if (hidden.size() != 1) {
            helper.fail("Expected left-click scene to create one MoeInHiding, got " + hidden.size());
            return;
        }
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, hidden.getFirst().getHideUntil(), "left-click hide until");
        assertEquals(helper, CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), level.getBlockState(pos), "restored hidden block");
        helper.kill(hidden.getFirst());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void cookieAndCounterActionsMutateSceneVariables(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(505L, 5L));
        SceneAction cookie = parseAction("{\"type\":\"block_party:cookie\",\"action\":{\"name\":\"phase5_cookie\",\"value\":\"yes\"}}");
        SceneAction counter = parseAction("{\"type\":\"block_party:counter\",\"action\":{\"name\":\"phase5_counter\",\"operation\":\"add\",\"value\":3}}");
        SceneAction playerCookie = parseAction("{\"type\":\"block_party:player_cookie\",\"action\":{\"name\":\"phase5_player_cookie\",\"value\":\"seen\"}}");
        SceneAction playerCounter = parseAction("{\"type\":\"block_party:counter\",\"action\":{\"scope\":\"player\",\"name\":\"phase5_player_counter\",\"operation\":\"set\",\"value\":8}}");
        SceneAction worldCounter = parseAction("{\"type\":\"block_party:world_counter\",\"action\":{\"name\":\"phase5_world_counter\",\"operation\":\"add\",\"value\":2}}");

        cookie.apply(moe);
        counter.apply(moe);
        playerCookie.apply(moe);
        playerCounter.apply(moe);
        worldCounter.apply(moe);

        SceneVariables variables = SceneVariables.get(moe.level());
        assertEquals(helper, "yes", variables.cookies(moe.getDatabaseID()).get("phase5_cookie"), "scene cookie value");
        assertEquals(helper, 3, variables.counters(moe.getDatabaseID()).get("phase5_counter"), "scene counter value");
        assertEquals(helper, "seen", variables.playerCookies(moe.getOwnerUUID()).get("phase5_player_cookie"), "player scene cookie value");
        assertEquals(helper, 8, variables.playerCounters(moe.getOwnerUUID()).get("phase5_player_counter"), "player scene counter value");
        assertEquals(helper, 2, variables.worldCounters().get("phase5_world_counter"), "world scene counter value");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void statSceneActionsMutateMoeState(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(522L, 22L));
        moe.setHealth(10.0F);
        moe.setFoodLevel(8.0F);
        moe.setLoyalty(2.0F);
        moe.setStress(5.0F);

        parseAction("{\"type\":\"block_party:health\",\"action\":{\"operation\":\"add\",\"value\":3}}").apply(moe);
        parseAction("{\"type\":\"block_party:food_level\",\"action\":{\"operation\":\"set\",\"value\":12}}").apply(moe);
        parseAction("{\"type\":\"block_party:loyalty\",\"action\":{\"operation\":\"add\",\"value\":4}}").apply(moe);
        parseAction("{\"type\":\"block_party:stress\",\"action\":{\"operation\":\"subtract\",\"value\":2}}").apply(moe);

        assertEquals(helper, 13.0F, moe.getHealth(), "scene health action");
        assertEquals(helper, 12.0F, moe.getFoodLevel(), "scene food level action");
        assertEquals(helper, 6.0F, moe.getLoyalty(), "scene loyalty action");
        assertEquals(helper, 3.0F, moe.getStress(), "scene stress action");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void createVoicemailActionStoresDelayedMessage(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(506L, 6L));
        SceneAction action = parseAction("{\"type\":\"block_party:create_voicemail\",\"action\":{\"text\":\"Call me back, @name.\",\"speaker\":{\"emotion\":\"happy\"},\"sound\":\"block_party:item.cell_phone.dial\",\"delay_minutes\":90}}");

        action.apply(moe);

        Voicemails.Entry entry = Voicemails.get(moe.level()).allForTests().getFirst();
        assertEquals(helper, moe.getOwnerUUID(), entry.owner(), "voicemail owner");
        assertEquals(helper, moe.getDatabaseID(), entry.npcId(), "voicemail NPC id");
        assertEquals(helper, "Call me back, @name.", entry.text(), "voicemail text");
        assertEquals(helper, "HAPPY", entry.speaker().emotion(), "voicemail speaker emotion");
        assertEquals(helper, BlockParty.source("item.cell_phone.dial"), entry.sound(), "voicemail sound");
        if (entry.availableAt() - entry.createdAt() != 90L * 60L * 1000L) {
            helper.fail("Expected voicemail delay to be 90 minutes");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void movementSceneActionsStartAndClearFollowSession(GameTestHelper helper) {
        UUID owner = new UUID(516L, 16L);
        UUID target = new UUID(517L, 17L);
        Moe moe = spawnMoe(helper, owner);
        moe.setDialogueTarget(target);
        SceneAction start = parseAction("""
                {"type":"block_party:start_follow_session","action":{"intent":"party_invite","ticks":80,"can_change_dimension":true}}
                """);
        SceneAction clear = parseAction("""
                {"type":"block_party:clear_follow_session"}
                """);

        start.apply(moe);
        if (!moe.isFollowing()
                || !target.equals(moe.getFollowPlayerUUID())
                || moe.getFollowIntent() != PlayerMovementIntent.PARTY_INVITE
                || moe.getFollowTicksRemaining() != 80
                || !moe.canFollowAcrossDimensions()) {
            helper.fail("Expected start_follow_session action to create a target-bound party invite session");
            return;
        }
        clear.apply(moe);
        if (moe.isFollowing()) {
            helper.fail("Expected clear_follow_session action to dismiss the active session");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void inventorySceneActionParsesAsOpenInventoryCommand(GameTestHelper helper) {
        SceneAction openInventory = parseAction("{\"type\":\"block_party:open_inventory\"}");

        if (openInventory != OpenInventoryAction.INSTANCE) {
            helper.fail("Expected open_inventory scene action to parse as OpenInventoryAction");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void itemSceneActionsGiveAndTakeItems(GameTestHelper helper) {
        UUID owner = new UUID(523L, 23L);
        Moe moe = spawnMoe(helper, owner);
        SceneAction give = parseAction("{\"type\":\"block_party:give_item\",\"action\":{\"item\":\"minecraft:cookie\",\"count\":3,\"target\":\"moe\"}}");
        SceneAction take = parseAction("{\"type\":\"block_party:take_item\",\"action\":{\"item\":\"minecraft:cookie\",\"count\":2,\"source\":\"moe\",\"destination\":\"discard\"}}");

        give.apply(moe);
        if (moe.getInventory().countItem(Items.COOKIE) != 3) {
            helper.fail("Expected give_item to add cookies to the Moe inventory");
            return;
        }

        take.apply(moe);
        if (moe.getInventory().countItem(Items.COOKIE) != 1) {
            helper.fail("Expected take_item to remove two cookies from the Moe inventory");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void giftReceptionUsesItemPreferenceSignal(GameTestHelper helper) {
        UUID owner = new UUID(526L, 26L);
        Moe moe = spawnMoe(helper, owner);
        moe.setBlockState(Blocks.CHEST.defaultBlockState());
        moe.setEmotion("NORMAL");
        SceneAction give = parseAction("{\"type\":\"block_party:give_item\",\"action\":{\"item\":\"minecraft:cod\",\"count\":1,\"target\":\"moe\"}}");

        give.apply(moe);

        if (moe.getInventory().countItem(Items.COD) != 1) {
            helper.fail("Expected gift action to add cod to the Moe inventory");
            return;
        }
        if (moe.latestGiftPreferenceSignal().isEmpty() || !moe.latestGiftPreferenceSignal().get().wantsToBeg()) {
            helper.fail("Expected cat-feature Moe to remember a begged-for fish gift");
            return;
        }
        assertEquals(helper, "HAPPY", moe.getEmotion(), "gift reaction emotion");
        assertEquals(helper, "HAPPY_DANCE", moe.getAnimationKey(), "gift reaction animation");

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:gift_received","filters":[
                  "block_party:if_has_gift_memory",
                  "block_party:if_liked_gift",
                  "block_party:if_begged_for_gift",
                  {"type":"block_party:gift_item","filter":{"item":"minecraft:cod"}},
                  {"type":"block_party:gift_preference","filter":{"operation":"at_least","value":0.5}},
                  {"type":"block_party:gift_begging","filter":{"operation":"at_least","value":0.5}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:gift_received","filters":[
                  "block_party:if_disliked_gift"
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected gift scene filters to accept liked fish gift");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected gift scene filters to reject disliked gift");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void inventorySceneFiltersMatchPlayerAndMoeItems(GameTestHelper helper) {
        UUID owner = new UUID(524L, 24L);
        Moe moe = spawnMoe(helper, owner);
        moe.getInventory().setItem(0, new net.minecraft.world.item.ItemStack(Items.COOKIE, 1));

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:moe_has_item","filter":{"item":"minecraft:cookie","count":1}},
                  {"type":"block_party:has_item","filter":{"item":"minecraft:cookie","count":1}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:moe_has_item","filter":{"item":"minecraft:diamond","count":1}}
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected inventory filters to match player and Moe inventory contents");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected inventory filters to reject missing player item");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void anchorSceneFiltersMatchCurrentRoutineAnchor(GameTestHelper helper) {
        UUID owner = new UUID(518L, 18L);
        Moe moe = spawnMoe(helper, owner);
        ServerLevel level = helper.getLevel();
        try {
            insertSimpleDataBlock(BlockPartyDB.get(level), BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, helper.absolutePos(new BlockPos(5, 1, 1)));
        } catch (SQLException exception) {
            helper.fail("Expected anchor filter setup to succeed: " + exception.getMessage());
            return;
        }

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                    {"type":"block_party:has_anchor","filter":{"type":"garden"}},
                    {"type":"block_party:anchor_type","filter":{"value":"garden"}},
                    {"type":"block_party:anchor_distance","filter":{"operation":"at_most","value":8}},
                    {"type":"block_party:anchor_priority","filter":{"operation":"at_least","value":70}},
                    {"type":"block_party:anchor_player_owned"}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                    {"type":"block_party:anchor_type","filter":{"value":"shrine"}}
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(moe)) {
            helper.fail("Expected anchor scene filters to accept current garden anchor");
            return;
        }
        if (rejects.scene().fulfills(moe)) {
            helper.fail("Expected anchor scene filters to reject mismatched anchor type");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void anchorSceneActionsMoveAndSetHomeToAnchor(GameTestHelper helper) {
        UUID owner = new UUID(519L, 19L);
        Moe moe = spawnMoe(helper, owner);
        ServerLevel level = helper.getLevel();
        BlockPos anchorPos = helper.absolutePos(new BlockPos(6, 1, 1));
        try {
            insertSimpleDataBlock(BlockPartyDB.get(level), BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, anchorPos);
        } catch (SQLException exception) {
            helper.fail("Expected anchor action setup to succeed: " + exception.getMessage());
            return;
        }

        SceneAction goToAnchor = parseAction("{\"type\":\"block_party:go_to_anchor\",\"action\":{\"speed\":1.25}}");
        SceneAction setHomeToAnchor = parseAction("{\"type\":\"block_party:set_home_to_anchor\"}");

        goToAnchor.apply(moe);
        if (!moe.moveTowardCurrentRoutineAnchor(1.0D)) {
            helper.fail("Expected Moe to have a routine anchor movement target");
            return;
        }
        setHomeToAnchor.apply(moe);
        if (!moe.hasHome() || !moe.getHome().getPos().equals(anchorPos)) {
            helper.fail("Expected set_home_to_anchor to promote current anchor into home");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void routineIntentSceneFiltersAndActionsPrepareCommands(GameTestHelper helper) {
        UUID owner = new UUID(520L, 20L);
        Moe moe = spawnMoe(helper, owner);
        moe.setStress(14.0F);
        moe.setRelaxation(0.0F);

        ScenesReloadListener.ParsedScene inferred = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                    {"type":"block_party:routine_intent","filter":{"value":"relax"}},
                    {"type":"block_party:explicit_routine_intent","filter":{"value":"idle"}}
                ],"actions":[]}
                """);
        if (!inferred.scene().fulfills(moe)) {
            helper.fail("Expected routine_intent filter to see inferred relaxation intent");
            return;
        }

        SceneAction setGather = parseAction("{\"type\":\"block_party:set_routine_intent\",\"action\":{\"intent\":\"gather\"}}");
        SceneAction clear = parseAction("{\"type\":\"block_party:clear_routine_intent\"}");
        setGather.apply(moe);
        if (moe.getRoutineIntent() != RoutineIntent.GATHER || moe.getEffectiveRoutineIntent() != RoutineIntent.GATHER) {
            helper.fail("Expected set_routine_intent action to make gather explicit");
            return;
        }
        clear.apply(moe);
        if (moe.getRoutineIntent() != RoutineIntent.IDLE || moe.getEffectiveRoutineIntent() != RoutineIntent.RELAX) {
            helper.fail("Expected clear_routine_intent action to restore inferred routine intent");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void sleepAtHomeSceneActionHidesMoeAtHomePosition(GameTestHelper helper) {
        UUID owner = new UUID(521L, 21L);
        Moe moe = spawnMoe(helper, owner);
        ServerLevel level = helper.getLevel();
        BlockPos home = moe.getHome().getPos();
        moe.moveToBlock(home);
        moe.setRoutineIntent(RoutineIntent.SLEEP);
        moe.setStress(3.0F);
        moe.setRelaxation(0.0F);

        SceneAction sleep = parseAction("{\"type\":\"block_party:sleep_at_home\",\"action\":{\"until\":\"one_second_passes\"}}");
        sleep.apply(moe);

        List<MoeInHiding> hidden = level.getEntitiesOfClass(MoeInHiding.class, new AABB(home).inflate(1.0D));
        if (hidden.size() != 1) {
            helper.fail("Expected sleep_at_home to hide Moe at home position, got " + hidden.size());
            return;
        }
        assertEquals(helper, home, hidden.getFirst().getAttachPos(), "sleep home attach position");
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, hidden.getFirst().getHideUntil(), "sleep hide until");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialSceneFiltersMatchNearbyTarget(GameTestHelper helper) {
        UUID owner = new UUID(507L, 7L);
        Moe observer = spawnMoeAt(helper, owner, new BlockPos(1, 1, 1));
        Moe target = spawnMoeAt(helper, owner, new BlockPos(3, 1, 1));
        target.setGivenName("Hotaru");
        observer.setBloodType("O");
        observer.setDere("NYANDERE");
        target.setBloodType("AB");
        target.setDere("DANDERE");

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:has_social_target","filter":{"radius":8}},
                  {"type":"block_party:social_affinity","filter":{"operation":"at_least","value":0.6}},
                  {"type":"block_party:social_visual","filter":{"value":"fame"}},
                  {"type":"block_party:social_reaction","filter":{"value":"celebrate"}},
                  {"type":"block_party:social_target_name","filter":{"operation":"contains","value":"Hot"}},
                  {"type":"block_party:social_target_blood_type","filter":{"value":"AB"}},
                  {"type":"block_party:social_target_dere","filter":{"value":"DANDERE"}}
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(observer)) {
            helper.fail("Expected social scene filters to match nearby AB DANDERE target");
            return;
        }
        helper.kill(observer);
        helper.kill(target);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void socialPlaceSceneFiltersMatchFriendsRememberedHangout(GameTestHelper helper) {
        UUID owner = new UUID(527L, 27L);
        ServerLevel level = helper.getLevel();
        Moe observer = spawnMoeAt(helper, owner, new BlockPos(1, 1, 1));
        Moe friend = spawnMoeAt(helper, owner, new BlockPos(3, 1, 1));
        friend.setGivenName("Hotaru");
        observer.setBloodType("O");
        observer.setDere("DEREDERE");
        friend.setBloodType("AB");
        friend.setDere("DANDERE");
        BlockPos garden = helper.absolutePos(new BlockPos(8, 1, 1));
        level.setBlock(garden.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        try {
            insertSimpleDataBlock(BlockPartyDB.get(level), BlockPartyDB.TABLE_GARDEN_LANTERNS, owner, level, garden);
        } catch (SQLException exception) {
            helper.fail("Expected social place setup to succeed: " + exception.getMessage());
            return;
        }
        if (friend.observePlaceNow().isEmpty()) {
            helper.fail("Expected friend to remember garden hangout");
            return;
        }

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  "block_party:if_social_place",
                  "block_party:if_social_place_share",
                  {"type":"block_party:social_place_behavior","filter":{"value":"share"}},
                  {"type":"block_party:social_place_type","filter":{"value":"garden"}},
                  {"type":"block_party:social_place_distance","filter":{"operation":"at_most","value":10}},
                  {"type":"block_party:social_place_owner_name","filter":{"operation":"contains","value":"Hot"}}
                ],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene rejects = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  "block_party:if_social_place_avoid"
                ],"actions":[]}
                """);

        if (!accepts.scene().fulfills(observer)) {
            helper.fail("Expected social place filters to accept friendly garden hangout");
            return;
        }
        if (rejects.scene().fulfills(observer)) {
            helper.fail("Expected social place filters to reject avoid behavior");
            return;
        }
        helper.kill(observer);
        helper.kill(friend);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueSubstitutesSocialAndNearbyNames(GameTestHelper helper) {
        UUID owner = new UUID(508L, 8L);
        Moe observer = spawnMoeAt(helper, owner, new BlockPos(1, 1, 1));
        Moe target = spawnMoeAt(helper, owner, new BlockPos(3, 1, 1));
        observer.setGivenName("Akemi");
        target.setGivenName("Hotaru");
        observer.setBloodType("O");
        target.setBloodType("AB");
        SceneAction action = parseAction("""
                {"type":"block_party:send_dialogue","action":{"text":"@name spotted @social.name near @nearby.name: @nearby.names","responses":[]}}
                """);

        action.apply(observer);

        assertEquals(helper, "Akemi spotted Hotaru near Hotaru: Hotaru", observer.getDialogue().text(), "social dialogue substitution");
        helper.kill(observer);
        helper.kill(target);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void malformedSceneParseFailsClosed(GameTestHelper helper) {
        try {
            ScenesReloadListener.parseSceneForTests(BlockParty.source("bad"), JsonParser.parseString("[]"));
            helper.fail("Expected malformed scene root to fail parsing");
            return;
        } catch (RuntimeException expected) {
            helper.succeed();
        }
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void unknownSceneActionIdsFailWithAuthorContext(GameTestHelper helper) {
        assertSceneParseFailsContaining(helper, """
                {"trigger":"block_party:right_click","actions":[{"type":"block_party:not_a_real_action"}]}
                """, "Unknown scene action ID block_party:not_a_real_action at scene block_party:test_scene actions[0]");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void malformedSceneActionPayloadsFailWithAuthorContext(GameTestHelper helper) {
        assertSceneParseFailsContaining(helper, """
                {"trigger":"block_party:right_click","actions":[{"type":"block_party:counter","action":[]}]}
                """, "Scene action scene block_party:test_scene actions[0] field 'action' must be an object payload");
        assertSceneParseFailsContaining(helper, """
                {"trigger":"block_party:right_click","actions":"block_party:end"}
                """, "Scene scene block_party:test_scene field 'actions' must be an array");
        assertSceneParseFailsContaining(helper, """
                {"trigger":"block_party:right_click","actions":["block_party:counter"]}
                """, "only block_party:end supports string form");
        helper.succeed();
    }

    private static Moe spawnMoe(GameTestHelper helper, UUID owner) {
        return spawnMoeAt(helper, owner, new BlockPos(1, 1, 1));
    }

    private static Moe spawnMoeAt(GameTestHelper helper, UUID owner, BlockPos sourceRelativePos) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(sourceRelativePos);
        BlockState sourceState = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, sourceState, 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected test Moe to spawn from sakura_log");
            throw new IllegalStateException("Expected test Moe to spawn");
        }
        if (!level.getBlockState(source).equals(Blocks.AIR.defaultBlockState())) {
            helper.fail("Expected test spawn source to be removed");
        }
        moe.sceneManager().tick();
        return moe;
    }

    private static SceneAction parseAction(String json) {
        return ScenesReloadListener.parseActionForTests(JsonParser.parseString(json).getAsJsonObject());
    }

    private static ScenesReloadListener.ParsedScene parseScene(String json) {
        return ScenesReloadListener.parseSceneForTests(BlockParty.source("test_scene"), JsonParser.parseString(json));
    }

    private static void assertSceneParseFailsContaining(GameTestHelper helper, String json, String expectedMessage) {
        try {
            parseScene(json);
            helper.fail("Expected scene parse to fail with message containing: " + expectedMessage);
        } catch (RuntimeException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains(expectedMessage)) {
                helper.fail("Expected scene parse failure to contain '" + expectedMessage + "', got: " + expected.getMessage());
            }
        }
    }

    private static void tickScene(Moe moe, int count) {
        for (int index = 0; index < count; ++index) {
            moe.sceneManager().tick();
        }
    }

    private static void insertSimpleDataBlock(BlockPartyDB db, String table, UUID owner, ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (PosDim, PosX, PosY, PosZ, PlayerUUID)
                VALUES (?, ?, ?, ?, ?);
                """.formatted(table))) {
            statement.setString(1, level.dimension().location().toString());
            statement.setInt(2, pos.getX());
            statement.setInt(3, pos.getY());
            statement.setInt(4, pos.getZ());
            statement.setString(5, owner.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
