package moeblocks.util.sort;

import moeblocks.entity.ai.routines.Waypoint;
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

    public static class Waypoints implements Comparator<Waypoint> {
        private final Entity entity;

        public Waypoints(Entity entity) {
            super();
            this.entity = entity;
        }

        @Override
        public int compare(Waypoint one, Waypoint two) {
            return new BlockDistance(this.entity).compare(one.getPosition(), two.getPosition());
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
