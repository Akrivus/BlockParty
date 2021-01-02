package moeblocks.automata.state.goal;

import net.minecraft.entity.ai.goal.Goal;

public abstract class AbstractStateGoal extends Goal {
    public abstract int getPriority();
}
