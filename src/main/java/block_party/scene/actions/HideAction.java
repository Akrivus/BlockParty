package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.entities.goals.HideUntil;
import block_party.scene.SceneAction;

public record HideAction(HideUntil until) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        moe.hide(this.until);
    }
}
