package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.scene.SceneAction;
import net.minecraft.world.entity.player.Player;

public enum OpenInventoryAction implements SceneAction {
    INSTANCE;

    @Override
    public void apply(Moe moe) {
        Player player = SceneActionPlayers.targetPlayer(moe);
        if (player != null) {
            moe.clearDialogue();
            moe.openChestFor(player);
        }
    }
}
