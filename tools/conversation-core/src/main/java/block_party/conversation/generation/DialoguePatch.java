package block_party.conversation.generation;

import com.google.gson.JsonObject;
import java.util.List;

public record DialoguePatch(String node, String text, JsonObject speaker, List<String> responseLabels) {
    public DialoguePatch {
        responseLabels = responseLabels == null ? List.of() : List.copyOf(responseLabels);
    }
}
