package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.goal.target.AbstractStateTarget;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> implements IState<E> {
    protected final O filter;
    protected final BiConsumer<E, List<IStateGoal>> generator;
    private final List<IStateGoal> goals = new ArrayList<>();
    
    public GoalState(O filter, BiConsumer<E, List<IStateGoal>> generator) {
        this.filter = filter;
        this.generator = generator;
    }
    
    @Override
    public void apply(E applicant) {
        this.generator.accept(applicant, this.goals);
        this.goals.forEach(goal -> this.getSelector(applicant, goal).addGoal(goal.getPriority(), (Goal) goal));
    }
    
    @Override
    public boolean canApply(E applicant) {
        return applicant.getState(this.filter.getClass()) != this.filter;
    }
    
    @Override
    public boolean canClear(E applicant) {
        return !this.canApply(applicant);
    }
    
    @Override
    public void clear(E applicant) {
        this.goals.forEach(goal -> this.getSelector(applicant, goal).removeGoal((Goal) goal));
    }
    
    private GoalSelector getSelector(E applicant, IStateGoal goal) {
        if (goal instanceof AbstractStateTarget) { return applicant.targetSelector; }
        return applicant.goalSelector;
    }
}
