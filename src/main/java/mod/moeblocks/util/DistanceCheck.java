package mod.moeblocks.util;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.Entity;

import java.util.Comparator;

public class DistanceCheck implements Comparator<Entity> {
    private final MoeEntity moe;

    public DistanceCheck(MoeEntity moe) {
        super();
        this.moe = moe;
    }

    @Override
    public int compare(Entity one, Entity two) {
        float d1 = this.moe.getDistance(one);
        float d2 = this.moe.getDistance(two);
        return Float.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
