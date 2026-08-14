package block_party.conversation.generation.model;

import block_party.conversation.io.ProjectJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public final class RecordedDirectoryModel implements NarrativeModel {
    private final Path directory;
    private final Map<block_party.conversation.generation.GenerationStage, Integer> calls =
            new EnumMap<>(block_party.conversation.generation.GenerationStage.class);

    public RecordedDirectoryModel(Path directory) {
        this.directory = directory;
    }

    @Override
    public ModelResponse generate(ModelRequest request) throws Exception {
        int call = calls.merge(request.stage(), 1, Integer::sum);
        String name = request.stage().name().toLowerCase(java.util.Locale.ROOT).replace('_', '-');
        Path numbered = directory.resolve(name + "-" + call + ".json");
        Path path = Files.isRegularFile(numbered) ? numbered : directory.resolve(name + ".json");
        if (!Files.isRegularFile(path)) {
            try (var children = Files.list(directory)) {
                var archived = children.filter(Files::isDirectory)
                        .filter(value -> value.getFileName().toString().endsWith("-" + name.replace('-', '_')))
                        .sorted().toList();
                if (call <= archived.size()) path = archived.get(call - 1).resolve("response.json");
            }
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("Missing recorded response for " + request.stage() + ": " + path);
        }
        String text = Files.readString(path, StandardCharsets.UTF_8);
        JsonElement output = JsonParser.parseString(text);
        if (output.isJsonObject() && output.getAsJsonObject().has("$file")) {
            Path referenced = directory.resolve(output.getAsJsonObject().get("$file").getAsString()).normalize();
            if (!referenced.startsWith(directory.normalize()) || !Files.isRegularFile(referenced)) {
                throw new IllegalStateException("Invalid recorded response reference: " + referenced);
            }
            output = JsonParser.parseString(Files.readString(referenced, StandardCharsets.UTF_8));
        }
        return new ModelResponse(output, new ModelUsage(0, 0), path.getFileName().toString(), "recorded", "fixture");
    }
}
