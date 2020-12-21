package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public enum ItemStates implements IStateEnum<AbstractNPCEntity> {
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

    ItemStates(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Item... items) {
        this.generator = generator;
        this.items = Arrays.asList(items);
    }

    ItemStates(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, ITag.INamedTag<Item> tag) {
        this(generator, tag.getAllElements().toArray(new Item[0]));
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState(this, this.generator);
    }

    public static ItemStates get(Item item) {
        for (ItemStates state : ItemStates.values()) {
            if (state.items.contains(item)) { return state; }
        }
        return ItemStates.DEFAULT;
    }
}
