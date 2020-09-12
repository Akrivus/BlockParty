package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

public class ConsumeGoal extends Goal {
    protected final NPCEntity entity;

    public ConsumeGoal(NPCEntity entity) {
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
