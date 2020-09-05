package moe.blocks.mod.entity.goal.engage;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.goal.EngageGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ShareGoals {
    public static class Student extends EngageGoal<FiniteEntity> {
        public Student(FiniteEntity entity) {
            super(entity, FiniteEntity.class);
        }

        @Override
        public boolean canMoveTo(FiniteEntity entity) {
            return this.entity.getFoodState().isSatiated() && this.entity.getDatingState().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodState().isHungry();
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
        public Player(FiniteEntity entity) {
            super(entity, PlayerEntity.class);
        }

        @Override
        public boolean canMoveTo(PlayerEntity entity) {
            return this.entity.getFoodState().isSatiated() && this.entity.getDatingState().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodStats().needFood();
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
