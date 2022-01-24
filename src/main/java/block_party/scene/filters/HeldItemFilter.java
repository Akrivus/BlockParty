package block_party.scene.filters;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

public class HeldItemFilter extends ItemFilter {
    protected InteractionHand hand;

    public HeldItemFilter() {
        this.getter = (npc) -> npc.getItemInHand(this.hand);
    }

    public void parse(JsonObject json) {
        this.hand = InteractionHand.valueOf(GsonHelper.getAsString(json, "hand", "main_hand").toUpperCase());
        super.parse(json);
    }

    public static class Player extends HeldItemFilter {
        public Player() {
            this.getter = (npc) -> npc.getServerPlayer().getItemInHand(this.hand);
        }
    }
}
