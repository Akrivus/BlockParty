package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum Emotion implements IStateEnum<AbstractNPCEntity> {
    ANGRY((npc, list) -> {

    }),
    BEGGING((npc, list) -> {

    }),
    CONFUSED((npc, list) -> {

    }),
    CRYING((npc, list) -> {

    }),
    MISCHIEVOUS((npc, list) -> {

    }),
    EMBARRASSED((npc, list) -> {

    }),
    HAPPY((npc, list) -> {

    }),
    NORMAL((npc, list) -> {

    }),
    PAINED((npc, list) -> {

    }),
    PSYCHOTIC((npc, list) -> {

    }),
    SCARED((npc, list) -> {

    }),
    SICK((npc, list) -> {

    }),
    SNOOTY((npc, list) -> {

    }),
    SMITTEN((npc, list) -> {

    }),
    TIRED((npc, list) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;

    Emotion(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator) {
        this.generator = generator;
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
        if (token.isEmpty()) { return Emotion.NORMAL; }
        return Emotion.valueOf(token);
    }
}