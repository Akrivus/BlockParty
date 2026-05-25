package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.entities.movement.RoutineIntent;
import block_party.scene.SceneAction;

public enum ClearRoutineIntentAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        moe.setRoutineIntent(RoutineIntent.IDLE);
    }
}
