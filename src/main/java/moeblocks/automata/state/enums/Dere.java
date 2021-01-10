package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public enum Dere implements IStateEnum<AbstractNPCEntity> {
    NYANDERE((moe, goals) -> {

    }, 0xffffff),
    HIMEDERE((moe, goals) -> {

    }, 0xcc00ff),
    KUUDERE((moe, goals) -> {

    }, 0x0000ff),
    TSUNDERE((npc, goals) -> {

    }, 0xffcc00),
    YANDERE((npc, goals) -> {

    }, 0xff0000),
    DEREDERE((npc, goals) -> {

    }, 0x0000ff),
    DANDERE((npc, goals) -> {

    }, 0x00ccff);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;
    private final int color;

    Dere(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, int color) {
        this.when(0, (npc) -> this.equals(npc.getDere()));
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
        return Dere.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return Dere.values();
    }

    public static Dere get(String key) {
        try { return Dere.valueOf(key); } catch (IllegalArgumentException e) {
            return Dere.NYANDERE;
        }
    }

    public int getColor() {
        return this.color;
    }

    public static Dere weigh(Random rand) {
        return Dere.values()[rand.nextInt(Dere.values().length - 1) + 1];
    }
}
