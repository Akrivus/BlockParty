package moeblocks.automata;

import moeblocks.automata.state.enums.CupSize;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.automata.state.goal.AbstractStateTarget;
import net.minecraft.entity.ai.goal.GoalSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> implements IState<E> {
    protected final O filter;
    protected final BiConsumer<E, List<AbstractStateGoal>> generator;
    protected final List<AbstractStateGoal> goals = new ArrayList<>();
    protected final boolean isMortal;
    protected int lifespan;

    public GoalState(O filter, BiConsumer<E, List<AbstractStateGoal>> generator, int lifespan) {
        this.filter = filter;
        this.generator = generator;
        this.lifespan = lifespan;
        this.isMortal = this.lifespan > 0;
    }

    public GoalState(O filter, BiConsumer<E, List<AbstractStateGoal>> generator) {
        this(filter, generator, 100);
    }
    
    @Override
    public void apply(E applicant) {
        this.generator.accept(applicant, this.goals);
        this.goals.forEach(goal -> this.getSelector(applicant, goal).addGoal(goal.getPriority(), goal));
    }

    @Override
    public boolean canClear(E applicant) {
        return --this.lifespan < 0 && this.isMortal;
    }

    @Override
    public void clear(E applicant) {
        this.goals.forEach(goal -> this.getSelector(applicant, goal).removeGoal(goal));
    }
    
    private GoalSelector getSelector(E applicant, AbstractStateGoal goal) {
        if (goal instanceof AbstractStateTarget) { return applicant.targetSelector; }
        return applicant.goalSelector;
    }
}
