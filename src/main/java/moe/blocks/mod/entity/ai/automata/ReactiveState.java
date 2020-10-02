package moe.blocks.mod.entity.ai.automata;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.ai.goal.ReactiveGoal;

import java.util.List;

public abstract class ReactiveState extends State<AbstractNPCEntity> {
    @Override
    public void apply(List<IStateGoal> goals, AbstractNPCEntity entity) {
        ReactiveGoal goal = this.getGoal(entity);
        if (goal != null) { goals.add(goal); }
    }

    public abstract ReactiveGoal getGoal(AbstractNPCEntity entity);
}
