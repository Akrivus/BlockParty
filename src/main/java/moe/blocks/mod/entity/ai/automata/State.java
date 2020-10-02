package moe.blocks.mod.entity.ai.automata;

import moe.blocks.mod.entity.ai.goal.target.AbstractStateTarget;
import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;

import java.util.ArrayList;
import java.util.List;

public abstract class State<E extends AbstractNPCEntity> {
    private final List<IStateGoal> goals;

    public State() {
        this.goals = new ArrayList<>();
    }

    public State start(E entity) {
        entity.world.getProfiler().startSection("stateStart");
        this.apply(this.goals, entity);
        this.goals.forEach(goal -> {
            GoalSelector selector = goal instanceof AbstractStateTarget ? entity.targetSelector : entity.goalSelector;
            selector.addGoal(goal.getPriority(), (Goal) goal);
        });
        entity.world.getProfiler().endSection();
        return this;
    }

    public abstract void apply(List<IStateGoal> goals, E entity);

    public State clean(E entity) {
        entity.world.getProfiler().startSection("stateClean");
        this.reset(entity);
        this.goals.forEach(goal -> {
            GoalSelector selector = goal instanceof AbstractStateTarget ? entity.targetSelector : entity.goalSelector;
            selector.removeGoal((Goal) goal);
        });
        entity.world.getProfiler().endSection();
        return this;
    }

    public void reset(E entity) {

    }
}
