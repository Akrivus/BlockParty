package block_party.db.records;

import block_party.db.DimBlockPos;
import java.util.UUID;

public record Garden(long databaseId, DimBlockPos dimPos, UUID playerUuid) implements DataBlockRow {
}
