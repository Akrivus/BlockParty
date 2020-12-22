package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.List;
import java.util.function.BiConsumer;

public enum Dere implements IStateEnum<AbstractNPCEntity> {
    NONDERE((moe, list) -> {

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
        return new GoalState(this, this.generator);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return Dere.NONDERE; }
        return Dere.valueOf(token);
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.deres.%s", this.name().toLowerCase()));
    }

    public int getColor() {
        return this.color;
    }
}
