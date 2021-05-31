package moeblocks.automata.state;

import moeblocks.automata.IState;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class GoalState extends Goal implements IState {
    protected boolean complete;
    protected AbstractNPCEntity npc;
    private final int priority;
    private boolean done;

    public GoalState(int priority, Flag flag, Flag... flags) {
        this.setMutexFlags(EnumSet.of(flag, flags));
        this.priority = priority;
    }

    protected abstract void onComplete();

    protected abstract boolean canCompleteOnFirstTry();

    protected boolean canCompleteOnOtherTry() {
        return this.canCompleteOnFirstTry();
    }

    protected boolean canContinue() {
        return true;
    }

    @Override
    public boolean shouldExecute() {
        return !this.complete;
    }

    @Override
    public void startExecuting() {
        this.complete = this.canCompleteOnFirstTry();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.complete && this.canContinue();
    }

    @Override
    public void tick() {
        this.complete = this.canCompleteOnOtherTry();
    }

    @Override
    public void resetTask() {
        this.onComplete();
        this.done = true;
    }

    @Override
    public void terminate(AbstractNPCEntity npc) {
        npc.goalSelector.removeGoal(this);
    }

    @Override
    public void onTransfer(AbstractNPCEntity npc) {
        (this.npc = npc).goalSelector.addGoal(this.priority, this);
    }

    @Override
    public boolean isDone(AbstractNPCEntity npc) {
        return this.done;
    }
}
