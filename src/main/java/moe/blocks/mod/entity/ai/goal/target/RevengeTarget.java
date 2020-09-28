package moe.blocks.mod.entity.ai.goal.target;

import moe.blocks.mod.entity.partial.NPCEntity;

public class RevengeTarget extends AbstractStateTarget {

    public RevengeTarget(NPCEntity entity) {
        super(entity);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.isVengeful() && super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        this.entity.setAttackTarget(this.entity.getRevengeTarget());
    }

    @Override
    public boolean setTarget() {
        return this.entity.isVengeful() && this.entity.canAttack(this.target = this.entity.getRevengeTarget());
    }

    @Override
    public int getPriority() {
        return 0x1;
    }
}