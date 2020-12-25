package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.keys.BlockDataState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.List;
import java.util.function.BiConsumer;

public class WatchedGoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> extends GoalState<O, E> {
    private final DataParameter<String> key;

    public WatchedGoalState(O filter, BiConsumer<E, List<IStateGoal>> generator, DataParameter<String> key) {
        super(filter, generator);
        this.key = key;
    }

    @Override
    public void apply(E applicant) {
        applicant.getDataManager().set(this.key, this.filter.toKey());
        super.apply(applicant);
    }
}
