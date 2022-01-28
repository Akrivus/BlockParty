package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.registry.SceneActions;
import block_party.scene.Response;
import block_party.scene.ISceneAction;
import block_party.scene.Speaker;
import block_party.utils.Markdown;
import com.google.common.collect.Maps;
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
        this.text = GsonHelper.getAsString(json, "text");
        this.speaker = new Speaker(json.getAsJsonObject("speaker"));
        this.responses = Maps.newHashMap();
        JsonArray responses = json.getAsJsonArray("responses");
        for (int i = 0; i < responses.size(); ++i) {
            SendResponse response = new SendResponse();
            JsonElement element = responses.get(i);
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
