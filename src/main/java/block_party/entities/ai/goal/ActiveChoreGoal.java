package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class ActiveChoreGoal extends Goal {
    private final Moe moe;

    ActiveChoreGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.moe.chores().canRunActive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.moe.chores().canRunActive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.moe.social().clearMovementIntent();
        this.moe.environment().clearMovementIntent();
    }

    @Override
    public void tick() {
        this.moe.chores().tickActive();
    }
}
