package block_party.conversation.compile;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.validation.ProjectValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DialogueCompilationSelfTest {
    private DialogueCompilationSelfTest() {}

    public static void main(String[] args) throws Exception {
        Path fixture = Path.of(args[0]);
        JsonObject source = JsonParser.parseString(Files.readString(fixture)).getAsJsonObject();
        var nodes = source.getAsJsonArray("nodes");
        var introduction = nodes.get(0).getAsJsonObject();
        introduction.getAsJsonArray("responses").get(1).getAsJsonObject().addProperty("target", "decline_reply");
        nodes.add(JsonParser.parseString("""
                {"id":"decline_reply","type":"DIALOGUE","title":"One more reply","text":"Maybe next time.",
                 "tooltip":true,"responses":[{"cue":"close_dialogue","label":"Bye","target":"declined","transition":"IMMEDIATE"}],
                 "conditions":[],"actions":[],"editor":{"x":280,"y":220}}
                """));
        ScenePackProject nested = ProjectJson.gson().fromJson(source, ScenePackProject.class);
        if (!new ProjectValidator().validate(nested).valid()) {
            throw new AssertionError("Valid nested dialogue fixture was rejected.");
        }
        Path output = Files.createTempDirectory("block-party-dialogue-compile-");
        new DatapackCompiler().compile(nested, output);
        JsonObject scene = JsonParser.parseString(Files.readString(output.resolve(
                "data/block_party_generated/scenes/flower_request/introduction.json"))).getAsJsonObject();
        var declineActions = scene.getAsJsonArray("actions").get(0).getAsJsonObject()
                .getAsJsonObject("action").getAsJsonArray("responses").get(1).getAsJsonObject().getAsJsonArray("actions");
        if (declineActions.size() != 1
                || !"block_party:send_dialogue".equals(declineActions.get(0).getAsJsonObject().get("type").getAsString())) {
            throw new AssertionError("Immediate response did not recursively embed its dialogue target: " + declineActions);
        }
        JsonObject invalidTrigger = source.deepCopy();
        find(invalidTrigger, "decline_reply").addProperty("trigger", "right_click");
        assertDiagnostic(invalidTrigger, "IMMEDIATE_TARGET_HAS_TRIGGER");
        JsonObject invalidConditions = source.deepCopy();
        find(invalidConditions, "decline_reply").getAsJsonArray("conditions").add(
                JsonParser.parseString("{\"type\":\"SCENE_FILTER\",\"filter\":{\"type\":\"block_party:always\"}}"));
        assertDiagnostic(invalidConditions, "IMMEDIATE_TARGET_HAS_SCENE_FILTERS");
        JsonObject cycle = source.deepCopy();
        find(cycle, "decline_reply").getAsJsonArray("responses").get(0).getAsJsonObject().addProperty("target", "introduction");
        assertDiagnostic(cycle, "IMMEDIATE_DIALOGUE_CYCLE");
        System.out.println("Dialogue compilation check passed.");
    }

    private static JsonObject find(JsonObject project, String id) {
        for (var element : project.getAsJsonArray("nodes")) {
            JsonObject node = element.getAsJsonObject();
            if (id.equals(node.get("id").getAsString())) return node;
        }
        throw new AssertionError("Missing node " + id);
    }

    private static void assertDiagnostic(JsonObject json, String code) {
        var report = new ProjectValidator().validate(ProjectJson.gson().fromJson(json, ScenePackProject.class));
        if (report.diagnostics().stream().noneMatch(diagnostic -> code.equals(diagnostic.code()))) {
            throw new AssertionError("Expected " + code + ": " + report.diagnostics());
        }
    }
}
