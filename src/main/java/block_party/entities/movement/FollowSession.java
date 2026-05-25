package block_party.entities.movement;

import java.util.UUID;

public record FollowSession(
        UUID playerUuid,
        PlayerMovementIntent intent,
        int ticksRemaining,
        boolean canChangeDimension) {
    public FollowSession {
        playerUuid = playerUuid == null ? new UUID(0L, 0L) : playerUuid;
        intent = intent == null ? PlayerMovementIntent.FOLLOW_REQUEST : intent;
        ticksRemaining = Math.max(0, ticksRemaining);
    }

    public boolean active() {
        return this.ticksRemaining > 0
                && (this.playerUuid.getMostSignificantBits() != 0L || this.playerUuid.getLeastSignificantBits() != 0L);
    }

    public FollowSession tick() {
        if (this.ticksRemaining <= 1) {
            return none();
        }
        return new FollowSession(this.playerUuid, this.intent, this.ticksRemaining - 1, this.canChangeDimension);
    }

    public static FollowSession none() {
        return new FollowSession(new UUID(0L, 0L), PlayerMovementIntent.FOLLOW_REQUEST, 0, false);
    }
}
