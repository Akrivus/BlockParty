package block_party.gametest;

import block_party.BlockParty;
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
import com.google.gson.JsonParser;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
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

    private static void tickScene(Moe moe, int count) {
        for (int index = 0; index < count; ++index) {
            moe.tick();
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
