package moeblocks.entity.ai.goal.attack;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.automata.IStateGoal;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BowAttackGoal extends Goal implements IStateGoal {
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
