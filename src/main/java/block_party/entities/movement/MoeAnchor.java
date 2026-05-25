package block_party.entities.movement;

import block_party.db.DimBlockPos;
import java.util.UUID;

public record MoeAnchor(
        MoeAnchorType type,
        long databaseId,
        DimBlockPos dimPos,
        UUID playerUuid,
        int priority) {
}
