package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.CookieJar;
import block_party.scene.data.Cookies;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.util.TriConsumer;

public class DoCookie extends Abstract1Shot {
    protected Operation operation;
    protected String name;
    protected String value;

    @Override
    public void apply(BlockPartyNPC npc) {
        this.operation.accept(CookieJar.getCookies(npc), this.name, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "set"));
        this.name = GsonHelper.getAsString(json, "name");
        this.value = GsonHelper.getAsString(json, "value", "");
    }

    public static class Player extends DoCookie {
        @Override
        public void apply(BlockPartyNPC npc) {
            if (!npc.isPlayerOnline()) { return; }
            Cookies cookies = CookieJar.getCookies(npc.getServerPlayer());
            this.operation.accept(cookies, this.name, this.value);
            CookieJar.save(npc.getLevel());
        }
    }

    public enum Operation {
        SET((x, y, z) -> x.set(y, z)),
        DELETE((x, y, z) -> x.delete(y));

        private final TriConsumer<Cookies, String, String> function;

        Operation(TriConsumer<Cookies, String, String> function) {
            this.function = function;
        }

        public void accept(Cookies x, String y, String z) {
            this.function.accept(x, y, z);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
