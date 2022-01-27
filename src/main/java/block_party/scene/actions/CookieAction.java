package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.Cookies;
import block_party.scene.ISceneAction;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.util.TriConsumer;

public class CookieAction implements ISceneAction {
    private Operation operation;
    private String name;
    private String value;

    @Override
    public void apply(BlockPartyNPC npc) {
        this.operation.accept(npc.sceneManager.cookies, this.name, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "set"));
        this.name = GsonHelper.getAsString(json, "name");
        this.value = GsonHelper.getAsString(json, "value", "");
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    public enum Operation {
        SET((x, y, z) -> x.add(y, z)),
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
