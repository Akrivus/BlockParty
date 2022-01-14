package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import block_party.world.PlayerData;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class CookieFilter implements ISceneFilter {
    protected String cookie;

    public boolean verify(BlockPartyNPC npc) {
        return npc.automaton.hasCookie(this.cookie);
    }

    public void parse(JsonObject json) {
        this.cookie = GsonHelper.getAsString(json, "cookie");
    }

    public static class Player extends CookieFilter {
        public boolean verify(BlockPartyNPC npc) {
            ServerPlayer player = (ServerPlayer) npc.getPlayer();
            return PlayerData.getCookiesFor(player).has(this.cookie);
        }
    }
}
