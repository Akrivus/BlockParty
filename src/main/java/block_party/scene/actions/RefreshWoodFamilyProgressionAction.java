package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import block_party.world.progression.WoodFamilyProgression;
import net.minecraft.server.level.ServerLevel;

public enum RefreshWoodFamilyProgressionAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        if (moe.level() instanceof ServerLevel level) {
            WoodFamilyProgression.refresh(level, SceneActionPlayers.targetPlayerUuid(moe));
        }
    }
}
