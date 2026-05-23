package block_party.db.records;

import block_party.db.DimBlockPos;
import java.util.UUID;

public record Location(
        long databaseId,
        DimBlockPos dimPos,
        UUID playerUuid,
        String requiredCondition,
        int priority) implements DataBlockRow {
}
