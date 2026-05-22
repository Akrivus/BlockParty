package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;

public enum EndAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        moe.clearDialogue();
    }
}
