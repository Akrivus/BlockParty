package moeblocks.mod.entity.ai.goal.engage;

import moeblocks.mod.entity.StateEntity;
import moeblocks.mod.entity.ai.goal.EngageGoal;

public class SocializeGoal extends EngageGoal<StateEntity> {
    public SocializeGoal(StateEntity entity) {
        super(entity, StateEntity.class);
    }

    @Override
    public boolean canShareWith(StateEntity entity) {
        return this.entity.isCompatible(entity);
    }

    @Override
    public void engage() {
        this.engaged = true;
    }

    @Override
    public int getEngagementInterval() {
        return 600 + this.entity.world.rand.nextInt(600);
    }

    @Override
    public int getEngagementTime() {
        return 20;
    }
}
