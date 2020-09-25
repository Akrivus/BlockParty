package moe.blocks.mod.entity.ai.goal.react;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.ai.goal.ReactiveGoal;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.ai.brain.task.Task;

public class FlapArmsGoal extends ReactiveGoal {
    public FlapArmsGoal(CharacterEntity entity) {
        super(entity);
    }

    @Override
    public void execute() {
        this.entity.canSee(this.entity.getInteractTarget());
        this.entity.setEmotion(Emotions.HAPPY, 20, this.entity.getInteractTarget());
        this.entity.setAnimation(Animations.FLAP_ARMS);
        this.status = Task.Status.STOPPED;
    }
}
