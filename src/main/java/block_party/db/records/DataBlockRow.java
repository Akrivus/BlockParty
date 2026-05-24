package block_party.db.records;

import block_party.db.DimBlockPos;
import java.util.UUID;

public interface DataBlockRow {
    long databaseId();

    DimBlockPos dimPos();

    UUID playerUuid();
}
