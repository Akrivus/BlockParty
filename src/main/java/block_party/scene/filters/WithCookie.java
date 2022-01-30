package block_party.scene.filters;

import block_party.scene.SceneVariables;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public class WithCookie extends AbstractString {
    protected String name;

    public WithCookie() {
        this.function = (npc) -> SceneVariables.getCookies(npc).get(this.name);
    }

    public void parse(JsonObject json) {
        this.name = GsonHelper.getAsString(json, "name");
        super.parse(json);
    }

    public static class Player extends WithCookie {
        public Player() {
            this.function = (npc) -> SceneVariables.getCookies(npc.getServerPlayer()).get(this.name);
        }
    }
}
