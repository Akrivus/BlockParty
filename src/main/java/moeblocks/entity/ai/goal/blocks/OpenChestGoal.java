package moeblocks.entity.ai.goal.blocks;

import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.keys.Emotion;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class OpenChestGoal extends Goal implements IStateGoal {
    protected final AbstractNPCEntity entity;
    protected PlayerEntity player;
    
    public OpenChestGoal(AbstractNPCEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public int getPriority() {
        return 0x1;
    }
    
    @Override
    public boolean shouldExecute() {
        return this.entity.canBeTarget(this.entity.getInteractTarget()) && this.entity.isInteracted();
    }
    
    @Override
    public void startExecuting() {
        this.entity.setEmotion(Emotion.EMBARRASSED);
        this.player = this.entity.getInteractTarget();
        this.player.openContainer(this.entity);
    }
}
