package moeblocks.entity.ai.goal.target;

import moeblocks.entity.AbstractNPCEntity;

public class RevengeTarget extends AbstractStateTarget {
    
    public RevengeTarget(AbstractNPCEntity entity) {
        super(entity);
    }
    
    @Override
    public int getPriority() {
        return 0x1;
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
}