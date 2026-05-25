package block_party.entities.movement;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.Garden;
import block_party.db.records.Location;
import block_party.db.records.Sapling;
import block_party.db.records.Shrine;
import block_party.entities.Moe;
import block_party.scene.SceneObservations;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class MoeAnchorResolver {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private MoeAnchorResolver() {
    }

    public static List<MoeAnchor> activeAnchors(Moe moe) {
        List<MoeAnchor> anchors = new ArrayList<>();
        if (moe.hasHome() && !moe.getHome().isEmpty()) {
            anchors.add(new MoeAnchor(MoeAnchorType.HOME, moe.getDatabaseID(), moe.getHome(), moe.getPlayerUUID(), 10));
        }
        if (!(moe.level() instanceof net.minecraft.server.level.ServerLevel level)) {
            return List.copyOf(anchors);
        }
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID player = moe.getPlayerUUID();
        try {
            for (Location location : db.listLocations(player)) {
                if (isUsableBy(location.playerUuid(), player) && conditionMatches(moe, location.requiredCondition())) {
                    anchors.add(new MoeAnchor(MoeAnchorType.LOCATION, location.databaseId(), location.dimPos(), location.playerUuid(), 100 + location.priority()));
                }
            }
            for (Garden garden : db.listGardens()) {
                if (isUsableBy(garden.playerUuid(), player)) {
                    anchors.add(new MoeAnchor(MoeAnchorType.GARDEN, garden.databaseId(), garden.dimPos(), garden.playerUuid(), 70));
                }
            }
            for (Sapling sapling : db.listSaplings()) {
                if (isUsableBy(sapling.playerUuid(), player)) {
                    anchors.add(new MoeAnchor(MoeAnchorType.SAPLING, sapling.databaseId(), sapling.dimPos(), sapling.playerUuid(), 50));
                }
            }
            for (Shrine shrine : db.listShrineRows(player)) {
                if (isUsableBy(shrine.playerUuid(), player)) {
                    anchors.add(new MoeAnchor(MoeAnchorType.SHRINE, shrine.databaseId(), shrine.dimPos(), shrine.playerUuid(), 40));
                }
            }
        } catch (RuntimeException | SQLException ignored) {
        }
        return List.copyOf(anchors);
    }

    public static Optional<MoeAnchor> bestRoutineAnchor(Moe moe) {
        return bestRoutineAnchor(moe, moe.getEffectiveRoutineIntent());
    }

    public static Optional<MoeAnchor> bestRoutineAnchor(Moe moe, RoutineIntent intent) {
        DimBlockPos origin = moe.getDimBlockPos();
        return activeAnchors(moe).stream()
                .filter(anchor -> anchor.dimPos().getDim() == origin.getDim())
                .max(Comparator
                        .comparingInt((MoeAnchor anchor) -> weightedPriority(anchor, intent))
                        .thenComparingDouble(anchor -> -anchor.dimPos().getPos().distSqr(origin.getPos())));
    }

    public static Optional<MoeAnchor> nearbyRoutineAnchor(Moe moe, double radius) {
        return nearbyRoutineAnchor(moe, radius, moe.getEffectiveRoutineIntent());
    }

    public static Optional<MoeAnchor> nearbyRoutineAnchor(Moe moe, double radius, RoutineIntent intent) {
        DimBlockPos origin = moe.getDimBlockPos();
        double radiusSqr = radius * radius;
        return activeAnchors(moe).stream()
                .filter(anchor -> anchor.dimPos().getDim() == origin.getDim())
                .filter(anchor -> anchor.dimPos().getPos().distSqr(origin.getPos()) <= radiusSqr)
                .max(Comparator
                        .comparingInt((MoeAnchor anchor) -> weightedPriority(anchor, intent))
                        .thenComparingDouble(anchor -> -anchor.dimPos().getPos().distSqr(origin.getPos())));
    }

    private static int weightedPriority(MoeAnchor anchor, RoutineIntent intent) {
        return anchor.priority() + switch (intent == null ? RoutineIntent.IDLE : intent) {
            case RELAX -> anchor.type() == MoeAnchorType.GARDEN ? 120 : 0;
            case REST, SLEEP -> anchor.type() == MoeAnchorType.HOME ? 160 : 0;
            case GATHER -> anchor.type() == MoeAnchorType.SAPLING ? 120 : 0;
            case VISIT -> anchor.type() == MoeAnchorType.LOCATION ? 120 : 0;
            case WORSHIP -> anchor.type() == MoeAnchorType.SHRINE ? 120 : 0;
            case CHORE -> switch (anchor.type()) {
                case GARDEN, LOCATION, SAPLING -> 60;
                case HOME, SHRINE -> 0;
            };
            case IDLE -> 0;
        };
    }

    private static boolean isUsableBy(UUID owner, UUID player) {
        return owner == null || owner.equals(EMPTY_UUID) || owner.equals(player);
    }

    private static boolean conditionMatches(Moe moe, String condition) {
        String key = condition == null || condition.isBlank() ? "always" : condition;
        int separator = key.indexOf(':');
        if (separator >= 0) {
            key = key.substring(separator + 1);
        }
        key = key.toLowerCase(Locale.ROOT);
        return SceneObservations.byPath(key)
                .map(observation -> observation.verify(moe))
                .orElse(false);
    }
}
