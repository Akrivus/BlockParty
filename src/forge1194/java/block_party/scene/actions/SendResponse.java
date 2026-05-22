package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.Response;
import block_party.utils.Trans;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class SendResponse extends Abstract1Shot {
    protected Response icon;
    protected String text;
    protected List<ISceneAction> actions;

    @Override
    public void apply(BlockPartyNPC npc) {
        npc.sceneManager.putActions(this.actions);
    }

    @Override
    public void parse(JsonObject json) {
        if (json.has("icon")) {
            ResourceLocation icon = ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon", ""));
            this.icon = icon == null ? Response.CLOSE_DIALOGUE : Response.CLOSE_DIALOGUE.fromValue(icon);
        } else {
            this.icon = Response.CLOSE_DIALOGUE;
        }
        this.text = GsonHelper.getAsString(json, "text", Trans.late(this.icon.getTranslationKey()));
        JsonArray actions = json.has("actions") && json.get("actions").isJsonArray() ? json.getAsJsonArray("actions") : new JsonArray();
        this.actions = ISceneAction.parseArray(actions);
    }

    public Response getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

}
