package block_party.conversation.generation;

import block_party.conversation.compile.DatapackCompiler;
import block_party.conversation.generation.model.ModelRequest;
import block_party.conversation.generation.model.ModelResponse;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.PackCondition;
import block_party.conversation.model.ResponseCues;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.TriggerTypes;
import block_party.conversation.report.BuildReportWriter;
import block_party.conversation.simulation.ProjectSimulator;
import block_party.conversation.validation.Diagnostic;
import block_party.conversation.validation.ProjectValidator;
import block_party.conversation.validation.Severity;
import block_party.conversation.validation.ValidationReport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GenerationPipeline {
    private static final int MAX_GRAPH_REPAIRS = 2;
    private static final int MAX_DIALOGUE_REPAIRS = 1;
    private static final int MAX_RESPONSE_LABEL_CHARACTERS = 64;
    private final NarrativeModel model;
    private final GenerationProgressListener progress;

    public GenerationPipeline(NarrativeModel model) {
        this(model, GenerationProgressListener.NONE);
    }

    public GenerationPipeline(NarrativeModel model, GenerationProgressListener progress) {
        this.model = model;
        this.progress = progress == null ? GenerationProgressListener.NONE : progress;
    }

    public GenerationResult generate(GenerationBrief brief, Path repositoryRoot, Path output) throws Exception {
        return generate(brief, repositoryRoot, output, null);
    }

    public GenerationResult generate(
            GenerationBrief brief,
            Path repositoryRoot,
            Path output,
            ContentCatalog archivedCatalog) throws Exception {
        if (brief.generationFormat() != 1) throw new IllegalArgumentException("Unsupported generationFormat " + brief.generationFormat());
        refuseNonEmpty(output);
        Files.createDirectories(output);
        Path generation = output.resolve("generation");
        Files.createDirectories(generation);

        ContentCatalog catalog = archivedCatalog == null
                ? new ContentCataloger().catalog(brief, repositoryRoot)
                : archivedCatalog;
        write(output.resolve("brief.json"), ProjectJson.gson().toJson(brief));
        write(output.resolve("catalog.json"), ProjectJson.gson().toJson(catalog));
        write(output.resolve("context.json"), ProjectJson.gson().toJson(catalog.context()));
        Session session = new Session(brief.budget(), generation, progress);

        ArcPlan plan = session.call(model, GenerationStage.ARC_PLAN,
                "Plan a compact interactive Block Party scene pack. Return only an ArcPlan with "
                        + "premise, characterArc, beats, and outcomes; do not write cards or dialogue.",
                context(brief, catalog), ArcPlan.class);
        validatePlan(plan);

        ScenePackProject graph = session.call(model, GenerationStage.GRAPH,
                "Return one complete ScenePackProject in projectFormat 2. Use uppercase enum values, "
                        + "placeholder dialogue, and speaker objects containing emotion and animation—not speaker names. "
                        + "Copy the brief's pack id, namespace, and title exactly. Never use RAW mechanics and do not wrap "
                        + "the project in a graph or mechanics object. Use right_click for ordinary Moe interaction. Every "
                        + "later GAMEPLAY_GATE must include a HAS_COOKIE or COUNTER condition written by an earlier choice. "
                        + "A DIALOGUE node may have at most " + SceneNode.MAX_RESPONSES + " player responses. "
                        + "Response cue is an icon enum from the schema; put words shown to the player in label. "
                        + "Never emit SCENE_FILTER conditions; locked batch selectors are applied by the tool.",
                context(brief, catalog, plan), ScenePackProject.class);
        graph = alignPackIdentity(brief, graph);
        graph = applyLockedSelectors(brief, graph);
        graph = truncateResponses(graph);
        graph = normalizeResponseCues(graph);
        ValidationReport validation = new ProjectValidator().validate(graph);
        int repairs = 0;
        while (!validation.valid() && repairs++ < MAX_GRAPH_REPAIRS) {
            JsonObject repairContext = new JsonObject();
            repairContext.add("brief", ProjectJson.gson().toJsonTree(brief));
            repairContext.add("project", ProjectJson.gson().toJsonTree(graph));
            repairContext.add("diagnostics", ProjectJson.gson().toJsonTree(validation));
            graph = session.call(model, GenerationStage.GRAPH_REPAIR,
                    "Repair only the diagnosed structural or mechanical errors. Return one complete ScenePackProject "
                            + "in projectFormat 2; speaker must be an object, never null or a name string. "
                            + "Use null emotion and animation fields for cards without a speaker. For REPEATABLE_REWARD, "
                            + "declare a COOKIE state, add a not=true HAS_COOKIE condition for it to the GAMEPLAY_GATE, "
                            + "and add SET_COOKIE for that same state directly to the gate actions; a counter guard is invalid.",
                    ProjectJson.gson().toJson(repairContext), ScenePackProject.class);
            graph = alignPackIdentity(brief, graph);
            graph = applyLockedSelectors(brief, graph);
            graph = truncateResponses(graph);
            graph = normalizeResponseCues(graph);
            validation = new ProjectValidator().validate(graph);
        }
        write(output.resolve("graph-validation.json"), ProjectJson.gson().toJson(validation));
        if (!validation.valid()) {
            throw new IllegalStateException("Graph repair exhausted with "
                    + validation.errors() + " error(s): " + errorSummary(validation));
        }
        enforceBrief(brief, graph);

        Intentions intentions = session.call(
                model,
                GenerationStage.INTENTIONS,
                "Return an object with a scenes array containing one temporary intention for every DIALOGUE node. "
                        + "Each scene requires node, speakerObjective, emotionalState, mayReveal, mustNotReveal, "
                        + "playerChoicePurpose, and continuity. Do not redesign mechanics. Return JSON only.",
                context(brief, catalog, plan, graph),
                Intentions.class);
        validateIntentions(graph, intentions);
        write(output.resolve("intentions.json"), ProjectJson.gson().toJson(intentions));

        String mechanics = MechanicsFingerprint.of(graph);
        int dialogueLimit = brief.constraints().maximumDialogueCharacters();
        String dialoguePrompt = "Return one dialogue patch for every project node. Each patch contains only the node ID, "
                + "polished text, a speaker object, and responseLabels in their existing order. The text field is rendered "
                + "verbatim in a speech bubble: write only words spoken aloud by the Moe, in first person where natural. "
                + "Speaker emotion and animation must use only the enum values allowed by the response schema. "
                + "Never use brackets, narration, speaker labels, screenplay directions, or descriptions of gestures, looks, "
                + "feelings, or actions. Put performance only in speaker emotion and animation. Example: write ‘Sometimes I "
                + "wish I had green hair like Grass.’, not ‘[Dirt looks down and confides their wish.]’. "
                + "Use only dialogue formatting and runtime substitutions documented in the supplied context; substitutions "
                + "belong in dialogue text, never response labels. Preserve response "
                + "count and meaning. Every text must be at most " + dialogueLimit + " characters, and every response label "
                + "at most " + MAX_RESPONSE_LABEL_CHARACTERS + " characters. Voice direction: "
                + brief.constraints().dialogueStyle() + " For cards without a speaker, use null emotion and animation fields. "
                + "Do not return mechanics.";
        DialoguePass dialogue = session.call(
                model,
                GenerationStage.DIALOGUE,
                dialoguePrompt,
                context(brief, catalog, plan, graph, intentions),
                DialoguePass.class);
        List<String> dialogueErrors = dialogueFormatErrors(graph, dialogue, dialogueLimit);
        int dialogueRepairs = 0;
        while (!dialogueErrors.isEmpty() && dialogueRepairs++ < MAX_DIALOGUE_REPAIRS) {
            dialogue = session.call(
                    model,
                    GenerationStage.DIALOGUE,
                    dialoguePrompt + " Rewrite every rejected patch as actual spoken dialogue.",
                    context(brief, catalog, plan, graph, intentions, dialogue, dialogueErrors),
                    DialoguePass.class);
            dialogueErrors = dialogueFormatErrors(graph, dialogue, dialogueLimit);
        }
        if (!dialogueErrors.isEmpty()) {
            throw new IllegalStateException("Dialogue formatting failed: " + String.join("; ", dialogueErrors));
        }
        ScenePackProject written = applyDialogue(graph, dialogue);
        if (!mechanics.equals(MechanicsFingerprint.of(written))) {
            throw new IllegalStateException("Dialogue stage attempted to mutate locked mechanics.");
        }
        ValidationReport finalValidation = new ProjectValidator().validate(written);
        if (!finalValidation.valid()) {
            throw new IllegalStateException(
                    "Dialogue output failed validation with "
                            + finalValidation.errors()
                            + " error(s).");
        }

        GenerationReview review = session.call(
                model,
                GenerationStage.REVIEW,
                "Review voice, continuity, repetition, promises, player-choice meaning, and canon. "
                        + "Check cardinal/corporeal identity, trait support, individual memories, and invented mechanics. "
                        + "Return findings as JSON; do not modify the project.",
                context(brief, catalog, plan, written, intentions),
                GenerationReview.class);
        write(output.resolve("review.json"), ProjectJson.gson().toJson(review));

        Path projectPath = output.resolve("project.json");
        ProjectJson.write(projectPath, written);
        var simulation = new ProjectSimulator().simulate(written);
        new BuildReportWriter().write(output.resolve("reports"), written, finalValidation, simulation);
        new DatapackCompiler().compile(written, output.resolve("datapack"));
        session.writeManifest(brief, written);
        return new GenerationResult(written, output, session.calls, session.inputTokens, session.outputTokens);
    }

    private static ScenePackProject truncateResponses(ScenePackProject graph) {
        JsonObject project = ProjectJson.gson().toJsonTree(graph).getAsJsonObject();
        for (JsonElement element : project.getAsJsonArray("nodes")) {
            JsonObject node = element.getAsJsonObject();
            if (!node.has("responses") || !node.get("responses").isJsonArray()) continue;
            JsonArray responses = node.getAsJsonArray("responses");
            while (responses.size() > SceneNode.MAX_RESPONSES) {
                responses.remove(responses.size() - 1);
            }
        }
        return ProjectJson.gson().fromJson(project, ScenePackProject.class);
    }

    private static ScenePackProject normalizeResponseCues(ScenePackProject graph) {
        JsonObject project = ProjectJson.gson().toJsonTree(graph).getAsJsonObject();
        for (JsonElement element : project.getAsJsonArray("nodes")) {
            JsonObject node = element.getAsJsonObject();
            if (!node.has("responses") || !node.get("responses").isJsonArray()) continue;
            Set<String> used = new HashSet<>();
            for (JsonElement responseElement : node.getAsJsonArray("responses")) {
                JsonObject response = responseElement.getAsJsonObject();
                String cue = response.has("cue") && !response.get("cue").isJsonNull()
                        ? response.get("cue").getAsString() : null;
                if (ResponseCues.valid(cue) && used.add(unqualifyCue(cue))) continue;
                String replacement = ResponseCues.NEUTRAL_DEFAULTS.stream()
                        .filter(candidate -> !used.contains(candidate)).findFirst().orElse("chat_bubble");
                response.addProperty("cue", replacement);
                used.add(replacement);
            }
        }
        return ProjectJson.gson().fromJson(project, ScenePackProject.class);
    }

    private static String unqualifyCue(String cue) {
        int separator = cue.indexOf(':');
        return separator >= 0 ? cue.substring(separator + 1) : cue;
    }

    private static ScenePackProject alignPackIdentity(GenerationBrief brief, ScenePackProject project) {
        JsonObject json = ProjectJson.gson().toJsonTree(project).getAsJsonObject();
        JsonObject pack = json.getAsJsonObject("pack");
        pack.addProperty("id", brief.id());
        pack.addProperty("namespace", brief.namespace());
        pack.addProperty("title", brief.title());
        return ProjectJson.gson().fromJson(json, ScenePackProject.class);
    }

    private static ScenePackProject applyLockedSelectors(GenerationBrief brief, ScenePackProject project) {
        if (brief.lockedFilters().isEmpty() && (brief.lockedTrigger() == null || brief.lockedTrigger().isBlank())) {
            return project;
        }
        List<SceneNode> nodes = new ArrayList<>();
        for (SceneNode node : project.nodes()) {
            boolean root = node.id().equals(project.entry()) || node.trigger() != null && !node.trigger().isBlank();
            if (!root) {
                nodes.add(node);
                continue;
            }
            List<PackCondition> conditions = new ArrayList<>(node.conditions().stream()
                    .filter(condition -> condition.type() != ConditionType.SCENE_FILTER).toList());
            for (JsonObject filter : brief.lockedFilters()) {
                conditions.add(new PackCondition(
                        ConditionType.SCENE_FILTER, null, null, null, 0, null, 0, false, null, 0, filter, null));
            }
            String trigger = brief.lockedTrigger() == null || brief.lockedTrigger().isBlank()
                    ? node.trigger() : brief.lockedTrigger();
            nodes.add(new SceneNode(
                    node.id(), node.type(), node.title(), trigger, conditions, node.text(), node.tooltip(), node.speaker(),
                    node.responses(), node.actions(), node.next(), node.ending(), node.editor()));
        }
        return new ScenePackProject(
                project.projectFormat(), project.target(), project.pack(), project.contract(), project.state(),
                project.allowRawMechanics(), project.entry(), nodes);
    }

    private static ScenePackProject applyDialogue(ScenePackProject graph, DialoguePass dialogue) {
        Map<String, DialoguePatch> patches = new HashMap<>();
        for (DialoguePatch patch : dialogue.nodes()) {
            if (patch.node() == null || patch.node().isBlank()) {
                throw new IllegalStateException("Dialogue stage returned a patch without a node ID.");
            }
            if (patches.put(patch.node(), patch) != null) {
                throw new IllegalStateException("Dialogue stage returned duplicate patches for " + patch.node() + ".");
            }
        }
        Set<String> expected = new HashSet<>();
        for (var node : graph.nodes()) expected.add(node.id());
        Set<String> returned = new HashSet<>(patches.keySet());
        if (!expected.equals(returned)) {
            Set<String> missing = new HashSet<>(expected);
            missing.removeAll(returned);
            Set<String> unknown = new HashSet<>(returned);
            unknown.removeAll(expected);
            throw new IllegalStateException(
                    "Dialogue patches did not match graph nodes. Missing: " + missing + "; unknown: " + unknown + ".");
        }

        JsonObject project = ProjectJson.gson().toJsonTree(graph).getAsJsonObject();
        for (JsonElement element : project.getAsJsonArray("nodes")) {
            JsonObject node = element.getAsJsonObject();
            DialoguePatch patch = patches.get(node.get("id").getAsString());
            node.addProperty("text", patch.text());
            node.add("speaker", patch.speaker() == null ? JsonNull.INSTANCE : patch.speaker().deepCopy());
            JsonArray responses = node.getAsJsonArray("responses");
            if (responses.size() != patch.responseLabels().size()) {
                throw new IllegalStateException("Dialogue patch changed response count for "
                        + node.get("id").getAsString() + ".");
            }
            for (int index = 0; index < responses.size(); index++) {
                responses.get(index).getAsJsonObject().addProperty("label", patch.responseLabels().get(index));
            }
        }
        return ProjectJson.gson().fromJson(project, ScenePackProject.class);
    }

    private static List<String> dialogueFormatErrors(
            ScenePackProject graph, DialoguePass dialogue, int maximumDialogueCharacters) {
        List<String> errors = new ArrayList<>();
        Set<String> dialogueNodes = new HashSet<>();
        for (var node : graph.nodes()) {
            if (node.type() == NodeType.DIALOGUE) dialogueNodes.add(node.id());
        }
        for (DialoguePatch patch : dialogue.nodes()) {
            String text = patch.text() == null ? "" : patch.text().strip();
            if (text.isEmpty() && dialogueNodes.contains(patch.node())) {
                errors.add(patch.node() + " has no spoken text");
            } else if (!text.isEmpty() && (text.startsWith("[") || text.endsWith("]"))) {
                errors.add(patch.node() + " uses bracketed stage direction instead of spoken dialogue");
            } else if (text.length() > maximumDialogueCharacters) {
                errors.add(patch.node() + " dialogue is " + text.length() + " characters; maximum is "
                        + maximumDialogueCharacters);
            }
            for (int index = 0; index < patch.responseLabels().size(); index++) {
                String label = patch.responseLabels().get(index);
                if (label != null && label.length() > MAX_RESPONSE_LABEL_CHARACTERS) {
                    errors.add(patch.node() + " response " + (index + 1) + " is " + label.length()
                            + " characters; maximum is " + MAX_RESPONSE_LABEL_CHARACTERS);
                }
            }
        }
        return errors;
    }

    private static void validateIntentions(ScenePackProject graph, Intentions intentions) {
        Set<String> expected = new HashSet<>();
        for (var node : graph.nodes()) {
            if (node.type() == NodeType.DIALOGUE) expected.add(node.id());
        }
        Set<String> returned = new HashSet<>();
        for (SceneIntention intention : intentions.scenes()) {
            if (intention.node() != null) returned.add(intention.node());
        }
        if (expected.equals(returned)) return;
        Set<String> missing = new HashSet<>(expected);
        missing.removeAll(returned);
        Set<String> unknown = new HashSet<>(returned);
        unknown.removeAll(expected);
        throw new IllegalStateException(
                "Intentions did not match dialogue nodes. Missing: " + missing + "; unknown: " + unknown + ".");
    }

    private static void validatePlan(ArcPlan plan) {
        if (plan.beats().isEmpty()) throw new IllegalStateException("Arc plan has no beats.");
        if (plan.outcomes().size() < 2) throw new IllegalStateException("Arc plan needs at least two outcomes.");
    }

    private static void enforceBrief(GenerationBrief brief, ScenePackProject project) {
        int cards = project.nodes().size();
        if (cards < brief.constraints().minimumCards()) {
            throw new IllegalStateException("Generated graph has " + cards + " cards; expected at least "
                    + brief.constraints().minimumCards() + ".");
        }
        if (project.allowRawMechanics()) throw new IllegalStateException("Generated projects cannot enable raw mechanics.");
    }

    private static String errorSummary(ValidationReport validation) {
        return validation.diagnostics().stream()
                .filter(issue -> issue.severity() == Severity.ERROR)
                .limit(12)
                .map(issue -> "[" + issue.code()
                        + (issue.node() == null ? "" : " @ " + issue.node())
                        + "] " + issue.message())
                .collect(java.util.stream.Collectors.joining("; "));
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
        private final GenerationProgressListener progress;
        private int calls;
        private int inputCharacters;
        private int outputCharacters;
        private int inputTokens;
        private int outputTokens;

        Session(GenerationBudget budget, Path archive, GenerationProgressListener progress) {
            this.budget = budget;
            this.archive = archive;
            this.progress = progress;
        }

        <T> T call(NarrativeModel model, GenerationStage stage, String system, String user, Class<T> type) throws Exception {
            if (calls >= budget.maximumCalls()) throw new IllegalStateException("Generation call budget exhausted.");
            if (inputCharacters + system.length() + user.length() > budget.maximumInputCharacters()) {
                throw new IllegalStateException("Generation input budget exhausted.");
            }
            int number = ++calls;
            progress.stageStarted(stage, number);
            inputCharacters += system.length() + user.length();
            ModelRequest request = new ModelRequest(
                    stage,
                    system,
                    user,
                    GenerationSchemas.forType(type),
                    budget.maximumOutputCharacters() - outputCharacters);
            Path directory = archive.resolve(String.format("%02d-%s", number, stage.name().toLowerCase(java.util.Locale.ROOT)));
            Files.createDirectories(directory);
            JsonObject archivedRequest = new JsonObject();
            archivedRequest.addProperty("stage", stage.name());
            archivedRequest.addProperty("systemPrompt", system);
            archivedRequest.addProperty("userPrompt", user);
            write(directory.resolve("request.json"), ProjectJson.gson().toJson(archivedRequest));
            Instant started = Instant.now();
            ModelResponse response;
            try {
                response = model.generate(request);
            } catch (Exception exception) {
                JsonObject metadata = new JsonObject();
                metadata.addProperty("stage", stage.name());
                metadata.addProperty("status", "FAILED");
                metadata.addProperty("startedAt", started.toString());
                metadata.addProperty("failedAt", Instant.now().toString());
                metadata.addProperty("errorType", exception.getClass().getName());
                metadata.addProperty("error", exception.getMessage() == null
                        ? exception.getClass().getSimpleName() : exception.getMessage());
                write(directory.resolve("metadata.json"), ProjectJson.gson().toJson(metadata));
                throw exception;
            }
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
            progress.stageCompleted(stage, number);
            return ProjectJson.gson().fromJson(normalizeProjectResponse(response.structuredOutput(), type), type);
        }

        private static JsonElement normalizeProjectResponse(JsonElement response, Class<?> type) {
            if (type == Intentions.class) return normalizeLegacyIntentionsResponse(response);
            if (type == DialoguePass.class) return normalizeLegacyDialogueResponse(response);
            if (type != ScenePackProject.class || !response.isJsonObject()) return response;
            JsonObject normalized = response.deepCopy().getAsJsonObject();
            if (!normalized.has("nodes") || !normalized.get("nodes").isJsonArray()) return normalized;
            for (JsonElement element : normalized.getAsJsonArray("nodes")) {
                if (!element.isJsonObject()) continue;
                JsonObject node = element.getAsJsonObject();
                normalizeSpeaker(node);
                normalizeConditions(node);
                normalizeTrigger(node);
            }
            normalizeDeclaredOutcomes(normalized);
            return normalized;
        }

        private static void normalizeDeclaredOutcomes(JsonObject project) {
            if (!project.has("contract") || !project.get("contract").isJsonObject()) return;
            JsonObject contract = project.getAsJsonObject("contract");
            JsonArray outcomes = contract.has("outcomes") && contract.get("outcomes").isJsonArray()
                    ? contract.getAsJsonArray("outcomes")
                    : new JsonArray();
            contract.add("outcomes", outcomes);
            Set<String> declared = new HashSet<>();
            for (JsonElement outcome : outcomes) {
                if (outcome.isJsonPrimitive()) declared.add(outcome.getAsString());
            }
            for (JsonElement element : project.getAsJsonArray("nodes")) {
                if (!element.isJsonObject()) continue;
                String ending = stringValue(element.getAsJsonObject(), "ending");
                if (!ending.isBlank() && declared.add(ending)) outcomes.add(ending);
            }
        }

        private static JsonElement normalizeLegacyIntentionsResponse(JsonElement response) {
            if (!response.isJsonObject()) return response;
            JsonObject result = response.deepCopy().getAsJsonObject();
            if (!result.has("scenes") && result.has("sceneIntentions")
                    && result.get("sceneIntentions").isJsonArray()) {
                JsonArray scenes = new JsonArray();
                for (JsonElement element : result.getAsJsonArray("sceneIntentions")) {
                    if (!element.isJsonObject()) continue;
                    JsonObject legacy = element.getAsJsonObject();
                    JsonObject scene = new JsonObject();
                    scene.addProperty("node", stringValue(legacy, "nodeId"));
                    scene.addProperty("speakerObjective", stringValue(legacy, "intention"));
                    scene.addProperty("emotionalState", "");
                    scene.add("mayReveal", new JsonArray());
                    scene.add("mustNotReveal", new JsonArray());
                    scene.add("playerChoicePurpose", new JsonObject());
                    scene.add("continuity", new JsonArray());
                    scenes.add(scene);
                }
                result.remove("sceneIntentions");
                result.add("scenes", scenes);
            }
            if (!result.has("scenes") || !result.get("scenes").isJsonArray()) return result;
            for (JsonElement element : result.getAsJsonArray("scenes")) {
                if (!element.isJsonObject()) continue;
                JsonObject scene = element.getAsJsonObject();
                normalizeStringList(scene, "mayReveal");
                normalizeStringList(scene, "mustNotReveal");
                normalizeStringList(scene, "continuity");
                if (scene.has("playerChoicePurpose")
                        && !scene.get("playerChoicePurpose").isJsonNull()
                        && !scene.get("playerChoicePurpose").isJsonObject()) {
                    JsonObject purpose = new JsonObject();
                    purpose.addProperty("summary", scene.get("playerChoicePurpose").getAsString());
                    scene.add("playerChoicePurpose", purpose);
                }
            }
            return result;
        }

        private static void normalizeStringList(JsonObject object, String property) {
            if (!object.has(property) || object.get(property).isJsonNull()) {
                object.add(property, new JsonArray());
            } else if (!object.get(property).isJsonArray()) {
                JsonArray values = new JsonArray();
                values.add(object.get(property).getAsString());
                object.add(property, values);
            }
        }

        private static JsonElement normalizeLegacyDialogueResponse(JsonElement response) {
            if (!response.isJsonObject()) return response;
            JsonObject object = response.getAsJsonObject();
            if (!object.has("projectFormat") || !object.has("nodes") || !object.get("nodes").isJsonArray()) {
                return response;
            }
            JsonObject result = new JsonObject();
            JsonArray patches = new JsonArray();
            for (JsonElement element : object.getAsJsonArray("nodes")) {
                if (!element.isJsonObject()) continue;
                JsonObject legacyNode = element.getAsJsonObject();
                JsonObject patch = new JsonObject();
                patch.addProperty("node", stringValue(legacyNode, "id"));
                patch.addProperty("text", stringValue(legacyNode, "text"));
                patch.add("speaker", legacyNode.has("speaker") && legacyNode.get("speaker").isJsonObject()
                        ? legacyNode.getAsJsonObject("speaker").deepCopy()
                        : emptySpeaker());
                JsonArray labels = new JsonArray();
                if (legacyNode.has("responses") && legacyNode.get("responses").isJsonArray()) {
                    for (JsonElement edgeElement : legacyNode.getAsJsonArray("responses")) {
                        labels.add(stringValue(edgeElement.getAsJsonObject(), "label"));
                    }
                }
                patch.add("responseLabels", labels);
                patches.add(patch);
            }
            result.add("nodes", patches);
            return result;
        }

        private static JsonObject emptySpeaker() {
            JsonObject speaker = new JsonObject();
            speaker.add("emotion", JsonNull.INSTANCE);
            speaker.add("animation", JsonNull.INSTANCE);
            return speaker;
        }

        private static void normalizeSpeaker(JsonObject node) {
            if (!node.has("speaker") || !node.get("speaker").isJsonNull()) return;
            JsonObject speaker = new JsonObject();
            speaker.add("emotion", JsonNull.INSTANCE);
            speaker.add("animation", JsonNull.INSTANCE);
            node.add("speaker", speaker);
        }

        private static void normalizeConditions(JsonObject node) {
            if (!node.has("conditions") || !node.get("conditions").isJsonArray()) return;
            for (JsonElement element : node.getAsJsonArray("conditions")) {
                if (!element.isJsonObject()) continue;
                JsonObject condition = element.getAsJsonObject();
                if (!"BLOCK".equals(stringValue(condition, "type"))) continue;
                if (hasText(condition, "item") || !hasText(condition, "marker")) continue;
                condition.addProperty("item", condition.get("marker").getAsString());
                condition.add("marker", JsonNull.INSTANCE);
            }
        }

        private static void normalizeTrigger(JsonObject node) {
            String trigger = stringValue(node, "trigger");
            if (!trigger.isBlank()) node.addProperty("trigger", TriggerTypes.canonicalize(trigger));
        }

        private static boolean hasText(JsonObject object, String property) {
            return object.has(property)
                    && !object.get(property).isJsonNull()
                    && object.get(property).isJsonPrimitive()
                    && !object.get(property).getAsString().isBlank();
        }

        private static String stringValue(JsonObject object, String property) {
            return hasText(object, property) ? object.get(property).getAsString() : "";
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
