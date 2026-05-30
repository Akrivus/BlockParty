package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class FollowSessionGoal extends Goal {
    private final Moe moe;

    FollowSessionGoal(Moe moe) {
        this.moe = moe;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.moe.level().isClientSide && this.moe.isFollowing() && !this.moe.hasDialogue();
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
    public void start() {
        this.moe.social().clearMovementIntent();
        this.moe.environment().clearMovementIntent();
    }

    @Override
    public void tick() {
        this.moe.updateFollowSessionMovement();
    }
}
