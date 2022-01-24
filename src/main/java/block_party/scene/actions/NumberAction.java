package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import block_party.scene.filters.NumberFilter;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class NumberAction implements ISceneAction {
    protected Function<BlockPartyNPC, Integer> getter;
    protected BiConsumer<BlockPartyNPC, Integer> setter;
    private Operation operation;
    private int value;

    public NumberAction(Function<BlockPartyNPC, Integer> getter, BiConsumer<BlockPartyNPC, Integer> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public NumberAction() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        int x = this.getter.apply(npc);
        int z = this.operation.accept(x, this.value);
        this.setter.accept(npc, z);
    }

    @Override
    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "add"));
        this.value = GsonHelper.getAsInt(json, "value", 1);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    public enum Operation {
        ADD((x, y) -> x + y),
        SUBTRACT((x, y) -> y - x),
        SET((x, y) -> y);

        private final BiFunction<Integer, Integer, Integer> function;

        Operation(BiFunction<Integer, Integer, Integer> function) {
            this.function = function;
        }

        public int accept(int x, int y) {
            return this.function.apply(x, y);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
