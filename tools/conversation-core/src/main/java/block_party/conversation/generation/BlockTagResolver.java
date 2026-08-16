package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BlockTagResolver {
    private final Map<String, TagDefinition> tags;
    private final List<String> warnings = new ArrayList<>();

    BlockTagResolver(Path repositoryRoot) throws Exception {
        tags = readTags(repositoryRoot.resolve("src/main/resources/data"));
    }

    List<String> tagsFor(String block) {
        return tags.keySet().stream()
                .filter(tag -> contains(tag, block, new HashSet<>()))
                .sorted()
                .toList();
    }

    List<String> warnings() {
        return List.copyOf(warnings);
    }

    private boolean contains(String tag, String block, Set<String> visiting) {
        if (!visiting.add(tag)) {
            String warning = "Cyclic block tag reference encountered at #" + tag;
            if (!warnings.contains(warning)) {
                warnings.add(warning);
            }
            return false;
        }
        TagDefinition definition = tags.get(tag);
        if (definition == null) {
            visiting.remove(tag);
            return false;
        }
        for (String value : definition.values()) {
            if (value.equals(block)
                    || value.startsWith("#") && contains(value.substring(1), block, visiting)) {
                visiting.remove(tag);
                return true;
            }
        }
        visiting.remove(tag);
        return false;
    }

    private static Map<String, TagDefinition> readTags(Path dataRoot) throws Exception {
        Map<String, TagDefinition> result = new LinkedHashMap<>();
        if (!Files.isDirectory(dataRoot)) {
            return result;
        }
        try (var namespaces = Files.list(dataRoot)) {
            for (Path namespace : namespaces.sorted().toList()) {
                Path blockTags = namespace.resolve("tags/block");
                if (!Files.isDirectory(blockTags)) {
                    continue;
                }
                try (var files = Files.walk(blockTags)) {
                    for (Path file : files.filter(path -> path.toString().endsWith(".json")).sorted().toList()) {
                        String relative = blockTags.relativize(file).toString()
                                .replace('\\', '/')
                                .replaceFirst("\\.json$", "");
                        String id = namespace.getFileName() + ":" + relative;
                        JsonObject json = ProjectJson.gson().fromJson(
                                Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
                        List<String> values = new ArrayList<>();
                        for (JsonElement element : json.getAsJsonArray("values")) {
                            if (element.isJsonPrimitive()) {
                                values.add(element.getAsString());
                            } else if (element.isJsonObject() && element.getAsJsonObject().has("id")) {
                                values.add(element.getAsJsonObject().get("id").getAsString());
                            }
                        }
                        boolean replace = json.has("replace") && json.get("replace").getAsBoolean();
                        TagDefinition previous = result.get(id);
                        if (!replace && previous != null) {
                            List<String> merged = new ArrayList<>(previous.values());
                            merged.addAll(values);
                            values = merged;
                        }
                        result.put(id, new TagDefinition(List.copyOf(values)));
                    }
                }
            }
        }
        return result;
    }

    private record TagDefinition(List<String> values) {
    }
}
