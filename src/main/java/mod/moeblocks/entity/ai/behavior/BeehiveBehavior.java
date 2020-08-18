package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.ai.goal.special.AttackGoalBees;
import mod.moeblocks.entity.util.Behaviors;

public class BeehiveBehavior extends BasicBehavior {
    protected AttackGoalBees attackGoalBees;

    @Override
    public void start() {
        this.attackGoalBees = new AttackGoalBees(this.moe);
        this.moe.goalSelector.addGoal(2, this.attackGoalBees);
        this.moe.goalSelector.removeGoal(this.moe.attackGoal);
    }

    @Override
    public void stop() {
        this.moe.goalSelector.removeGoal(this.attackGoalBees);
        this.moe.goalSelector.addGoal(2, this.moe.attackGoal);
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BEEHIVE;
    }
}
