package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum HungerState implements IStateEnum<AbstractNPCEntity> {
    SATISFIED((npc, list) -> {

    }, (npc) -> npc.getFoodLevel(), 16, 20),
    HUNGRY((npc, list) -> {

    }, (npc) -> npc.getFoodLevel(), 4, 16),
    STARVING((npc, list) -> {

    }, (npc) -> npc.getFoodLevel(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    HungerState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
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
        if (token.isEmpty()) { return HungerState.SATISFIED; }
        return HungerState.valueOf(token);
    }
}
