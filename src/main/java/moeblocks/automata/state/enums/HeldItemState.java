package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.PredicateGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTags;
import moeblocks.init.MoeTriggers;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.Hand;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public enum HeldItemState implements IStateEnum<AbstractNPCEntity> {
    DEFAULT((moe, list) -> {
    
    }),
    FARMER((moe, list) -> {
    
    }, MoeTags.FARMER),
    FIGHTER((moe, list) -> {
    
    }, MoeTags.FIGHTER),
    MINER((moe, list) -> {
    
    }, MoeTags.MINER),
    ARCHER((moe, list) -> {
    
    }, MoeTags.ARCHER);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final List<Item> items;
    
    HeldItemState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, ITag.INamedTag<Item> tag) {
        this(generator, tag.getAllElements().toArray(new Item[0]));
    }
    
    HeldItemState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Item... items) {
        this.generator = generator;
        this.items = Arrays.asList(items);
    }
    
    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new PredicateGoalState<>(this, this.generator, (npc) -> this.items.contains(npc.getHeldItem(Hand.MAIN_HAND).getItem()));
    }
    
    @Override
    public String toKey() {
        return this.name();
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return HeldItemState.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HeldItemState.values();
    }
    
    public static HeldItemState get(String key) {
        try {
            return HeldItemState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return HeldItemState.DEFAULT;
        }
    }
}
