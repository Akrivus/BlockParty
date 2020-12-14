package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractDieEntity;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public enum Deres implements IStateEnum<AbstractNPCEntity> {
    HIMEDERE((moe, list) -> {

    }, AbstractDieEntity.Face.ONE, 0xcc00ff),
    KUUDERE((moe, list) -> {

    }, AbstractDieEntity.Face.TWO, 0x0000ff),
    TSUNDERE((npc, list) -> {

    }, AbstractDieEntity.Face.THREE, 0xffcc00),
    YANDERE((npc, list) -> {

    }, AbstractDieEntity.Face.FOUR, 0xff0000),
    DEREDERE((npc, list) -> {

    }, AbstractDieEntity.Face.FIVE, 0x0000ff),
    DANDERE((npc, list) -> {

    }, AbstractDieEntity.Face.SIX, 0x00ccff);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final AbstractDieEntity.Face face;
    private final int color;

    Deres(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, AbstractDieEntity.Face face, int color) {
        this.generator = generator;
        this.face = face;
        this.color = color;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState(this, this.generator);
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.deres.%s", this.name().toLowerCase()));
    }

    public static Deres get(AbstractDieEntity.Face face) {
        return Arrays.stream(Deres.values()).filter((state) -> state.face.equals(face)).findFirst().get();
    }
}
