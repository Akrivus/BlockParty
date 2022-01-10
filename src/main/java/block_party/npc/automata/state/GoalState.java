package block_party.npc.automata.state;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public abstract class GoalState extends Goal implements ISceneAction {
    protected boolean complete;
    protected BlockPartyNPC npc;
    private final int priority;
    private boolean done;

    public GoalState(int priority, Flag flag, Flag... flags) {
        this.setFlags(EnumSet.of(flag, flags));
        this.priority = priority;
    }

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
    public void apply(BlockPartyNPC npc) {
        (this.npc = npc).goalSelector.addGoal(this.priority, this);
    }

    @Override
    public void onComplete() {
        this.npc.goalSelector.removeGoal(this);
    }

    @Override
    public boolean isComplete() {
        return this.done;
    }
}
