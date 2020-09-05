package moe.blocks.mod.entity.goal.engage;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.goal.EngageGoal;

public class SocializeGoal extends EngageGoal<FiniteEntity> {
    public SocializeGoal(FiniteEntity entity) {
        super(entity, FiniteEntity.class);
    }

    @Override
    public boolean canMoveTo(FiniteEntity entity) {
        return this.entity.isCompatible(entity);
    }

    @Override
    public void engage() {
        this.engaging = false;
    }

    @Override
    public int getEngagementInterval() {
        return 600 + this.entity.world.rand.nextInt(600);
    }

    @Override
    public int getResetDelay() {
        return 20;
    }
}
