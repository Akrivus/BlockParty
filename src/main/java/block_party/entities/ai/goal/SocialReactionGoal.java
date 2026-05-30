package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class SocialReactionGoal extends Goal {
    private final Moe moe;

    SocialReactionGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.moe.social().canUseGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.moe.social().canContinueGoal();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.moe.social().tickGoal();
    }

    @Override
    public void stop() {
        this.moe.social().stopGoal();
    }
}
