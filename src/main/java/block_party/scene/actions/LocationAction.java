package block_party.scene.actions;

import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneVariableScope;
import block_party.scene.SceneVariableStore;
import block_party.scene.SceneVariables;

public record LocationAction(Operation operation, String name, SceneVariableScope scope, Source source) implements SceneAction {
    public LocationAction {
        operation = operation == null ? Operation.REMEMBER : operation;
        scope = scope == null ? SceneVariableScope.NPC : scope;
        source = source == null ? Source.MOE : source;
    }

    @Override
    public void apply(Moe moe) {
        SceneVariableStore store = store(moe, this.scope);
        if (this.operation == Operation.FORGET) {
            store.locations().delete(this.name);
        } else {
            DimBlockPos position = this.source.position(moe);
            if (position != null && !position.isEmpty()) {
                store.locations().set(this.name, position);
            }
        }
    }

    private static SceneVariableStore store(Moe moe, SceneVariableScope scope) {
        SceneVariables variables = SceneVariables.get(moe.level());
        return switch (scope) {
            case NPC -> variables.npc(moe.getDatabaseID());
            case PLAYER -> variables.player(SceneActionPlayers.targetPlayerUuid(moe));
            case WORLD -> variables.world();
        };
    }

    public enum Operation {
        REMEMBER,
        FORGET
    }

    public enum Source {
        MOE, PLAYER, HOME, CURRENT_ANCHOR, REMEMBERED_PLACE;

        public static Source fromValue(String value) {
            try {
                return valueOf(value.toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return MOE;
            }
        }

        DimBlockPos position(Moe moe) {
            return switch (this) {
                case MOE -> new DimBlockPos(moe.level().dimension(), moe.blockPosition());
                case PLAYER -> {
                    var player = SceneActionPlayers.targetPlayer(moe);
                    yield player == null ? null : new DimBlockPos(player.level().dimension(), player.blockPosition());
                }
                case HOME -> moe.hasHome() ? moe.getHome() : null;
                case CURRENT_ANCHOR -> moe.currentRoutineAnchor().map(anchor -> anchor.dimPos()).orElse(null);
                case REMEMBERED_PLACE -> moe.rememberedPlace()
                        .map(place -> new DimBlockPos(moe.level().dimension(), place.pos())).orElse(null);
            };
        }
    }
}
