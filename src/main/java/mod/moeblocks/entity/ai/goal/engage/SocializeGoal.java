package mod.moeblocks.entity.ai.goal.engage;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.goal.EngageGoal;
import mod.moeblocks.entity.util.Emotions;

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
        System.out.println("OWO");
    }

    @Override
    public int getEngagementInterval() {
        return 3000 + this.entity.world.rand.nextInt(3000);
    }

    @Override
    public int getEngagementTime() {
        return 20;
    }

    @Override
    public int getResetTime() {
        return 100;
    }
}
