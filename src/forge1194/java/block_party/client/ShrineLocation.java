package block_party.client;

import block_party.utils.sorters.BlockDistance;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public class ShrineLocation {
    private final List<BlockPos> positions;

    public ShrineLocation(List<BlockPos> positions) {
        this.positions = positions;
    }

    public ShrineLocation() {
        this(Lists.newArrayList());
    }

    public Optional<BlockPos> get(BlockPos pos) {
        return this.positions.stream().sorted((one, two) -> new BlockDistance(Minecraft.getInstance().player).compare(one, two)).findFirst();
    }
}
