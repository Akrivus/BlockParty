package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SetHomingDistanceGoal extends Goal implements IStateGoal {
    private final NPCEntity entity;
    private final int homingDistance;

    public SetHomingDistanceGoal(NPCEntity entity, int homingDistance) {
        this.entity = entity;
        this.homingDistance = homingDistance;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getMaximumHomeDistance() != this.homingDistance;
    }

    @Override
    public void startExecuting() {
        this.entity.setHomingDistance(this.homingDistance);
    }

    @Override
    public int getPriority() {
        return 0x0;
    }
}
