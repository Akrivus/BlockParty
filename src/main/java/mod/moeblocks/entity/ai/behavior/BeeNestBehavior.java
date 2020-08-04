package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.ai.goal.special.AttackGoalBees;
import mod.moeblocks.entity.util.Behaviors;

public class BeeNestBehavior extends BasicBehavior {
    protected AttackGoalBees attackGoalBees;

    @Override
    public void start() {
        this.attackGoalBees = new AttackGoalBees(this.moe);
        this.moe.goalSelector.removeGoal(this.moe.attackGoalMelee);
        this.moe.goalSelector.removeGoal(this.moe.attackGoalRanged);
        this.moe.goalSelector.addGoal(4, this.attackGoalBees);
        this.moe.setCanFly(true);
    }

    @Override
    public void stop() {
        this.moe.goalSelector.removeGoal(this.attackGoalBees);
        this.moe.goalSelector.addGoal(4, this.moe.attackGoalMelee);
        this.moe.goalSelector.addGoal(4, this.moe.attackGoalRanged);
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BEE_NEST;
    }
}
