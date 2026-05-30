package block_party.entities.ai.goal;

import block_party.entities.Moe;
import net.minecraft.world.entity.ai.goal.GoalSelector;

public final class MoeGoals {
    private MoeGoals() {
    }

    public static void register(Moe moe, GoalSelector goals) {
        goals.addGoal(0, new ActiveChoreGoal(moe));
        goals.addGoal(1, new FollowSessionGoal(moe));
        goals.addGoal(2, new EnvironmentalMovementGoal(moe));
        goals.addGoal(3, new SocialReactionGoal(moe));
        goals.addGoal(4, new IdleRoutineGoal(moe));
    }

    public static void updateActionState(Moe moe) {
        if (moe.level().isClientSide || moe.hasDialogue()) {
            return;
        }
        if (moe.updateFollowSessionMovement()) {
            moe.social().clearMovementIntent();
            moe.environment().clearMovementIntent();
            return;
        }
        if (moe.environment().updateRoutineMovement()) {
            moe.social().clearMovementIntent();
            return;
        }
        if (moe.social().hasTickDelay()) {
            moe.social().decrementTickDelay();
            if (!moe.social().updateMovementIntent()) {
                moe.environment().updateMovementIntent();
            }
            return;
        }
        moe.social().resetTickDelay();
        if (!moe.social().updateState()) {
            moe.routine().updateMovement();
        } else {
            moe.environment().clearMovementIntent();
        }
    }
}
