package moeblocks.automata.state.keys;

import moeblocks.automata.*;
import moeblocks.automata.state.PredicateGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTags;
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
        if (key.isEmpty()) { return HeldItemState.DEFAULT; }
        return HeldItemState.valueOf(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HeldItemState.values();
    }

    public static HeldItemState get(Item item) {
        for (HeldItemState state : HeldItemState.values()) {
            if (state.items.contains(item)) { return state; }
        }
        return HeldItemState.DEFAULT;
    }
}
