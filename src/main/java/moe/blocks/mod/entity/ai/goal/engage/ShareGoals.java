package moe.blocks.mod.entity.ai.goal.engage;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.entity.ai.goal.EngageGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ShareGoals {
    public static class Student extends EngageGoal<StudentEntity> {
        public Student(StudentEntity entity) {
            super(entity, StudentEntity.class);
        }

        @Override
        public boolean canMoveTo(StudentEntity entity) {
            return this.entity.getFoodStats().isSatiated() && this.entity.getRelationships().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodStats().isHungry();
        }

        @Override
        public void engage() {
            this.entity.entityDropItem(this.entity.getHeldItem(Hand.OFF_HAND).split(1));
            this.engaging = true;
        }

        @Override
        public int getEngagementInterval() {
            return 600;
        }

        @Override
        public int getResetDelay() {
            return 100;
        }
    }

    public static class Player extends EngageGoal<PlayerEntity> {
        public Player(StudentEntity entity) {
            super(entity, PlayerEntity.class);
        }

        @Override
        public boolean canMoveTo(PlayerEntity entity) {
            return this.entity.getFoodStats().isSatiated() && this.entity.getRelationships().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodStats().needFood();
        }

        @Override
        public void engage() {
            this.entity.entityDropItem(this.entity.getHeldItem(Hand.OFF_HAND).split(1));
            this.engaging = true;
        }

        @Override
        public int getEngagementInterval() {
            return 600;
        }

        @Override
        public int getResetDelay() {
            return 100;
        }
    }
}
