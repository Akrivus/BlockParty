package block_party.scene.filters;

import block_party.scene.CookieJar;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class WithCounter extends AbstractInteger {
    protected String name;

    public WithCounter() {
        this.function = (npc) -> CookieJar.getCounters(npc).get(this.name);
    }

    @Override
    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
        super.parse(json);
    }

    public static class Player extends WithCounter {
        public Player() {
            this.function = (npc) -> CookieJar.getCounters(npc.getServerPlayer()).get(this.name);
        }
    }
}
