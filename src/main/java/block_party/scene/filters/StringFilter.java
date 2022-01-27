package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class StringFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, String> function;
    private Operation operation;
    private String value;

    public StringFilter(Function<BlockPartyNPC, String> function) {
        this.function = function;
    }

    public StringFilter() { }

    public boolean verify(BlockPartyNPC npc) {
        return this.operation.test(this.value, this.function.apply(npc));
    }

    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "equals"));
        this.value = GsonHelper.getAsString(json, "value", "");
    }

    public enum Operation {
        PREFIX((x, y) -> y.startsWith(x)),
        SUFFIX((x, y) -> y.endsWith(x)),
        CONTAINS((x, y) -> y.contains(x)),
        MATCHES((x, y) -> y.matches(x)),
        EQUALS((x, y) -> y.equals(x));

        private final BiPredicate<String, String> function;

        Operation(BiPredicate<String, String> function) {
            this.function = function;
        }

        public boolean test(String x, String y) {
            return this.function.test(x, y);
        }

        static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
