package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;

public enum ClearFollowSessionAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        moe.clearFollowSession(true);
    }
}
