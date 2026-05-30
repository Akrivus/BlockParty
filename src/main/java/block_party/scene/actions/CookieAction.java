package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariableScope;
import block_party.scene.SceneVariableStore;
import block_party.scene.SceneVariables;
import block_party.scene.data.Cookies;
import java.util.Locale;

public record CookieAction(Operation operation, String name, String value, SceneVariableScope scope) implements SceneAction {
    public CookieAction(Operation operation, String name, String value) {
        this(operation, name, value, SceneVariableScope.NPC);
    }

    @Override
    public void apply(Moe moe) {
        Cookies cookies = scopedVariables(moe, this.scope).cookies();
        this.operation.apply(cookies, this.name, this.value);
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
        SET {
            @Override
            void apply(Cookies cookies, String name, String value) {
                cookies.set(name, value);
            }
        },
        DELETE {
            @Override
            void apply(Cookies cookies, String name, String value) {
                cookies.delete(name);
            }
        };

        abstract void apply(Cookies cookies, String name, String value);

        public static Operation fromValue(String value) {
            try {
                return Operation.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return SET;
            }
        }
    }
}
