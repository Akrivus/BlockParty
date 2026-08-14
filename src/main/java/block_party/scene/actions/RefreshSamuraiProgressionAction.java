package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.world.progression.SamuraiProgression;

public enum RefreshSamuraiProgressionAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        SamuraiProgression.refresh(SceneActionPlayers.targetPlayer(moe));
    }
}
