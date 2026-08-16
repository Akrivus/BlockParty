package block_party.conversation.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class GenerationArchiveReader {
    private static final List<String> ROOT_DOCUMENTS =
            List.of(
                    "brief.json",
                    "catalog.json",
                    "context.json",
                    "graph-validation.json",
                    "intentions.json",
                    "review.json");
    private static final List<String> STAGE_DOCUMENTS =
            List.of("metadata.json", "request.json", "response.json");

    JsonObject read(Path projectPath) throws Exception {
        JsonObject result = new JsonObject();
        Path root = generationRoot(projectPath);
        result.addProperty("available", root != null);
        if (root == null) {
            return result;
        }

        for (String name : ROOT_DOCUMENTS) {
            addJsonFile(result, jsonName(name), root.resolve(name));
        }

        Path archive = root.resolve("generation");
        addJsonFile(result, "manifest", archive.resolve("manifest.json"));
        result.add("stages", readStages(archive));
        return result;
    }

    static Path generationRoot(Path projectPath) {
        Path parent = projectPath.getParent();
        if (parent == null) return null;
        if (Files.isDirectory(parent.resolve("generation"))) return parent;
        Path generated = parent.resolve("generated");
        return Files.isDirectory(generated.resolve("generation")) ? generated : null;
    }

    private JsonArray readStages(Path archive) throws Exception {
        JsonArray stages = new JsonArray();
        if (!Files.isDirectory(archive)) {
            return stages;
        }

        try (var children = Files.list(archive)) {
            for (Path directory : children.filter(Files::isDirectory).sorted().toList()) {
                if ("revisions".equals(directory.getFileName().toString())) {
                    continue;
                }

                JsonObject stage = new JsonObject();
                stage.addProperty("directory", directory.getFileName().toString());
                for (String name : STAGE_DOCUMENTS) {
                    addJsonFile(stage, jsonName(name), directory.resolve(name));
                }
                stages.add(stage);
            }
        }
        return stages;
    }

    private static void addJsonFile(JsonObject target, String property, Path file) throws Exception {
        if (Files.isRegularFile(file)) {
            target.add(property, JsonParser.parseString(Files.readString(file)));
        }
    }

    private static String jsonName(String fileName) {
        return fileName.substring(0, fileName.length() - ".json".length());
    }
}
