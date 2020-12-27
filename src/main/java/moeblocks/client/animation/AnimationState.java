package moeblocks.client.animation;

import moeblocks.automata.IState;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.keys.Animation;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.network.datasync.DataParameter;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class AnimationState extends WatchedGoalState<Animation, AbstractNPCEntity> {
    public AnimationState(Animation filter) {
        super(filter, (npc, list) -> { }, AbstractNPCEntity.ANIMATION);
    }
    
    public abstract void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);
    
    @Override
    public boolean canClear(AbstractNPCEntity applicant) {
        return !this.canApply(applicant);
    }
}
