package block_party.db;

import block_party.db.records.Shrine;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;

public final class ShrineLocations {
    private final List<Shrine> shrines;

    public ShrineLocations(List<Shrine> shrines) {
        this.shrines = List.copyOf(shrines);
    }

    public Optional<BlockPos> get(BlockPos pos) {
        return Shrine.closest(this.shrines, new DimBlockPos(net.minecraft.world.level.Level.OVERWORLD, pos))
                .map(shrine -> shrine.dimPos().getPos());
    }

    public Optional<Shrine> closest(DimBlockPos pos) {
        return Shrine.closest(this.shrines, pos);
    }

    public List<Shrine> entries() {
        return this.shrines;
    }
}
