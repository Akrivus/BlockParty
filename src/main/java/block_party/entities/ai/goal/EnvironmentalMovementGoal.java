package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class EnvironmentalMovementGoal extends Goal {
    private final Moe moe;

    EnvironmentalMovementGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.moe.environment().canUseGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.moe.environment().canContinueGoal();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.moe.environment().tickGoal();
    }

    @Override
    public void stop() {
        this.moe.environment().stopGoal();
    }
}
