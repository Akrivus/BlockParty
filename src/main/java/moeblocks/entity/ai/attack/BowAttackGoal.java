package moeblocks.entity.ai.attack;

import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.EnumSet;

public class BowAttackGoal extends AbstractStateGoal {
    protected final AbstractNPCEntity entity;
    
    public BowAttackGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }
    
    @Override
    public int getPriority() {
        return 0x6;
    }
    
    @Override
    public boolean shouldExecute() {
        return false;
    }
}
