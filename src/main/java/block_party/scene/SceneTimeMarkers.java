package block_party.scene;

import block_party.entities.Moe;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;

public final class SceneTimeMarkers {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);
    private static final String PREFIX = "__time_marker:";

    private SceneTimeMarkers() {
    }

    public static void mark(Moe moe, String name, SceneVariableScope scope) {
        if (!(moe.level() instanceof ServerLevel level) || name == null || name.isBlank()) {
            return;
        }
        store(moe, scope).cookies().set(cookieName(name), level.getGameTime() + ":" + System.currentTimeMillis());
    }

    public static boolean elapsed(Moe moe, String name, SceneVariableScope scope, long minGameTicks, long minRealMillis) {
        if (!(moe.level() instanceof ServerLevel level) || name == null || name.isBlank()) {
            return false;
        }
        Marker marker = read(store(moe, scope).cookies().get(cookieName(name)));
        if (marker == null) {
            return false;
        }
        long elapsedGameTicks = Math.max(0L, level.getGameTime() - marker.gameTime);
        long elapsedRealMillis = Math.max(0L, System.currentTimeMillis() - marker.realMillis);
        return elapsedGameTicks >= Math.max(0L, minGameTicks)
                && elapsedRealMillis >= Math.max(0L, minRealMillis);
    }

    public static String cookieName(String name) {
        return PREFIX + name;
    }

    private static Marker read(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String[] parts = value.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        try {
            return new Marker(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static SceneVariableStore store(Moe moe, SceneVariableScope scope) {
        SceneVariables variables = SceneVariables.get(moe.level());
        return switch (scope == null ? SceneVariableScope.NPC : scope) {
            case NPC -> variables.npc(moe.getDatabaseID());
            case PLAYER -> variables.player(targetPlayerUuid(moe));
            case WORLD -> variables.world();
        };
    }

    private static UUID targetPlayerUuid(Moe moe) {
        UUID target = moe.getDialogueTarget();
        return EMPTY_UUID.equals(target) ? moe.getPlayerUUID() : target;
    }

    private record Marker(long gameTime, long realMillis) {
    }
}
