package block_party.scene.actions;

import block_party.entities.Moe;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;

final class SceneActionPlayers {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private SceneActionPlayers() {
    }

    static Player targetPlayer(Moe moe) {
        UUID target = moe.getDialogueTarget();
        UUID playerUuid = EMPTY_UUID.equals(target) ? moe.getPlayerUUID() : target;
        for (Player player : moe.level().players()) {
            if (player.getUUID().equals(playerUuid)) {
                return player;
            }
        }
        return null;
    }
}
