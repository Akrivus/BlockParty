package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.dialogue.ResponseIcon;
import block_party.utils.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class ResponseAction implements ISceneAction {
    protected ResponseIcon icon;
    protected String text;
    protected List<ISceneAction> actions;
    private boolean complete;

    @Override
    public void apply(BlockPartyNPC npc) {
        npc.automaton.putActions(this.actions);
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void parse(JsonObject json) {
        this.icon = ResponseIcon.CLOSE_DIALOGUE.fromValue(JsonUtils.getAsResourceLocation(json, "icon"));
        this.text = GsonHelper.getAsString(json, "text");
        this.actions = ISceneAction.parseArray(json.getAsJsonArray("actions"));
    }

    public ResponseIcon getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }
}
