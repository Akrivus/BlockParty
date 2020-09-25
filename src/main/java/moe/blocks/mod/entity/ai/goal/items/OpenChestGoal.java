package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class OpenChestGoal extends Goal implements IStateGoal {
    protected final CharacterEntity entity;
    protected PlayerEntity player;

    public OpenChestGoal(CharacterEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.canBeTarget(this.entity.getInteractTarget()) && this.entity.isInteracted();
    }

    @Override
    public void startExecuting() {
        this.player = this.entity.getInteractTarget();
        this.player.openContainer(this.entity);
    }

    @Override
    public int getPriority() {
        return 0x1;
    }
}
