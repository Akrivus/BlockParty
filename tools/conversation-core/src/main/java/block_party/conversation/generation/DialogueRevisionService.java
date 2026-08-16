package block_party.conversation.generation;

import block_party.conversation.generation.model.ModelRequest;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.validation.ProjectValidator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public final class DialogueRevisionService {
    private static final String REVISION_PROMPT = """
            Return JSON with 2 or 3 prose-only alternatives. Each alternative has text,
            responseLabels, and rationale. Preserve response count and meaning. Never emit
            mechanics, ids, targets, actions, conditions, or transitions.
            """;

    public DialogueRevision request(NarrativeModel model, ScenePackProject project, String nodeId,
            String instruction, String context, Path archive) throws Exception {
        var node = project.nodes().stream().filter(value -> value.id().equals(nodeId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown card " + nodeId));
        JsonObject payload = new JsonObject();
        payload.addProperty("instruction", instruction);
        payload.addProperty("cardId", nodeId);
        payload.addProperty("currentText", node.text());
        payload.add("currentResponseLabels", ProjectJson.gson().toJsonTree(
                node.responses().stream().map(value -> value.label()).toList()));
        payload.addProperty("neighborContext", context == null ? "" : context);
        payload.addProperty("mechanicsFingerprint", MechanicsFingerprint.of(project));
        var response = model.generate(new ModelRequest(
                GenerationStage.DIALOGUE_REVISION,
                REVISION_PROMPT,
                ProjectJson.gson().toJson(payload),
                GenerationSchemas.forType(DialogueRevision.class),
                20_000));
        DialogueRevision revision = ProjectJson.gson().fromJson(response.structuredOutput(), DialogueRevision.class);
        if (revision.alternatives().isEmpty()) {
            throw new IllegalStateException("Revision returned no alternatives.");
        }
        validateAlternatives(revision, node.responses().size());
        if (archive != null) {
            archiveRevision(archive, nodeId, payload, response.structuredOutput());
        }
        return revision;
    }

    public ScenePackProject apply(ScenePackProject project, String nodeId, DialogueAlternative alternative) {
        String before = MechanicsFingerprint.of(project);
        JsonObject json = ProjectJson.gson().toJsonTree(project).getAsJsonObject();
        boolean found = false;
        for (JsonElement element : json.getAsJsonArray("nodes")) {
            JsonObject node = element.getAsJsonObject();
            if (!nodeId.equals(node.get("id").getAsString())) {
                continue;
            }
            found = true;
            node.addProperty("text", alternative.text());
            JsonArray responses = node.getAsJsonArray("responses");
            if (!alternative.responseLabels().isEmpty()) {
                if (responses.size() != alternative.responseLabels().size()) {
                    throw new IllegalArgumentException("Response count changed.");
                }
                for (int i = 0; i < responses.size(); i++) {
                    responses.get(i).getAsJsonObject().addProperty(
                            "label", alternative.responseLabels().get(i));
                }
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Unknown card " + nodeId);
        }
        ScenePackProject revised = ProjectJson.gson().fromJson(json, ScenePackProject.class);
        if (!before.equals(MechanicsFingerprint.of(revised))) {
            throw new IllegalStateException("Revision changed locked mechanics.");
        }
        if (!new ProjectValidator().validate(revised).valid()) {
            throw new IllegalStateException("Revision produced an invalid project.");
        }
        return revised;
    }

    private static void validateAlternatives(DialogueRevision revision, int responseCount) {
        for (DialogueAlternative alternative : revision.alternatives()) {
            int proposedCount = alternative.responseLabels().size();
            if (proposedCount != 0 && proposedCount != responseCount) {
                throw new IllegalStateException("Revision changed the response count.");
            }
        }
    }

    private static void archiveRevision(
            Path archive, String nodeId, JsonObject request, JsonElement response) throws Exception {
        Path directory = archive.resolve("revisions")
                .resolve(Instant.now().toEpochMilli() + "-" + nodeId);
        Files.createDirectories(directory);
        writeJson(directory.resolve("request.json"), request);
        writeJson(directory.resolve("response.json"), response);
    }

    private static void writeJson(Path path, JsonElement value) throws Exception {
        Files.writeString(
                path,
                ProjectJson.gson().toJson(value) + System.lineSeparator(),
                StandardCharsets.UTF_8);
    }
}
