package moeblocks.mod.entity.ai.goal;

import moeblocks.mod.client.Animations;
import moeblocks.mod.entity.StateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class AttackGoals {
    public static class Melee extends Goal {
        private final StateEntity entity;
        private int timeUntilAttacking;

        public Melee(StateEntity entity) {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public boolean shouldExecute() {
            return this.entity.isMeleeFighter() && this.entity.canBeTarget(this.entity.getAttackTarget());
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute() || !this.entity.hasPath();
        }

        @Override
        public void startExecuting() {
            LivingEntity victim = this.entity.getAttackTarget();
            if (this.entity.canBeTarget(victim)) {
                this.entity.getNavigator().tryMoveToEntityLiving(victim, this.entity.getFollowSpeed(victim, 32.0F));
                this.timeUntilAttacking = this.entity.getAttackCooldown();
            }
        }

        @Override
        public void resetTask() {
            this.entity.getNavigator().clearPath();
        }

        @Override
        public void tick() {
            LivingEntity victim = this.entity.getAttackTarget();
            if (this.entity.canSee(victim) && this.entity.getDistance(victim) < 3.0F) {
                if (--this.timeUntilAttacking < 0) {
                    this.entity.attackEntityAsMob(victim);
                    this.entity.swingArm(Hand.MAIN_HAND);
                    this.startExecuting();
                }
            } else {
                this.startExecuting();
            }
        }
    }

    public static class Ranged extends Goal {
        private final StateEntity entity;
        private int timeUntilAttacking;
        private int timeUntilSeen;

        public Ranged(StateEntity entity) {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public boolean shouldExecute() {
            return this.entity.isRangedFighter() && this.entity.canBeTarget(this.entity.getAttackTarget());
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute() && this.timeUntilAttacking >= 0;
        }

        @Override
        public void resetTask() {
            this.entity.getNavigator().clearPath();
            this.entity.resetAnimationState();
        }

        @Override
        public void startExecuting() {
            LivingEntity victim = this.entity.getAttackTarget();
            if (this.entity.canBeTarget(victim)) {
                this.entity.getNavigator().tryMoveToEntityLiving(victim, this.entity.getFollowSpeed(victim, 256.0F) - 1.0F);
                this.entity.resetAnimationState();
                this.timeUntilAttacking = this.entity.getAttackCooldown();
                this.timeUntilSeen = 5;
            }
        }

        @Override
        public void tick() {
            LivingEntity victim = this.entity.getAttackTarget();
            this.entity.setAnimation(Animations.AIM);
            if (this.entity.canSee(victim)) {
                if (--this.timeUntilSeen < 0 && --this.timeUntilAttacking < 0) {
                    this.entity.attackEntityFromRange(victim, 0.0F);
                }
            } else {
                this.startExecuting();
            }
        }
    }
}
