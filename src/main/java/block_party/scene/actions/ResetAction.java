package block_party.scene.actions;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;

public class ResetAction implements ISceneAction {
    @Override
    public void apply(BlockPartyNPC npc) {
        return;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
