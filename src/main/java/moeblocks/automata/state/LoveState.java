package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum LoveState implements IStateEnum<AbstractNPCEntity> {
    INTIMATE((npc, list) -> {

    }, (npc) -> npc.getLove(), 16, 20),
    CLOSE((npc, list) -> {

    }, (npc) -> npc.getLove(), 12, 16),
    FRIENDLY((npc, list) -> {

    }, (npc) -> npc.getLove(), 8, 12),
    ACQUAINTED((npc, list) -> {

    }, (npc) -> npc.getLove(), 4, 8),
    ESTRANGED((npc, list) -> {

    }, (npc) -> npc.getLove(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    LoveState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new ConditionalGoalState(this, this.generator, this.valuator, this.start, this.end);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return LoveState.ACQUAINTED; }
        return LoveState.valueOf(token);
    }
}
