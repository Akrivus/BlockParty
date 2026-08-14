package block_party.conversation.model;

import com.google.gson.JsonObject;
import java.util.List;

public record SceneNode(
        String id,
        NodeType type,
        String title,
        String trigger,
        List<PackCondition> conditions,
        String text,
        boolean tooltip,
        JsonObject speaker,
        List<ResponseEdge> responses,
        List<PackAction> actions,
        String next,
        String ending,
        EditorPosition editor) {
    public SceneNode {
        conditions = conditions == null ? List.of() : List.copyOf(conditions);
        responses = responses == null ? List.of() : List.copyOf(responses);
        actions = actions == null ? List.of() : List.copyOf(actions);
    }
}
