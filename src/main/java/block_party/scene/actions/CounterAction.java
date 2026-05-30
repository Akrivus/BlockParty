package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariableScope;
import block_party.scene.SceneVariableStore;
import block_party.scene.SceneVariables;
import block_party.scene.data.Counters;
import java.util.Locale;

public record CounterAction(Operation operation, String name, int value, SceneVariableScope scope) implements SceneAction {
    public CounterAction(Operation operation, String name, int value) {
        this(operation, name, value, SceneVariableScope.NPC);
    }

    @Override
    public void apply(Moe moe) {
        Counters counters = scopedVariables(moe, this.scope).counters();
        this.operation.apply(counters, this.name, this.value);
    }

    private static SceneVariableStore scopedVariables(Moe moe, SceneVariableScope scope) {
        SceneVariables variables = SceneVariables.get(moe.level());
        return switch (scope) {
            case NPC -> variables.npc(moe.getDatabaseID());
            case PLAYER -> variables.player(SceneActionPlayers.targetPlayerUuid(moe));
            case WORLD -> variables.world();
        };
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
