package block_party.scene.filters;

import block_party.world.PlayerData;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class CookieFilter extends StringFilter {
    protected String name;

    public CookieFilter() {
        this.function = (npc) -> npc.automaton.cookies.get(this.name);
    }

    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
        super.parse(json);
    }

    public static class Player extends CookieFilter {
        public Player() {
            this.function = (npc) -> PlayerData.getCookiesFor(npc.getServerPlayer()).get(this.name);
        }
    }
}
