package block_party.db;

import net.minecraft.core.BlockPos;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public class ShrineLocations {
    private final List<BlockPos> positions;

    public ShrineLocations(List<BlockPos> positions) {
        this.positions = positions;
    }

    public ShrineLocations() {
        this(Lists.newArrayList());
    }

    public Optional<BlockPos> get(BlockPos pos) {
        return this.positions.stream()
                .min((one, two) -> Double.compare(one.distSqr(pos), two.distSqr(pos)));
    }
}
