package block_party.entities.movement;

import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record PlayerMovementRequest(
        PlayerMovementIntent intent,
        UUID playerUuid,
        long npcId,
        ResourceKey<Level> dimension,
        Vec3 position,
        float yRot) {
    public static PlayerMovementRequest phoneCall(UUID playerUuid, long npcId, ResourceKey<Level> dimension, Vec3 position, float yRot) {
        return new PlayerMovementRequest(PlayerMovementIntent.PHONE_CALL, playerUuid, npcId, dimension, position, yRot);
    }

    public static PlayerMovementRequest partyInvite(UUID playerUuid, long npcId, ResourceKey<Level> dimension, Vec3 position, float yRot) {
        return new PlayerMovementRequest(PlayerMovementIntent.PARTY_INVITE, playerUuid, npcId, dimension, position, yRot);
    }
}
