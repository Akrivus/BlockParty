package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MechanicsFingerprint {
    private MechanicsFingerprint() { }

    public static String of(ScenePackProject project) {
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
}
