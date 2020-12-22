package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum StoryPhase implements IStateEnum<AbstractNPCEntity> {
    INTRODUCTION((npc, list) -> {

    }, (npc) -> npc.getProgress(), 0, 1),
    INFATUATION((npc, list) -> {

    }, (npc) -> npc.getProgress(), 1, 2),
    CONFUSION((npc, list) -> {

    }, (npc) -> npc.getProgress(), 2, 3),
    RESOLUTION((npc, list) -> {

    }, (npc) -> npc.getProgress(), 3, 4),
    TRAGEDY((npc, list) -> {

    }, (npc) -> npc.getProgress(), 4, 5);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    StoryPhase(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
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
        if (token.isEmpty()) { return StoryPhase.INTRODUCTION; }
        return StoryPhase.valueOf(token);
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.story.%s", this.name().toLowerCase()));
    }
}
