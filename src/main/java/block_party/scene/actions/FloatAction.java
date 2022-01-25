package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FloatAction implements ISceneAction {
    protected Function<BlockPartyNPC, Float> getter;
    protected BiConsumer<BlockPartyNPC, Float> setter;
    private Operation operation;
    private float value;

    public FloatAction(Function<BlockPartyNPC, Float> getter, BiConsumer<BlockPartyNPC, Float> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public FloatAction() { }

    @Override
    public void apply(BlockPartyNPC npc) {
        float x = this.getter.apply(npc);
        float z = this.operation.accept(x, this.value);
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

        private final BiFunction<Float, Float, Float> function;

        Operation(BiFunction<Float, Float, Float> function) {
            this.function = function;
        }

        public float accept(float x, float y) {
            return this.function.apply(x, y);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
