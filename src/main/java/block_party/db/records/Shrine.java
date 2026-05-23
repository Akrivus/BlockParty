package block_party.db.records;

import block_party.db.DimBlockPos;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record Shrine(long databaseId, DimBlockPos dimPos, UUID playerUuid) implements DataBlockRow {
    public static Optional<Shrine> closest(List<Shrine> shrines, DimBlockPos origin) {
        return shrines.stream().min(Comparator.comparingDouble(shrine -> shrine.dimPos().getPos().distSqr(origin.getPos())));
    }
}
