package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.goals.HideUntil;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import block_party.registry.resources.ScenesReloadListener;
import block_party.scene.Response;
import block_party.scene.SceneAction;
import block_party.scene.SceneTrigger;
import block_party.scene.SceneVariables;
import block_party.db.voicemail.Voicemails;
import com.google.gson.JsonParser;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public static void structuredSceneObservationFactoriesMatchActiveMoeState(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(512L, 12L));
        moe.setHealth(12.0F);
        moe.setFoodLevel(9.0F);
        moe.setGivenName("Akemi");
        SceneVariables variables = SceneVariables.get(moe.level());
        variables.cookies(moe.getDatabaseID()).set("mood", "sunny");
        variables.counters(moe.getDatabaseID()).set("score", 3);

        ScenesReloadListener.ParsedScene accepts = parseScene("""
                {"trigger":"block_party:right_click","filters":[
                  {"type":"block_party:health","filter":{"operation":"at_least","value":10}},
                  {"type":"block_party:food_level","filter":{"operation":"less_than","value":10}},
                  {"type":"block_party:counter","filter":{"name":"score","operation":"equals","value":3}},
                  {"type":"block_party:has_cookie","filter":{"name":"mood","operation":"equals","value":"sunny"}},
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
    public static void unimplementedSceneObservationIdsFailClosed(GameTestHelper helper) {
        Moe moe = spawnMoe(helper, new UUID(513L, 13L));
        ScenesReloadListener.ParsedScene unknown = parseScene("""
                {"trigger":"block_party:right_click","filters":["block_party:not_a_real_filter"],"actions":[]}
                """);
        ScenesReloadListener.ParsedScene deferred = parseScene("""
                {"trigger":"block_party:right_click","filters":[{"type":"block_party:family_name","filter":{"value":"Minashigo"}}],"actions":[]}
                """);

        if (unknown.scene().fulfills(moe)) {
            helper.fail("Expected unknown scene filters to fail closed");
            return;
        }
        if (deferred.scene().fulfills(moe)) {
            helper.fail("Expected deferred scene filters to fail closed");
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

        cookie.apply(moe);
        counter.apply(moe);

        SceneVariables variables = SceneVariables.get(moe.level());
        assertEquals(helper, "yes", variables.cookies(moe.getDatabaseID()).get("phase5_cookie"), "scene cookie value");
        assertEquals(helper, 3, variables.counters(moe.getDatabaseID()).get("phase5_counter"), "scene counter value");
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
    public static void malformedSceneParseFailsClosed(GameTestHelper helper) {
        try {
            ScenesReloadListener.parseSceneForTests(BlockParty.source("bad"), JsonParser.parseString("[]"));
            helper.fail("Expected malformed scene root to fail parsing");
            return;
        } catch (RuntimeException expected) {
            helper.succeed();
        }
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

    private static void tickScene(Moe moe, int count) {
        for (int index = 0; index < count; ++index) {
            moe.sceneManager().tick();
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
