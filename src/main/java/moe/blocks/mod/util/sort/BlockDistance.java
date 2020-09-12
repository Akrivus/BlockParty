package moe.blocks.mod.util.sort;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class BlockDistance implements Comparator<BlockPos> {
    private final Entity entity;

    public BlockDistance(Entity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public int compare(BlockPos one, BlockPos two) {
        double d1 = this.entity.getDistanceSq(one.getX(), one.getY(), one.getZ());
        double d2 = this.entity.getDistanceSq(two.getX(), two.getY(), two.getZ());
        return Double.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
