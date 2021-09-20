package block_party.utils.sorters;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.Comparator;

public class BlockDistance implements Comparator<BlockPos> {
    private final Entity entity;

    public BlockDistance(Entity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public int compare(BlockPos one, BlockPos two) {
        double d1 = this.entity.distanceToSqr(one.getX(), one.getY(), one.getZ());
        double d2 = this.entity.distanceToSqr(two.getX(), two.getY(), two.getZ());
        return Double.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
