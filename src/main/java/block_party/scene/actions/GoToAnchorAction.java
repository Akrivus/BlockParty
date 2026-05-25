package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;

public record GoToAnchorAction(double speed) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        moe.moveTowardCurrentRoutineAnchor(Math.max(0.0D, this.speed));
    }
}
