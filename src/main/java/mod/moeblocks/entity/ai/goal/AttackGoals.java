package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class AttackGoals {
    public static class Melee extends Goal {
        private final MoeEntity moe;
        private int timeUntilAttacking;
        private Path path;

        public Melee(MoeEntity moe) {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
            this.moe = moe;
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity victim = this.moe.getAttackTarget();
            if (this.moe.isMeleeFighter() && this.moe.canBeTarget(victim)) {
                this.path = this.moe.getNavigator().getPathToEntity(victim, 0);
                if (this.path != null) {
                    this.timeUntilAttacking = this.moe.getAttackCooldown();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute() || !this.moe.hasPath();
        }

        @Override
        public void startExecuting() {
            LivingEntity victim = this.moe.getAttackTarget();
            if (victim != null) {
                this.moe.getNavigator().setPath(this.path, 1.0F);
            }
        }

        @Override
        public void resetTask() {
            this.moe.getNavigator().clearPath();
        }

        @Override
        public void tick() {
            LivingEntity victim = this.moe.getAttackTarget();
            if (victim != null) {
                this.moe.getNavigator().setPath(this.path, 1.0F);
                if (this.moe.getEntitySenses().canSee(victim) && this.moe.getDistance(victim) < 3.0F) {
                    --this.timeUntilAttacking;
                    if (this.timeUntilAttacking < 0) {
                        this.timeUntilAttacking = this.moe.getAttackCooldown();
                        this.moe.attackEntityAsMob(victim);
                        this.moe.swingArm(Hand.MAIN_HAND);
                    }
                }
            }
        }
    }

    public static class Ranged extends Goal {
        private final MoeEntity moe;
        private int timeUntilAttacking;
        private int timeUntilSeen;

        public Ranged(MoeEntity moe) {
            super();
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            this.moe = moe;
        }

        @Override
        public boolean shouldExecute() {
            LivingEntity victim = this.moe.getAttackTarget();
            if (this.moe.isRangedFighter() && this.moe.canBeTarget(victim)) {
                this.timeUntilAttacking = this.moe.getAttackCooldown();
                this.timeUntilSeen = 5;
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            LivingEntity victim = this.moe.getAttackTarget();
            return this.moe.isRangedFighter() && this.moe.canBeTarget(victim) && this.moe.hasPath();
        }

        @Override
        public void resetTask() {
            this.moe.getNavigator().clearPath();
        }

        public void tick() {
            LivingEntity victim = this.moe.getAttackTarget();
            this.moe.getLookController().setLookPositionWithEntity(victim, 30.0F, 30.0F);
            if (this.moe.getEntitySenses().canSee(victim)) {
                if (this.timeUntilSeen < 0) {
                    if (this.timeUntilAttacking < 0) {
                        double distance = this.moe.getDistanceSq(victim.getPosX(), victim.getPosY(), victim.getPosZ());
                        this.moe.attackEntityFromRange(victim, MathHelper.clamp(MathHelper.sqrt(distance) / 256.0, 0.1F, 1.0F));
                        this.moe.getNavigator().clearPath();
                    } else {
                        --this.timeUntilAttacking;
                    }
                } else {
                    --this.timeUntilSeen;
                }
            } else {
                this.moe.getNavigator().tryMoveToEntityLiving(victim, 1.0F);
                this.timeUntilSeen = 5;
            }
        }
    }
}
