package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.entities.movement.MoeAnchor;
import block_party.scene.SceneAction;

import java.util.Optional;

public enum SetHomeToAnchorAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        Optional<MoeAnchor> anchor = moe.currentRoutineAnchor();
        if (anchor.isEmpty()) {
            return;
        }
        moe.setHasHome(true);
        moe.setHome(anchor.get().dimPos());
    }
}
