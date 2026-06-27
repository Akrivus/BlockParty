package block_party.world.progression;

import block_party.scene.SceneVariables;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;

public final class WoodFamilyProgression {
    public static final String OAK_REPLENISHMENT_SEEN = "oak_replenishment_seen";
    public static final String BIRCH_REPLENISHMENT_SEEN = "birch_replenishment_seen";
    public static final String SPRUCE_WASTE_AVOIDED = "spruce_waste_avoided";
    public static final String ACACIA_CLEAN_USE_SEEN = "acacia_clean_use_seen";
    public static final String JUNGLE_REPLENISHMENT_SEEN = "jungle_replenishment_seen";
    public static final String DARK_OAK_REPLENISHMENT_SEEN = "dark_oak_replenishment_seen";
    public static final String OAK_BEFRIENDED = "oak_befriended";
    public static final String BIRCH_BEFRIENDED = "birch_befriended";
    public static final String SPRUCE_BEFRIENDED = "spruce_befriended";
    public static final String ACACIA_BEFRIENDED = "acacia_befriended";
    public static final String JUNGLE_BEFRIENDED = "jungle_befriended";
    public static final String DARK_OAK_BEFRIENDED = "dark_oak_befriended";
    public static final String WOOD_FAMILY_ARC_READY = "wood_family_arc_ready";

    private WoodFamilyProgression() {
    }

    public static boolean isReady(ServerLevel level, UUID player) {
        if (player == null) {
            return false;
        }
        var cookies = SceneVariables.get(level).playerCookies(player);
        return cookies.has(WOOD_FAMILY_ARC_READY)
                || cookies.has(OAK_BEFRIENDED)
                && cookies.has(BIRCH_BEFRIENDED)
                && cookies.has(DARK_OAK_BEFRIENDED)
                && (cookies.has(SPRUCE_BEFRIENDED)
                || cookies.has(ACACIA_BEFRIENDED)
                || cookies.has(JUNGLE_BEFRIENDED));
    }

    public static void recordReplenishment(ServerLevel level, UUID player, String cookie) {
        if (player == null || cookie == null || cookie.isBlank()) {
            return;
        }
        var cookies = SceneVariables.get(level).playerCookies(player);
        cookies.set(cookie, "true");
        refresh(level, player);
    }

    public static void refresh(ServerLevel level, UUID player) {
        if (player == null) {
            return;
        }
        var cookies = SceneVariables.get(level).playerCookies(player);
        if (isReady(level, player)) {
            cookies.set(WOOD_FAMILY_ARC_READY, "true");
        }
    }
}
