package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.BlockGoalState;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.Block;
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
        return new BlockGoalState(this, this.generator, this.blocks);
    }
    
    @Override
    public String toKey() {
        return this.name();
    }
    
    @Override
    public IStateEnum<MoeEntity> fromKey(String key) {
        return BlockDataState.get(key);
    }
    
    @Override
    public IStateEnum<MoeEntity>[] getKeys() {
        return BlockDataState.values();
    }
    
    public static BlockDataState get(String key) {
        try {
            return BlockDataState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return BlockDataState.DEFAULT;
        }
    }
}
