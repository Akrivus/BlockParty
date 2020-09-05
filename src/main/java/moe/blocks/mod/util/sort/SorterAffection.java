package moe.blocks.mod.util.sort;

import moe.blocks.mod.entity.FiniteEntity;
import net.minecraft.entity.LivingEntity;

import java.util.Comparator;

public class SorterAffection implements Comparator<LivingEntity> {
    private final FiniteEntity entity;

    public SorterAffection(FiniteEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public int compare(LivingEntity one, LivingEntity two) {
        float d1 = this.entity.getDatingState().get(one).getAffection();
        float d2 = this.entity.getDatingState().get(two).getAffection();
        return Float.compare(d1, d2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
