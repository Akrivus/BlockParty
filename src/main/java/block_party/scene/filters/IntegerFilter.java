package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class IntegerFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, Integer> function;
    private Operation operation;
    private int value;

    public IntegerFilter(Function<BlockPartyNPC, Integer> function) {
        this.function = function;
    }

    public IntegerFilter() { }

    public boolean verify(BlockPartyNPC npc) {
        return this.operation.test(this.value, this.function.apply(npc));
    }

    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "equals"));
        this.value = GsonHelper.getAsInt(json, "value", 0);
    }

    public enum Operation {
        GREATER_THAN((x, y) -> x > y),
        GREATER_THAN_EQUALS((x, y) -> x >= y),
        LESS_THAN((x, y) -> x < y),
        LESS_THAN_EQUALS((x, y) -> x <= y),
        EQUALS((x, y) -> x == y);

        private final BiPredicate<Integer, Integer> function;

        Operation(BiPredicate<Integer, Integer> function) {
            this.function = function;
        }

        public boolean test(int x, int y) {
            return this.function.test(x, y);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
