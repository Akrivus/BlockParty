package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.Response;
import block_party.scene.ISceneAction;
import block_party.utils.JsonUtils;
import block_party.utils.Trans;
import com.google.gson.JsonObject;
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
        this.icon = Response.CLOSE_DIALOGUE.fromValue(JsonUtils.getAsResourceLocation(json, "icon"));
        this.text = GsonHelper.getAsString(json, "text", Trans.late(this.icon.getTranslationKey()));
        this.actions = ISceneAction.parseArray(json.getAsJsonArray("actions"));
    }

    public Response getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

}
