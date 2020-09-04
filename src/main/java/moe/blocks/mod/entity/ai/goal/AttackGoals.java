package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AttackGoals {
    public static class Melee extends FollowGoal {
        private int timeUntilAttack;

        public Melee(StudentEntity entity) {
            super(entity);
        }

        @Override
        public boolean shouldExecute() {
            this.target = this.entity.getAttackTarget();
            if (this.entity.isMeleeFighter() && this.entity.canBeTarget(this.target) && this.canMoveTo(this.target)) {
                this.path = this.entity.getNavigator().getPathToEntity(this.target, 0);
                return this.path != null;
            }
            return false;
        }

        @Override
        public void startExecuting() {
            this.timeUntilAttack = this.entity.getAttackCooldown();
            super.startExecuting();
        }

        @Override
        public void onFollowed() {
            this.timeUntilReset = 100;
            if (--this.timeUntilAttack < 0) {
                this.entity.attackEntityAsMob(this.target);
                this.entity.swingArm(Hand.MAIN_HAND);
                if (this.shouldExecute()) {
                    this.startExecuting();
                }
            }
        }

        @Override
        public boolean canMoveTo(LivingEntity target) {
            return this.entity.canSee(target);
        }

        @Override
        public float getDistanceThreshhold() {
            return 4.0F;
        }
    }

    public static class Ranged extends MoveGoal<LivingEntity> {
        private int timeUntilFire;
        private int timeUntilSeen;

        public Ranged(StudentEntity entity) {
            super(entity, LivingEntity.class, 0.05F);
        }

        @Override
        public boolean shouldExecute() {
            this.target = this.entity.getAttackTarget();
            if (this.entity.isRangedFighter() && this.entity.canBeTarget(this.target) && this.canMoveTo(this.target)) {
                this.path = this.entity.getNavigator().getPathToEntity(this.target, 0);
                return this.path != null;
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.timeUntilFire >= 0;
        }

        @Override
        public void resetTask() {
            this.entity.resetAnimationState();
            super.resetTask();
        }

        @Override
        public void startExecuting() {
            this.entity.resetAnimationState();
            this.timeUntilFire = this.entity.getAttackCooldown();
            this.timeUntilSeen = 5;
            super.startExecuting();
        }

        @Override
        public void onFollowed() {
            this.entity.setAnimation(Animations.AIM);
            this.timeUntilReset = 100;
            if (--this.timeUntilSeen < 0 && --this.timeUntilFire < 0) {
                this.entity.attackEntityFromRange(this.target, 0.0F);
                if (this.shouldExecute()) {
                    this.startExecuting();
                }
            }
        }

        @Override
        public boolean canMoveTo(LivingEntity target) {
            return this.entity.canSee(target);
        }

        @Override
        public float getDistanceThreshhold() {
            return 4.0F;
        }
    }
}
