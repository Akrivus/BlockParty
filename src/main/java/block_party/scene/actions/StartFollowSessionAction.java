package block_party.scene.actions;

import block_party.entities.Moe;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.scene.SceneAction;
import java.util.UUID;

public record StartFollowSessionAction(PlayerMovementIntent intent, int ticks, boolean canChangeDimension, boolean triggerScene) implements SceneAction {
    @Override
    public void apply(Moe moe) {
        UUID player = moe.getDialogueTarget().getMostSignificantBits() == 0L && moe.getDialogueTarget().getLeastSignificantBits() == 0L
                ? moe.getPlayerUUID()
                : moe.getDialogueTarget();
        moe.startFollowSession(player, this.intent, this.ticks, this.canChangeDimension, this.triggerScene);
    }
}
