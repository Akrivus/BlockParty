package mod.moeblocks.entity.ai.goal.engage;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.goal.EngageGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ShareGoals {
    public static class Moe extends EngageGoal<StateEntity> {
        public Moe(StateEntity entity) {
            super(entity, StateEntity.class);
        }

        @Override
        public boolean canShareWith(StateEntity entity) {
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
        public Player(StateEntity entity) {
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
