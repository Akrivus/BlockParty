package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.scene.SceneTimeMarkers;
import block_party.scene.SceneVariableScope;

public record MarkTimeAction(String name, SceneVariableScope scope) implements SceneAction {
    public MarkTimeAction {
        scope = scope == null ? SceneVariableScope.NPC : scope;
    }

    @Override
    public void apply(Moe moe) {
        SceneTimeMarkers.mark(moe, this.name, this.scope);
    }
}
