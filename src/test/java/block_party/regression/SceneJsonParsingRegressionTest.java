package block_party.regression;

import block_party.registry.resources.Scenes;
import block_party.scene.ISceneAction;
import block_party.scene.ISceneObservation;
import block_party.scene.Response;
import block_party.scene.Scene;
import block_party.scene.actions.End;
import block_party.scene.actions.Hide;
import block_party.scene.actions.SendDialogue;
import block_party.scene.actions.SendResponse;
import block_party.scene.traits.Emotion;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertNotNull;
import static block_party.regression.TestSupport.assertTrue;
import static block_party.regression.TestSupport.getField;

final class SceneJsonParsingRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testBundledDialogueSceneBuildsRegisteredActions();
        testBundledHideSceneBuildsRegisteredHideAction();
        testSceneActionParserUsesRegisteredActions();
        testSceneObservationParserUsesRegisteredFilters();
        testEveryBundledSceneResourceReferencesBuildableActionAndFilterIds();
    }

    private void testBundledDialogueSceneBuildsRegisteredActions() {
        Scene scene = loadBundledScene("test_dialogue");
        List<ISceneAction> actions = scene.getActions();

        assertEquals(1, actions.size(), "Dialogue scene has one root action");
        assertTrue(actions.get(0) instanceof SendDialogue, "Dialogue scene parses send_dialogue action");

        SendDialogue firstPage = (SendDialogue) actions.get(0);
        SendResponse firstResponse = firstPage.responses.get(Response.NEXT_RESPONSE);
        List<ISceneAction> secondPageActions = (List<ISceneAction>) getField(firstResponse, "actions");
        assertEquals(1, secondPageActions.size(), "Dialogue response parses nested actions");
        assertTrue(secondPageActions.get(0) instanceof SendDialogue, "Dialogue response parses nested send_dialogue action");

        SendDialogue secondPage = (SendDialogue) secondPageActions.get(0);
        SendResponse secondResponse = secondPage.responses.get(Response.NEXT_RESPONSE);
        List<ISceneAction> thirdPageActions = (List<ISceneAction>) getField(secondResponse, "actions");
        SendDialogue thirdPage = (SendDialogue) thirdPageActions.get(0);
        SendResponse finalResponse = thirdPage.responses.get(Response.NEXT_RESPONSE);
        List<ISceneAction> finalActions = (List<ISceneAction>) getField(finalResponse, "actions");

        assertEquals(1, finalActions.size(), "Final dialogue response parses primitive action");
        assertTrue(finalActions.get(0) instanceof End, "Final dialogue response parses end action");
    }

    private void testBundledHideSceneBuildsRegisteredHideAction() {
        Scene scene = loadBundledScene("test_hide");
        List<ISceneAction> actions = scene.getActions();

        assertEquals(1, actions.size(), "Hide scene has one action");
        assertTrue(actions.get(0) instanceof Hide, "Hide scene parses hide action");
    }

    private void testSceneActionParserUsesRegisteredActions() {
        List<ISceneAction> actions = ISceneAction.parseArray(jsonArray("[\"block_party:send_response\",\"block_party:end\"]"));

        assertEquals(2, actions.size(), "Scene action parser builds registered actions");
        assertTrue(actions.get(0) instanceof SendResponse, "Scene action parser builds registered send_response action");
        assertTrue(actions.get(1) instanceof End, "Scene action parser builds registered end action");
    }

    private void testSceneObservationParserUsesRegisteredFilters() {
        List<ISceneObservation> filters = ISceneObservation.parseArray(jsonArray("[\"block_party:never\",\"block_party:if_happy\"]"));

        assertEquals(2, filters.size(), "Scene observation parser builds registered filters");
        assertTrue(filters.get(0) == block_party.scene.SceneObservation.NEVER, "Scene observation parser builds registered never filter");
        assertTrue(filters.get(1) == Emotion.HAPPY, "Scene observation parser builds registered emotion filter");
    }

    private void testEveryBundledSceneResourceReferencesBuildableActionAndFilterIds() {
        Set<ResourceLocation> actionIds = new LinkedHashSet<>();
        Set<ResourceLocation> filterIds = new LinkedHashSet<>();

        for (Path scene : bundledSceneResources()) {
            JsonElement json = parseSceneFile(scene);
            collectSceneIds(json, actionIds, filterIds);
        }

        assertTrue(!actionIds.isEmpty(), "Bundled scene resources reference at least one action");
        assertTrue(!filterIds.isEmpty(), "Bundled scene resources reference at least one filter");
        for (ResourceLocation actionId : actionIds) {
            assertNotNull(ISceneAction.buildKnownAction(ISceneAction.own(actionId)), "Bundled scene action ID is buildable: " + actionId);
        }
        for (ResourceLocation filterId : filterIds) {
            assertNotNull(ISceneObservation.buildKnownFilter(ISceneObservation.own(filterId)), "Bundled scene filter ID is buildable: " + filterId);
        }
    }

    private Scene loadBundledScene(String name) {
        ExposedScenes scenes = new ExposedScenes();
        ResourceLocation location = new ResourceLocation("block_party", name);
        scenes.load(Map.of(location, readSceneJson(name)));
        Map<ResourceLocation, Scene> byName = (Map<ResourceLocation, Scene>) getField(scenes, "byName");
        return byName.get(location);
    }

    private JsonElement readSceneJson(String name) {
        String path = "data/block_party/scenes/" + name + ".json";
        InputStream stream = SceneJsonParsingRegressionTest.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new AssertionError("Missing bundled scene JSON: " + path);
        }
        return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private JsonArray jsonArray(String json) {
        return JsonParser.parseString(json).getAsJsonArray();
    }

    private List<Path> bundledSceneResources() {
        Path root = Path.of("src", "main", "resources", "data", "block_party", "scenes");
        try (Stream<Path> files = Files.walk(root)) {
            return files
                    .filter((path) -> path.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new AssertionError("Could not scan bundled scene resources under " + root, e);
        }
    }

    private JsonElement parseSceneFile(Path path) {
        try {
            return JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AssertionError("Could not parse bundled scene JSON: " + path, e);
        }
    }

    private void collectSceneIds(JsonElement element, Set<ResourceLocation> actionIds, Set<ResourceLocation> filterIds) {
        if (element == null || element.isJsonNull()) { return; }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collectSceneIds(child, actionIds, filterIds);
            }
            return;
        }
        if (!element.isJsonObject()) { return; }

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if ("actions".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                collectTypedIds(entry.getValue().getAsJsonArray(), actionIds, "action");
            } else if ("filters".equals(entry.getKey()) && entry.getValue().isJsonArray()) {
                collectTypedIds(entry.getValue().getAsJsonArray(), filterIds, "filter");
            }
            collectSceneIds(entry.getValue(), actionIds, filterIds);
        }
    }

    private void collectTypedIds(JsonArray array, Set<ResourceLocation> ids, String kind) {
        for (int i = 0; i < array.size(); ++i) {
            JsonElement member = array.get(i);
            ResourceLocation location = resourceLocation(member);
            if (location == null) {
                throw new AssertionError("Bundled scene " + kind + " entry does not contain a valid ID: " + member);
            }
            ids.add(location);
        }
    }

    private ResourceLocation resourceLocation(JsonElement member) {
        if (member.isJsonPrimitive()) {
            return ResourceLocation.tryParse(member.getAsString());
        }
        if (member.isJsonObject() && member.getAsJsonObject().has("type")) {
            return ResourceLocation.tryParse(member.getAsJsonObject().get("type").getAsString());
        }
        return null;
    }

    private static final class ExposedScenes extends Scenes {
        void load(Map<ResourceLocation, JsonElement> folder) {
            this.apply(folder, (ResourceManager) null, (ProfilerFiller) null);
        }
    }
}
