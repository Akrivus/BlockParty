package moeblocks.entity.ai.items;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

public class ConsumeGoal extends Goal {
    protected final AbstractNPCEntity entity;
    
    public ConsumeGoal(AbstractNPCEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public boolean shouldExecute() {
        return !this.entity.isHandActive() && this.entity.getHeldItem(Hand.OFF_HAND).isFood();
    }
    
    @Override
    public void startExecuting() {
        this.entity.setActiveHand(Hand.OFF_HAND);
    }
}
