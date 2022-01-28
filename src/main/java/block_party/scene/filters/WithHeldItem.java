package block_party.scene.filters;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;

public class WithHeldItem extends AbstractItem {
    protected InteractionHand hand;

    public WithHeldItem() {
        this.getter = (npc) -> npc.getItemInHand(this.hand);
    }

    public void parse(JsonObject json) {
        this.hand = InteractionHand.valueOf(GsonHelper.getAsString(json, "hand", "main_hand").toUpperCase());
        super.parse(json);
    }

    public static class Player extends WithHeldItem {
        public Player() {
            this.getter = (npc) -> npc.getServerPlayer().getItemInHand(this.hand);
        }
    }
}
