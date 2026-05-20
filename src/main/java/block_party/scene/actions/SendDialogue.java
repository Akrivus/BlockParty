package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.registry.SceneActions;
import block_party.scene.ISceneAction;
import block_party.scene.Response;
import block_party.scene.Speaker;
import block_party.utils.Markdown;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.Map;

public class SendDialogue implements ISceneAction {
    public Map<Response, SendResponse> responses;
    protected boolean tooltip;
    protected String text;
    protected Speaker speaker;

    @Override
    public void apply(BlockPartyNPC npc) {
        npc.setDialogue(this);
    }

    @Override
    public boolean isComplete(BlockPartyNPC npc) {
        return npc.hasResponse();
    }

    @Override
    public void onComplete(BlockPartyNPC npc) {
        ISceneAction action = this.responses.get(npc.getResponse());
        if (action == null) { action = SceneActions.build(SceneActions.END); }
        npc.sceneManager.putAction(action);
    }

    @Override
    public void parse(JsonObject json) {
        this.tooltip = GsonHelper.getAsBoolean(json, "tooltip", false);
        this.text = GsonHelper.getAsString(json, "text", "");
        JsonObject speaker = json.has("speaker") && json.get("speaker").isJsonObject() ? json.getAsJsonObject("speaker") : new JsonObject();
        this.speaker = new Speaker(speaker);
        this.responses = new java.util.LinkedHashMap<>();
        JsonArray responses = json.has("responses") && json.get("responses").isJsonArray() ? json.getAsJsonArray("responses") : new JsonArray();
        for (int i = 0; i < responses.size(); ++i) {
            JsonElement element = responses.get(i);
            if (!element.isJsonObject()) { continue; }
            SendResponse response = new SendResponse();
            response.parse(element.getAsJsonObject());
            this.responses.put(response.getIcon(), response);
        }
    }

    public String getText(BlockPartyNPC npc) {
        return Markdown.markWithSubs(this.text, npc);
    }

    public boolean isTooltip() {
        return this.tooltip;
    }

    public Speaker getSpeaker() {
        return this.speaker;
    }

}
