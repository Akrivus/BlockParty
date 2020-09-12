package moe.blocks.mod.entity.ai.goal.attack;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.goal.target.AbstractStateTarget;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BowAttackGoal extends Goal implements IStateGoal {
    protected final NPCEntity entity;

    public BowAttackGoal(NPCEntity entity) {
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
