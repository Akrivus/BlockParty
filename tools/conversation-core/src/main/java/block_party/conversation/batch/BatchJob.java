package block_party.conversation.batch;

import block_party.conversation.generation.GenerationBrief;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.List;

public record BatchJob(
        String id,
        String family,
        int variation,
        Path directory,
        GenerationBrief brief,
        String trigger,
        List<JsonObject> filters) {
    public BatchJob {
        filters = filters.stream().map(JsonObject::deepCopy).toList();
    }
}
