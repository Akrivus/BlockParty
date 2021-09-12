package block_party.mob.automata.state;

import block_party.mob.BlockPartyNPC;
import block_party.mob.automata.IState;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class GoalState extends Goal implements IState {
    protected boolean complete;
    protected BlockPartyNPC npc;
    private final int priority;
    private boolean done;

    public GoalState(int priority, Flag flag, Flag... flags) {
        this.setFlags(EnumSet.of(flag, flags));
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
    public boolean canUse() {
        return !this.complete;
    }

    @Override
    public void start() {
        this.complete = this.canCompleteOnFirstTry();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.complete && this.canContinue();
    }

    @Override
    public void tick() {
        this.complete = this.canCompleteOnOtherTry();
    }

    @Override
    public void stop() {
        this.onComplete();
        this.done = true;
    }

    @Override
    public void terminate(BlockPartyNPC npc) {
        npc.goalSelector.removeGoal(this);
    }

    @Override
    public void onTransfer(BlockPartyNPC npc) {
        (this.npc = npc).goalSelector.addGoal(this.priority, this);
    }

    @Override
    public boolean isDone(BlockPartyNPC npc) {
        return this.done;
    }
}
