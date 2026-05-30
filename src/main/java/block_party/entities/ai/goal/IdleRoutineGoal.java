package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class IdleRoutineGoal extends Goal {
    private final Moe moe;

    IdleRoutineGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.moe.routine().canUseGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.moe.routine().updateMovement();
    }
}
