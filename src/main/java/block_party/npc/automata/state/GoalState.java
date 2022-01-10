package block_party.npc.automata.state;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class GoalState extends Goal implements ISceneAction {
    private final int priority;
    protected boolean complete;
    protected BlockPartyNPC npc;
    private boolean done;

    public GoalState(int priority, Flag flag, Flag... flags) {
        this.setFlags(EnumSet.of(flag, flags));
        this.priority = priority;
    }

    @Override
    public boolean canUse() {
        return !this.complete;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.complete && this.canContinue();
    }

    protected boolean canContinue() {
        return true;
    }

    @Override
    public void start() {
        this.complete = this.canCompleteOnFirstTry();
    }

    protected abstract boolean canCompleteOnFirstTry();

    @Override
    public void stop() {
        this.onComplete();
        this.done = true;
    }

    @Override
    public void tick() {
        this.complete = this.canCompleteOnOtherTry();
    }

    protected boolean canCompleteOnOtherTry() {
        return this.canCompleteOnFirstTry();
    }

    @Override
    public void onComplete() {
        this.npc.goalSelector.removeGoal(this);
    }

    @Override
    public void apply(BlockPartyNPC npc) {
        (this.npc = npc).goalSelector.addGoal(this.priority, this);
    }

    @Override
    public boolean isComplete() {
        return this.done;
    }
}
