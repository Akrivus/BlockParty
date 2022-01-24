package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.Counters;
import block_party.scene.ISceneAction;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.util.TriConsumer;

public class CounterAction implements ISceneAction {
    private Operation operation;
    private String name;
    private int value;

    @Override
    public void apply(BlockPartyNPC npc) {
        this.operation.accept(npc.automaton.counters, this.name, this.value);
    }

    @Override
    public void parse(JsonObject json) {
        this.operation = Operation.get(GsonHelper.getAsString(json, "operation", "add"));
        this.name = GsonHelper.getAsString(json, "name");
        this.value = GsonHelper.getAsInt(json, "value", 1);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    public enum Operation {
        ADD((x, y, z) -> x.increment(y, z)),
        SUBTRACT((x, y, z) -> x.decrement(y, z)),
        SET((x, y, z) -> x.set(y, z)),
        DELETE((x, y, z) -> x.delete(y));

        private final TriConsumer<Counters, String, Integer> function;

        Operation(TriConsumer<Counters, String, Integer> function) {
            this.function = function;
        }

        public void accept(Counters x, String y, int z) {
            this.function.accept(x, y, z);
        }

        private static Operation get(String key) {
            return Operation.valueOf(key.toUpperCase());
        }
    }
}
