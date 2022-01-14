package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class NumberFilter implements ISceneFilter {
    protected Function<BlockPartyNPC, Integer> function;
    private Operation operation;
    private int numeric;

    public NumberFilter(Function<BlockPartyNPC, Integer> function) {
        this.function = function;
    }

    public NumberFilter() { }

    public boolean verify(BlockPartyNPC npc) {
        return this.operation.test(this.numeric, this.function.apply(npc));
    }

    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "equals"));
        this.numeric = GsonHelper.getAsInt(json, "numeric", 0);
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
            return Operation.get(key.toUpperCase());
        }
    }
}
