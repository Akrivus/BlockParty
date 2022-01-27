package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;

public class EndAction implements ISceneAction {
    @Override
    public void apply(BlockPartyNPC npc) {
        return;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
