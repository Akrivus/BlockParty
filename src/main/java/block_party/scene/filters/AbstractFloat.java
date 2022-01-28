package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class AbstractFloat implements ISceneFilter {
    protected Function<BlockPartyNPC, Float> function;
    private Operation operation;
    private float value;

    public AbstractFloat(Function<BlockPartyNPC, Float> function) {
        this.function = function;
    }

    public AbstractFloat() { }

    public boolean verify(BlockPartyNPC npc) {
        return this.operation.test(this.value, this.function.apply(npc));
    }

    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "equals"));
        this.value = GsonHelper.getAsFloat(json, "value", 0);
    }

    public enum Operation {
        GREATER_THAN((x, y) -> x > y),
        GREATER_THAN_EQUALS((x, y) -> x >= y),
        LESS_THAN((x, y) -> x < y),
        LESS_THAN_EQUALS((x, y) -> x <= y),
        EQUALS((x, y) -> x == y);

        private final BiPredicate<Float, Float> function;

        Operation(BiPredicate<Float, Float> function) {
            this.function = function;
        }

        public boolean test(float x, float y) {
            return this.function.test(x, y);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
