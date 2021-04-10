package moeblocks.util.sort;

import net.minecraft.entity.Entity;

import java.util.Comparator;

public class EntityDistance implements Comparator<Entity> {
    private final Entity entity;

    public EntityDistance(Entity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public int compare(Entity one, Entity two) {
        float d1 = this.entity.getDistance(one);
        float d2 = this.entity.getDistance(two);
        return Float.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
