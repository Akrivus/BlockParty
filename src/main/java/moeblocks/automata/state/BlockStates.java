package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public enum BlockStates implements IStateEnum<MoeEntity> {
    DEFAULT((moe, list) -> {

    });

    private final BiConsumer<MoeEntity, List<IStateGoal>> generator;
    private final List<Block> blocks;

    BlockStates(BiConsumer<MoeEntity, List<IStateGoal>> generator, Block... blocks) {
        this.generator = generator;
        this.blocks = Arrays.asList(blocks);
    }

    BlockStates(BiConsumer<MoeEntity, List<IStateGoal>> generator, ITag.INamedTag<Block> tag) {
        this(generator, tag.getAllElements().toArray(new Block[0]));
    }

    @Override
    public IState getState(MoeEntity applicant) {
        return new GoalState.BlockBased(this, this.generator);
    }

    public static BlockStates get(BlockState block) {
        for (BlockStates state : BlockStates.values()) {
            if (state.blocks.contains(block.getBlock())) { return state; }
        }
        return BlockStates.DEFAULT;
    }
}
