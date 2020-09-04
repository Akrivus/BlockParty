package moe.blocks.mod.entity.ai.goal.engage;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.entity.ai.goal.EngageGoal;

public class SocializeGoal extends EngageGoal<StudentEntity> {
    public SocializeGoal(StudentEntity entity) {
        super(entity, StudentEntity.class);
    }

    @Override
    public boolean canMoveTo(StudentEntity entity) {
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
