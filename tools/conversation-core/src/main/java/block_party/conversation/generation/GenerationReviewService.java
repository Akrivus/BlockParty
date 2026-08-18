package block_party.conversation.generation;

import block_party.conversation.generation.model.ModelRequest;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/** Re-runs editorial review for an author-edited generated project. */
public final class GenerationReviewService {
    public GenerationReview review(NarrativeModel model, GenerationBrief brief, ScenePackProject project, Path archive)
            throws Exception {
        return review(model, brief, project, null, archive);
    }

    public GenerationReview review(NarrativeModel model, GenerationBrief brief, ScenePackProject project,
            String authorContext, Path archive) throws Exception {
        String system = "Re-review this author-edited Block Party project. Report only current findings; do not repeat "
                + "issues that the supplied project has fixed. Use HIGH only for concrete defects that must block compilation, "
                + "such as broken routes, impossible mechanics, false gameplay claims, or material canon violations. "
                + "Use MEDIUM or LOW for preferences and polish. Treat supplied author review context as project canon and "
                + "environmental facts, while still reporting contradictions with that context or concrete runtime defects. "
                + "Do not demand an in-project mechanic for an inaccessible or externally enforced world rule. "
                + "Return findings as JSON; do not modify the project.";
        JsonObject payload = new JsonObject();
        payload.add("brief", ProjectJson.gson().toJsonTree(brief));
        payload.add("project", ProjectJson.gson().toJsonTree(project));
        payload.addProperty("authorReviewContext", authorContext == null ? "" : authorContext);
        String user = ProjectJson.gson().toJson(payload);
        var response = model.generate(new ModelRequest(
                GenerationStage.REVIEW, system, user, GenerationSchemas.forType(GenerationReview.class), 24_000));
        GenerationReview review = ProjectJson.gson().fromJson(response.structuredOutput(), GenerationReview.class);
        if (archive != null) archive(archive, system, user, response, review);
        return review;
    }

    private static void archive(Path root, String system, String user,
            block_party.conversation.generation.model.ModelResponse response, GenerationReview review) throws Exception {
        Path directory = root.resolve("manual-review-" + System.currentTimeMillis());
        Files.createDirectories(directory);
        JsonObject request = new JsonObject();
        request.addProperty("stage", GenerationStage.REVIEW.name());
        request.addProperty("systemPrompt", system);
        request.addProperty("userPrompt", user);
        JsonObject metadata = new JsonObject();
        metadata.addProperty("stage", "MANUAL_REVIEW");
        metadata.addProperty("provider", response.provider());
        metadata.addProperty("model", response.model());
        metadata.addProperty("requestId", response.requestId());
        metadata.addProperty("completedAt", Instant.now().toString());
        metadata.addProperty("inputTokens", response.usage().inputTokens());
        metadata.addProperty("outputTokens", response.usage().outputTokens());
        Files.writeString(directory.resolve("request.json"), ProjectJson.gson().toJson(request), StandardCharsets.UTF_8);
        Files.writeString(directory.resolve("response.json"), ProjectJson.gson().toJson(review), StandardCharsets.UTF_8);
        Files.writeString(directory.resolve("metadata.json"), ProjectJson.gson().toJson(metadata), StandardCharsets.UTF_8);
    }
}
