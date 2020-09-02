package moe.blocks.mod.util;

import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;

import java.util.Comparator;

public class SorterAffection implements Comparator<LivingEntity> {
    private final StudentEntity entity;

    public SorterAffection(StudentEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public int compare(LivingEntity one, LivingEntity two) {
        float d1 = this.entity.getRelationships().get(one).getAffection();
        float d2 = this.entity.getRelationships().get(two).getAffection();
        return Float.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
