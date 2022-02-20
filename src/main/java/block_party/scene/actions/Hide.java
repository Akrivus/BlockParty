package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.entities.goals.HideUntil;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class Hide extends Abstract1Shot {
    private HideUntil until;

    @Override
    public void apply(BlockPartyNPC npc) {
        npc.hide(this.until);
    }

    @Override
    public void parse(JsonObject json) {
        this.until = HideUntil.EXPOSED.fromValue(GsonHelper.getAsString(json, "until", "exposed"));
    }
}
