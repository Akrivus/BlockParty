package moe.blocks.mod.entity.ai.goal.blocks;

import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class OpenChestGoal extends Goal implements IStateGoal {
    protected final AbstractNPCEntity entity;
    protected PlayerEntity player;

    public OpenChestGoal(AbstractNPCEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.canBeTarget(this.entity.getInteractTarget()) && this.entity.isInteracted() && this.entity.getRelationshipWith(player).can(Relationship.Actions.LOOK_IN_BRA);
    }

    @Override
    public void startExecuting() {
        this.entity.setEmotion(Emotions.EMBARRASSED, 600);
        this.player = this.entity.getInteractTarget();
        this.player.openContainer(this.entity);
    }

    @Override
    public int getPriority() {
        return 0x1;
    }
}
