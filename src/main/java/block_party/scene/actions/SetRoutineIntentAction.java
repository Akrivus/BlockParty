package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.entities.movement.RoutineIntent;
import block_party.scene.SceneAction;

public record SetRoutineIntentAction(RoutineIntent intent) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        moe.setRoutineIntent(this.intent);
    }
}
