package block_party.conversation.generation;

import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.generation.model.ModelRequest;
import block_party.conversation.generation.model.ModelResponse;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.report.BuildReportWriter;
import block_party.conversation.simulation.ProjectSimulator;
import block_party.conversation.validation.Diagnostic;
import block_party.conversation.validation.ProjectValidator;
import block_party.conversation.validation.ValidationReport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class GenerationPipeline {
    private static final int MAX_GRAPH_REPAIRS = 2;
    private final NarrativeModel model;

    public GenerationPipeline(NarrativeModel model) {
        this.model = model;
    }

    public GenerationResult generate(GenerationBrief brief, Path repositoryRoot, Path output) throws Exception {
        if (brief.generationFormat() != 1) throw new IllegalArgumentException("Unsupported generationFormat " + brief.generationFormat());
        refuseNonEmpty(output);
        Files.createDirectories(output);
        Path generation = output.resolve("generation");
        Files.createDirectories(generation);

        ContentCatalog catalog = new ContentCataloger().catalog(brief, repositoryRoot);
        write(output.resolve("brief.json"), ProjectJson.gson().toJson(brief));
        write(output.resolve("catalog.json"), ProjectJson.gson().toJson(catalog));
        Session session = new Session(brief.budget(), generation);

        ArcPlan plan = session.call(model, GenerationStage.ARC_PLAN,
                "Plan a compact interactive Block Party scene pack. Return JSON only.",
                context(brief, catalog), ArcPlan.class);
        validatePlan(brief, plan);

        ScenePackProject graph = session.call(model, GenerationStage.GRAPH,
                "Create projectFormat 2 mechanics and graph. Use placeholder dialogue. Return JSON only. Never use RAW mechanics.",
                context(brief, catalog, plan), ScenePackProject.class);
        ValidationReport validation = new ProjectValidator().validate(graph);
        int repairs = 0;
        while (!validation.valid() && repairs++ < MAX_GRAPH_REPAIRS) {
            JsonObject repairContext = new JsonObject();
            repairContext.add("brief", ProjectJson.gson().toJsonTree(brief));
            repairContext.add("project", ProjectJson.gson().toJsonTree(graph));
            repairContext.add("diagnostics", ProjectJson.gson().toJsonTree(validation));
            graph = session.call(model, GenerationStage.GRAPH_REPAIR,
                    "Repair only the diagnosed structural/mechanical errors. Return the complete projectFormat 2 project as JSON.",
                    ProjectJson.gson().toJson(repairContext), ScenePackProject.class);
            validation = new ProjectValidator().validate(graph);
        }
        if (!validation.valid()) throw new IllegalStateException("Graph repair exhausted with " + validation.errors() + " error(s).");
        enforceBrief(brief, graph);

        Intentions intentions = session.call(model, GenerationStage.INTENTIONS,
                "Write one temporary scene intention for every DIALOGUE node. Do not redesign mechanics. Return JSON only.",
                context(brief, catalog, plan, graph), Intentions.class);
        write(output.resolve("intentions.json"), ProjectJson.gson().toJson(intentions));

        String mechanics = mechanicsFingerprint(graph);
        ScenePackProject written = session.call(model, GenerationStage.DIALOGUE,
                "Polish only dialogue text, response labels, speaker emotion, and animation. Preserve every mechanic, id, edge, and transition. Return the complete project JSON.",
                context(brief, catalog, plan, graph, intentions), ScenePackProject.class);
        if (!mechanics.equals(mechanicsFingerprint(written))) {
            throw new IllegalStateException("Dialogue stage attempted to mutate locked mechanics.");
        }
        ValidationReport finalValidation = new ProjectValidator().validate(written);
        if (!finalValidation.valid()) throw new IllegalStateException("Dialogue output failed validation with " + finalValidation.errors() + " error(s).");

        GenerationReview review = session.call(model, GenerationStage.REVIEW,
                "Review voice, continuity, repetition, promises, and player-choice meaning. Return findings as JSON; do not modify the project.",
                context(brief, catalog, plan, written, intentions), GenerationReview.class);
        write(output.resolve("review.json"), ProjectJson.gson().toJson(review));
        if (review.findings().stream().anyMatch(finding -> "ERROR".equalsIgnoreCase(finding.severity()))) {
            throw new IllegalStateException("Generation review reported blocking findings.");
        }

        Path projectPath = output.resolve("project.json");
        ProjectJson.write(projectPath, written);
        var simulation = new ProjectSimulator().simulate(written);
        new BuildReportWriter().write(output.resolve("reports"), written, finalValidation, simulation);
        new DatapackCompiler().compile(written, output.resolve("datapack"));
        session.writeManifest(brief, written);
        return new GenerationResult(written, output, session.calls, session.inputTokens, session.outputTokens);
    }

    private static void validatePlan(GenerationBrief brief, ArcPlan plan) {
        if (plan.beats().isEmpty()) throw new IllegalStateException("Arc plan has no beats.");
        if (plan.beats().size() > brief.constraints().maximumCards()) {
            throw new IllegalStateException("Arc plan exceeds the maximum card budget.");
        }
        if (plan.outcomes().size() < 2) throw new IllegalStateException("Arc plan needs at least two outcomes.");
    }

    private static void enforceBrief(GenerationBrief brief, ScenePackProject project) {
        int cards = project.nodes().size();
        if (cards < brief.constraints().minimumCards() || cards > brief.constraints().maximumCards()) {
            throw new IllegalStateException("Generated graph has " + cards + " cards; expected "
                    + brief.constraints().minimumCards() + "–" + brief.constraints().maximumCards() + ".");
        }
        if (project.allowRawMechanics()) throw new IllegalStateException("Generated projects cannot enable raw mechanics.");
    }

    private static String mechanicsFingerprint(ScenePackProject project) {
        JsonObject json = ProjectJson.gson().toJsonTree(project).getAsJsonObject();
        for (JsonElement nodeElement : json.getAsJsonArray("nodes")) {
            JsonObject node = nodeElement.getAsJsonObject();
            node.remove("text");
            node.remove("speaker");
            if (node.has("responses")) {
                for (JsonElement edgeElement : node.getAsJsonArray("responses")) edgeElement.getAsJsonObject().remove("label");
            }
            node.remove("editor");
        }
        return ProjectJson.gson().toJson(json);
    }

    private static String context(Object... values) {
        JsonArray context = new JsonArray();
        for (Object value : values) context.add(ProjectJson.gson().toJsonTree(value));
        return ProjectJson.gson().toJson(context);
    }

    private static void refuseNonEmpty(Path output) throws IOException {
        if (!Files.isDirectory(output)) return;
        try (var children = Files.list(output)) {
            if (children.findAny().isPresent()) throw new IOException("Generation output is not empty: " + output);
        }
    }

    private static void write(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    private static final class Session {
        private final GenerationBudget budget;
        private final Path archive;
        private int calls;
        private int inputCharacters;
        private int outputCharacters;
        private int inputTokens;
        private int outputTokens;

        Session(GenerationBudget budget, Path archive) {
            this.budget = budget;
            this.archive = archive;
        }

        <T> T call(NarrativeModel model, GenerationStage stage, String system, String user, Class<T> type) throws Exception {
            if (calls >= budget.maximumCalls()) throw new IllegalStateException("Generation call budget exhausted.");
            if (inputCharacters + system.length() + user.length() > budget.maximumInputCharacters()) {
                throw new IllegalStateException("Generation input budget exhausted.");
            }
            int number = ++calls;
            inputCharacters += system.length() + user.length();
            ModelRequest request = new ModelRequest(stage, system, user, null, budget.maximumOutputCharacters() - outputCharacters);
            Path directory = archive.resolve(String.format("%02d-%s", number, stage.name().toLowerCase(java.util.Locale.ROOT)));
            Files.createDirectories(directory);
            JsonObject archivedRequest = new JsonObject();
            archivedRequest.addProperty("stage", stage.name());
            archivedRequest.addProperty("systemPrompt", system);
            archivedRequest.addProperty("userPrompt", user);
            write(directory.resolve("request.json"), ProjectJson.gson().toJson(archivedRequest));
            Instant started = Instant.now();
            ModelResponse response = model.generate(request);
            String serialized = ProjectJson.gson().toJson(response.structuredOutput());
            outputCharacters += serialized.length();
            if (outputCharacters > budget.maximumOutputCharacters()) throw new IllegalStateException("Generation output budget exhausted.");
            inputTokens += response.usage().inputTokens();
            outputTokens += response.usage().outputTokens();
            write(directory.resolve("response.json"), serialized);
            JsonObject metadata = new JsonObject();
            metadata.addProperty("stage", stage.name());
            metadata.addProperty("provider", response.provider());
            metadata.addProperty("model", response.model());
            metadata.addProperty("requestId", response.requestId());
            metadata.addProperty("startedAt", started.toString());
            metadata.addProperty("inputTokens", response.usage().inputTokens());
            metadata.addProperty("outputTokens", response.usage().outputTokens());
            write(directory.resolve("metadata.json"), ProjectJson.gson().toJson(metadata));
            return ProjectJson.gson().fromJson(response.structuredOutput(), type);
        }

        void writeManifest(GenerationBrief brief, ScenePackProject project) throws IOException {
            JsonObject manifest = new JsonObject();
            manifest.addProperty("generation_format", brief.generationFormat());
            manifest.addProperty("project_format", project.projectFormat());
            manifest.addProperty("calls", calls);
            manifest.addProperty("input_characters", inputCharacters);
            manifest.addProperty("output_characters", outputCharacters);
            manifest.addProperty("input_tokens", inputTokens);
            manifest.addProperty("output_tokens", outputTokens);
            write(archive.resolve("manifest.json"), ProjectJson.gson().toJson(manifest));
        }
    }
}
