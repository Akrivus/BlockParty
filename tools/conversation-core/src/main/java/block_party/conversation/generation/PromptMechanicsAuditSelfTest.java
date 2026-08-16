package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.model.ScenePackProject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PromptMechanicsAuditSelfTest {
    private PromptMechanicsAuditSelfTest() {}

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]);
        ScenePackProject unrelated = ProjectJson.gson().fromJson(Files.readString(
                root.resolve("tools/conversation-core/examples/flower-request.project.json")), ScenePackProject.class);
        GenerationBrief brief = new GenerationBrief(
                1, "sunset_lantern", "block_party_generated", "Sunset Lantern",
                "At sunset, if it is clear and this Moe is idle, walk to the garden, look at the lantern, "
                        + "play the awe animation, wait briefly, then return home",
                List.of(), true, List.of(), List.of(), List.of(), List.of(), null, List.of(), null, null,
                "recorded", "fixture", null);
        var findings = PromptMechanicsAudit.audit(brief, unrelated);
        assertMessage(findings, "time_period");
        assertMessage(findings, "weather");
        assertMessage(findings, "routine_intent");
        assertMessage(findings, "routine_tick");
        assertMessage(findings, "ASSIGN_NEAR_BLOCK");
        assertMessage(findings, "PLAY_ANIMATION");
        assertMessage(findings, "assignment_arrived");
        if (GenerationMechanicsGuide.describe(brief).getAsJsonArray("translations").size() < 7) {
            throw new AssertionError("Generation mechanics guide is incomplete.");
        }
        var schema = GenerationSchemas.forType(ScenePackProject.class);
        var filterProperties = schema.getAsJsonObject("properties").getAsJsonObject("nodes")
                .getAsJsonObject("items").getAsJsonObject("properties").getAsJsonObject("conditions")
                .getAsJsonObject("items").getAsJsonObject("properties").getAsJsonObject("filter")
                .getAsJsonArray("anyOf").get(0).getAsJsonObject().getAsJsonObject("properties");
        if (!filterProperties.has("type") || !filterProperties.has("value") || filterProperties.has("emotion")) {
            throw new AssertionError("PackCondition.filter received the wrong structured-output schema: " + filterProperties);
        }
        var projectJson = ProjectJson.gson().toJsonTree(unrelated).getAsJsonObject();
        projectJson.getAsJsonArray("nodes").get(0).getAsJsonObject().getAsJsonArray("conditions").add(
                JsonParser.parseString("{\"type\":\"SCENE_FILTER\",\"filter\":{\"type\":\"block_party:weather\",\"value\":\"clear\"}}"));
        ScenePackProject inferred = ProjectJson.gson().fromJson(projectJson, ScenePackProject.class);
        var locked = JsonParser.parseString(
                "{\"type\":\"block_party:time_period\",\"value\":\"evening\"}").getAsJsonObject();
        GenerationBrief lockedBrief = new GenerationBrief(
                1, "locked", "block_party_generated", "Locked", "evening and clear", List.of(), true,
                List.of(), List.of(), List.of(), List.of(), "routine_tick", List.of(locked), null, null,
                "recorded", "fixture", null);
        ScenePackProject merged = GenerationPipeline.applyLockedSelectors(lockedBrief, inferred);
        var rootFilters = merged.nodes().get(0).conditions().stream()
                .filter(condition -> condition.type() == block_party.conversation.model.ConditionType.SCENE_FILTER).toList();
        if (rootFilters.size() != 2) {
            throw new AssertionError("Locked selectors replaced inferred selectors: " + rootFilters);
        }
        var nullableProject = ProjectJson.gson().toJsonTree(unrelated).getAsJsonObject();
        var nullableConditions = new com.google.gson.JsonArray();
        nullableConditions.add(JsonParser.parseString(
                "{\"type\":\"HAS_COOKIE\",\"scope\":\"NPC\",\"state\":\"started\",\"filter\":null}"));
        nullableConditions.add(JsonParser.parseString(
                "{\"type\":\"SCENE_FILTER\",\"state\":\"weather\",\"filter\":null}"));
        var firstNullableNode = nullableProject.getAsJsonArray("nodes").get(0).getAsJsonObject();
        firstNullableNode.add("conditions", nullableConditions);
        firstNullableNode.getAsJsonObject("editor").addProperty("x", -220);
        var normalizedProject = GenerationPipeline.normalizeProjectResponseForTests(nullableProject);
        ScenePackProject nullableParsed = ProjectJson.gson().fromJson(normalizedProject, ScenePackProject.class);
        if (nullableParsed.nodes().get(0).conditions().get(0).filter() != null
                || !"block_party:weather".equals(nullableParsed.nodes().get(0).conditions().get(1).filter()
                        .get("type").getAsString())
                || nullableParsed.nodes().stream().anyMatch(node -> node.editor().x() < 80 || node.editor().y() < 80)) {
            throw new AssertionError("Generated filters or editor positions were not normalized before Gson deserialization.");
        }
        var eventProjectJson = ProjectJson.gson().toJsonTree(unrelated).getAsJsonObject();
        for (var element : eventProjectJson.getAsJsonArray("nodes")) {
            var node = element.getAsJsonObject();
            if ("reward".equals(node.get("id").getAsString())) node.addProperty("trigger", "assignment_arrived");
        }
        Path eventOutput = Files.createTempDirectory("block-party-event-root-");
        new DatapackCompiler().compile(ProjectJson.gson().fromJson(eventProjectJson, ScenePackProject.class), eventOutput);
        if (!Files.isRegularFile(eventOutput.resolve("data/block_party_generated/scenes/flower_request/reward.json"))) {
            throw new AssertionError("Compiler omitted an explicitly triggered dialogue scene.");
        }
        System.out.println("Prompt mechanics audit check passed.");
    }

    private static void assertMessage(List<block_party.conversation.validation.Diagnostic> findings, String text) {
        if (findings.stream().noneMatch(finding -> finding.message().contains(text))) {
            throw new AssertionError("Prompt mechanics audit did not report " + text + ": " + findings);
        }
    }
}
