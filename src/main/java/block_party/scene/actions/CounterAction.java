package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariables;
import block_party.scene.data.Counters;
import java.util.Locale;

public record CounterAction(Operation operation, String name, int value) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        Counters counters = SceneVariables.get(moe.level()).counters(moe.getDatabaseID());
        this.operation.apply(counters, this.name, this.value);
    }

    public enum Operation {
        ADD {
            @Override
            void apply(Counters counters, String name, int value) {
                counters.increment(name, value);
            }
        },
        SUBTRACT {
            @Override
            void apply(Counters counters, String name, int value) {
                counters.decrement(name, value);
            }
        },
        SET {
            @Override
            void apply(Counters counters, String name, int value) {
                counters.set(name, value);
            }
        },
        DELETE {
            @Override
            void apply(Counters counters, String name, int value) {
                counters.delete(name);
            }
        };

        abstract void apply(Counters counters, String name, int value);

        public static Operation fromValue(String value) {
            try {
                return Operation.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return ADD;
            }
        }
    }
}
