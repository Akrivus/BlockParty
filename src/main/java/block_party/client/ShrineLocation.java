package block_party.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;

public final class ShrineLocation {
    private static List<BlockPos> positions = List.of();

    private ShrineLocation() {
    }

    public static void update(List<BlockPos> shrines) {
        positions = List.copyOf(shrines);
    }

    public static Optional<BlockPos> get(BlockPos pos) {
        return positions.stream().min(Comparator.comparingDouble(shrine -> shrine.distSqr(pos)));
    }

    public static List<BlockPos> positions() {
        return new ArrayList<>(positions);
    }
}
