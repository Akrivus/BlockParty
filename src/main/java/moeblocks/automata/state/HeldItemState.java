package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

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
        return new GoalState(this, this.generator);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return HeldItemState.DEFAULT; }
        return HeldItemState.valueOf(token);
    }

    public static HeldItemState get(Item item) {
        for (HeldItemState state : HeldItemState.values()) {
            if (state.items.contains(item)) { return state; }
        }
        return HeldItemState.DEFAULT;
    }
}
