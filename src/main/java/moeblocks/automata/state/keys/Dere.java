package moeblocks.automata.state.keys;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.List;
import java.util.function.BiConsumer;

public enum Dere implements IStateEnum<AbstractNPCEntity> {
    NYANDERE((moe, list) -> {

    }, 0xffffff),
    HIMEDERE((moe, list) -> {

    }, 0xcc00ff),
    KUUDERE((moe, list) -> {

    }, 0x0000ff),
    TSUNDERE((npc, list) -> {

    }, 0xffcc00),
    YANDERE((npc, list) -> {

    }, 0xff0000),
    DEREDERE((npc, list) -> {

    }, 0x0000ff),
    DANDERE((npc, list) -> {

    }, 0x00ccff);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final int color;

    Dere(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, int color) {
        this.generator = generator;
        this.color = color;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new WatchedGoalState(this, this.generator, AbstractNPCEntity.DERE);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        if (key.isEmpty()) { return Dere.NYANDERE; }
        return Dere.valueOf(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return Dere.values();
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.deres.%s", this.name().toLowerCase()));
    }

    public int getColor() {
        return this.color;
    }
}
