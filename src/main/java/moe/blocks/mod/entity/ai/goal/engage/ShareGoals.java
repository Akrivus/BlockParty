package moe.blocks.mod.entity.ai.goal.engage;

import moe.blocks.mod.entity.ai.goal.EngageGoal;
import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ShareGoals {
    public static class Moe extends EngageGoal<StudentEntity> {
        public Moe(StudentEntity entity) {
            super(entity, StudentEntity.class);
        }

        @Override
        public boolean canShareWith(StudentEntity entity) {
            return this.entity.getFoodStats().isSatiated() && this.entity.getRelationships().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodStats().isHungry();
        }

        @Override
        public void engage() {
            this.entity.entityDropItem(this.entity.getHeldItem(Hand.OFF_HAND).split(1));
            this.engaged = true;
        }

        @Override
        public int getEngagementInterval() {
            return 20;
        }

        @Override
        public int getEngagementTime() {
            return 100;
        }
    }

    public static class Player extends EngageGoal<PlayerEntity> {
        public Player(StudentEntity entity) {
            super(entity, PlayerEntity.class);
        }

        @Override
        public boolean canShareWith(PlayerEntity entity) {
            return this.entity.getFoodStats().isSatiated() && this.entity.getRelationships().get(entity).canDoChoresFor() && this.entity.getHeldItem(Hand.OFF_HAND).isFood() && entity.getFoodStats().needFood();
        }

        @Override
        public void engage() {
            this.entity.entityDropItem(this.entity.getHeldItem(Hand.OFF_HAND).split(1));
            this.engaged = true;
        }

        @Override
        public int getEngagementInterval() {
            return 20;
        }

        @Override
        public int getEngagementTime() {
            return 100;
        }
    }
}
