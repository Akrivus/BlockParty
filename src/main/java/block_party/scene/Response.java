package block_party.scene;

import block_party.npc.BlockPartyNPC;
import block_party.utils.JsonUtils;
import block_party.utils.Trans;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;

public class Response implements ISceneAction {
    protected Icon icon;
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
        this.icon = Icon.CLOSE_DIALOGUE.fromValue(JsonUtils.getAsResourceLocation(json, "icon"));
        this.text = GsonHelper.getAsString(json, "text", Trans.late(this.icon.getTranslationKey()));
        this.actions = ISceneAction.parseArray(json.getAsJsonArray("actions"));
    }

    public Icon getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public enum Icon {
        GREEN_CHECKMARK, RED_X, CHAT_BUBBLE, LOVELY_HEART, TRUSTY_ARMOR, STRESSFUL_SKULL, LEATHER_BAG, ANVIL, NEXT_RESPONSE, CLOSE_DIALOGUE, OPEN_DIALOGUE;

        public Icon fromValue(ResourceLocation location) {
            return fromValue(location.getPath());
        }

        public Icon fromValue(String key) {
            try {
                return Icon.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this;
            }
        }

        public String getTranslationKey() {
            return String.format("gui.block_party.dialogue.response.%s", this.name().toLowerCase());
        }
    }
}
