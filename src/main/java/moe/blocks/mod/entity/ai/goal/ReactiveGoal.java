package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.data.conversation.Reactions;
import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.goal.Goal;

public abstract class ReactiveGoal extends Goal implements IStateGoal {
    protected final CharacterEntity entity;
    protected Task.Status status;

    public ReactiveGoal(CharacterEntity entity) {
        this.entity = entity;
    }

    @Override
    public void startExecuting() {
        this.execute();
        if (this.status == null) { this.status = Task.Status.RUNNING; }
    }

    @Override
    public boolean shouldExecute() {
        return this.status != Task.Status.STOPPED;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.status == Task.Status.RUNNING;
    }

    @Override
    public void resetTask() {
        this.entity.setNextTickOp(me -> me.setNextState(States.REACTION, Reactions.NONE.state));
    }

    @Override
    public int getPriority() {
        return 0x1;
    }

    public abstract void execute();
}