package block_party.conversation.compile;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.PackAction;
import block_party.conversation.model.ProjectIndex;
import block_party.conversation.model.ResponseEdge;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ScenePackProject;
import block_party.conversation.model.TriggerTypes;
import block_party.conversation.validation.ProjectValidator;
import block_party.conversation.validation.ValidationReport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DatapackCompiler {
    public CompilationResult compile(ScenePackProject project, Path output) throws IOException {
        ValidationReport validation = new ProjectValidator().validate(project);
        if (!validation.valid()) {
            throw new IllegalArgumentException("Cannot compile a project with " + validation.errors() + " validation error(s).");
        }
        refuseNonEmptyOutput(output);
        Files.createDirectories(output);

        List<Path> files = new ArrayList<>();
        JsonObject pack = new JsonObject();
        JsonObject details = new JsonObject();
        details.addProperty("description", project.pack().title());
        int packFormat = project.target() == null ? project.pack().format() : project.target().packFormat();
        details.addProperty("pack_format", packFormat);
        pack.add("pack", details);
        files.add(writeJson(output.resolve("pack.mcmeta"), pack));

        ProjectIndex index = new ProjectIndex(project);
        Map<String, SceneNode> nodes = index.nodes();
        List<SceneNode> roots = new ArrayList<>();
        roots.add(nodes.get(project.entry()));
        project.nodes().stream()
                .filter(node -> node.type() == NodeType.GAMEPLAY_GATE && !node.id().equals(project.entry()))
                .forEach(roots::add);

        Path scenes = output.resolve("data").resolve(project.pack().namespace()).resolve("scenes").resolve(project.pack().id());
        for (SceneNode root : roots) {
            JsonObject scene = new JsonObject();
            scene.addProperty("trigger", compileTrigger(root.trigger()));
            JsonArray filters = new JsonArray();
            root.conditions().forEach(condition -> filters.add(MechanicsCompiler.condition(condition, index)));
            scene.add("filters", filters);
            scene.add("actions", renderNode(root, nodes, index, new HashSet<>()));
            files.add(writeJson(scenes.resolve(root.id() + ".json"), scene));
        }

        JsonObject manifest = new JsonObject();
        manifest.addProperty("generator", "block-party-conversation-tool");
        manifest.addProperty("format", 1);
        manifest.addProperty("project_format", project.projectFormat());
        manifest.addProperty("project", project.pack().id());
        manifest.add("contract", ProjectJson.gson().toJsonTree(project.contract()));
        manifest.addProperty("source_sha256", projectHash(project));
        files.add(writeJson(output.resolve("generation-manifest.json"), manifest));
        return new CompilationResult(output, files);
    }

    private static JsonArray renderNode(SceneNode node, Map<String, SceneNode> nodes, ProjectIndex index, Set<String> stack) {
        if (!stack.add(node.id())) {
            throw new IllegalArgumentException("Immediate dialogue cycle reaches '" + node.id() + "'. Use a gameplay gate to break runtime recursion.");
        }
        JsonArray actions = compileActions(node.actions(), index);
        if (node.type() == NodeType.END) {
            appendEnd(actions);
        } else if (node.type() == NodeType.GAMEPLAY_GATE) {
            appendTarget(actions, node.next(), null, nodes, index, stack);
        } else {
            JsonObject payload = new JsonObject();
            payload.addProperty("text", node.text());
            payload.addProperty("tooltip", node.tooltip());
            if (node.speaker() != null) {
                payload.add("speaker", node.speaker().deepCopy());
            }
            JsonArray responses = new JsonArray();
            for (ResponseEdge edge : node.responses()) {
                JsonObject response = new JsonObject();
                response.addProperty("icon", qualify(edge.cue()));
                if (edge.label() != null && !edge.label().isBlank()) {
                    response.addProperty("text", edge.label());
                }
                JsonArray responseActions = compileActions(edge.actions(), index);
                appendTarget(responseActions, edge.target(), edge.transition(), nodes, index, new HashSet<>(stack));
                response.add("actions", responseActions);
                responses.add(response);
            }
            payload.add("responses", responses);
            JsonObject dialogue = new JsonObject();
            dialogue.addProperty("type", "block_party:send_dialogue");
            dialogue.add("action", payload);
            actions.add(dialogue);
        }
        return actions;
    }

    private static void appendTarget(JsonArray actions, String target, block_party.conversation.model.TransitionType transition,
            Map<String, SceneNode> nodes, ProjectIndex index, Set<String> stack) {
        SceneNode next = nodes.get(target);
        if (next == null) {
            return;
        }
        if (next.type() == NodeType.GAMEPLAY_GATE
                || transition == block_party.conversation.model.TransitionType.LATER_INTERACTION
                || transition == block_party.conversation.model.TransitionType.EXTERNAL_EVENT
                || transition == block_party.conversation.model.TransitionType.PACK_EXIT) {
            appendEnd(actions);
            return;
        }
        for (JsonElement action : renderNode(next, nodes, index, stack)) {
            actions.add(action.deepCopy());
        }
    }

    private static void appendEnd(JsonArray actions) {
        if (actions.isEmpty() || !actions.get(actions.size() - 1).isJsonPrimitive()
                || !"block_party:end".equals(actions.get(actions.size() - 1).getAsString())) {
            actions.add(new JsonPrimitive("block_party:end"));
        }
    }

    private static JsonArray compileActions(List<PackAction> values, ProjectIndex index) {
        JsonArray result = new JsonArray();
        values.forEach(value -> result.add(MechanicsCompiler.action(value, index)));
        return result;
    }

    private static String projectHash(ScenePackProject project) {
        try {
            byte[] content = ProjectJson.gson().toJson(project).getBytes(StandardCharsets.UTF_8);
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256").digest(content);
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static Path writeJson(Path path, JsonObject value) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, ProjectJson.gson().toJson(value) + System.lineSeparator(), StandardCharsets.UTF_8);
        return path;
    }

    private static void refuseNonEmptyOutput(Path output) throws IOException {
        if (!Files.isDirectory(output)) {
            return;
        }
        try (var children = Files.list(output)) {
            if (children.findAny().isPresent()) {
                throw new IOException("Output directory is not empty: " + output);
            }
        }
    }

    private static String qualify(String value) {
        return value.contains(":") ? value : "block_party:" + value;
    }

    private static String compileTrigger(String value) {
        return TriggerTypes.qualified(value);
    }
}
