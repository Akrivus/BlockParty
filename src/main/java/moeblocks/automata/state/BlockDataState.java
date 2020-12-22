package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public enum BlockDataState implements IStateEnum<MoeEntity> {
    DEFAULT((moe, list) -> {

    });

    private final BiConsumer<MoeEntity, List<IStateGoal>> generator;
    private final List<Block> blocks;

    BlockDataState(BiConsumer<MoeEntity, List<IStateGoal>> generator, ITag.INamedTag<Block> tag) {
        this(generator, tag.getAllElements().toArray(new Block[0]));
    }

    BlockDataState(BiConsumer<MoeEntity, List<IStateGoal>> generator, Block... blocks) {
        this.generator = generator;
        this.blocks = Arrays.asList(blocks);
    }

    @Override
    public IState getState(MoeEntity applicant) {
        return new BlockBasedGoalState(this, this.generator);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<MoeEntity> fromToken(String token) {
        if (token.isEmpty()) { return BlockDataState.DEFAULT; }
        return BlockDataState.valueOf(token);
    }

    public static BlockDataState get(BlockState block) {
        for (BlockDataState state : BlockDataState.values()) {
            if (state.blocks.contains(block.getBlock())) { return state; }
        }
        return BlockDataState.DEFAULT;
    }
}
