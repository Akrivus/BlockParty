package block_party.world;

import block_party.db.BlockPartyDB;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.entities.data.HidingSpots;
import block_party.entities.movement.RoutineIntent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Wakes nearby hidden corporeal Moes according to their relationship with an approaching player. */
public final class MoeWakeService {
    public static final int SCAN_INTERVAL_TICKS = 20;
    public static final int PLAYER_COOLDOWN_TICKS = 100;
    public static final int MOE_COOLDOWN_TICKS = 200;
    public static final double MAX_WAKE_RADIUS = 20.0D;
    private static final Map<UUID, Long> PLAYER_COOLDOWNS = new HashMap<>();
    private static final Map<Long, Long> MOE_COOLDOWNS = new HashMap<>();

    private MoeWakeService() {
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        long tick = event.getServer().getTickCount();
        for (var player : event.getServer().getPlayerList().getPlayers()) {
            if (Math.floorMod(tick + player.getUUID().hashCode(), SCAN_INTERVAL_TICKS) == 0) {
                tryWake(player.serverLevel(), player, tick);
            }
        }
        if (tick % 1200L == 0L) {
            PLAYER_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() <= tick);
            MOE_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() <= tick);
        }
    }

    public static Moe tryWake(ServerLevel level, Player player, long tick) {
        if (level == null || player == null || !player.isAlive() || player.isSpectator()
                || PLAYER_COOLDOWNS.getOrDefault(player.getUUID(), 0L) > tick) {
            return null;
        }
        BlockPartyDB db = BlockPartyDB.get(level);
        for (HidingSpots.Spot spot : HidingSpots.get(level).nearby(player.blockPosition(), MAX_WAKE_RADIUS)) {
            if (MOE_COOLDOWNS.getOrDefault(spot.databaseId(), 0L) > tick) {
                continue;
            }
            PlayerRelationship relationship = db.findPlayerRelationshipSafe(spot.databaseId(), player.getUUID()).orElse(null);
            double radius = wakeRadius(relationship);
            if (radius <= 0.0D || spot.pos().distSqr(player.blockPosition()) > radius * radius) {
                continue;
            }
            Moe moe = HidingSpots.reveal(level, spot.pos());
            if (moe == null) {
                continue;
            }
            moe.setRoutineIntent(RoutineIntent.IDLE);
            moe.setDialogueTarget(player.getUUID());
            PLAYER_COOLDOWNS.put(player.getUUID(), tick + PLAYER_COOLDOWN_TICKS);
            MOE_COOLDOWNS.put(spot.databaseId(), tick + MOE_COOLDOWN_TICKS);
            return moe;
        }
        return null;
    }

    public static double wakeRadius(PlayerRelationship relationship) {
        if (relationship == null)
            return 0.0D;
        float familiarity = Math.max(relationship.trust(), relationship.loyalty());
        float radius = familiarity / 5.0f;
        return Math.min(radius, MAX_WAKE_RADIUS);
    }

    public static void clearForTests() {
        PLAYER_COOLDOWNS.clear();
        MOE_COOLDOWNS.clear();
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        clearForTests();
    }
}
