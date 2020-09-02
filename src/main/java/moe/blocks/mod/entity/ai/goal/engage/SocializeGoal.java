package moe.blocks.mod.entity.ai.goal.engage;

import moe.blocks.mod.entity.ai.goal.EngageGoal;
import moe.blocks.mod.entity.StudentEntity;

public class SocializeGoal extends EngageGoal<StudentEntity> {
    public SocializeGoal(StudentEntity entity) {
        super(entity, StudentEntity.class);
    }

    @Override
    public boolean canShareWith(StudentEntity entity) {
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
