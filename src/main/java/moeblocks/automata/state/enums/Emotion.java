package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum Emotion implements IStateEnum<AbstractNPCEntity> {
    ANGRY((npc, goals) -> {

    }),
    BEGGING((npc, goals) -> {

    }),
    CONFUSED((npc, goals) -> {

    }),
    CRYING((npc, goals) -> {

    }),
    MISCHIEVOUS((npc, goals) -> {

    }),
    EMBARRASSED((npc, goals) -> {

    }),
    HAPPY((npc, goals) -> {

    }),
    NORMAL((npc, goals) -> {

    }),
    PAINED((npc, goals) -> {

    }),
    PSYCHOTIC((npc, goals) -> {

    }),
    SCARED((npc, goals) -> {

    }),
    SICK((npc, goals) -> {

    }),
    SNOOTY((npc, goals) -> {

    }),
    SMITTEN((npc, goals) -> {

    }),
    TIRED((npc, goals) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    Emotion(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator) {
        this.when(0, (npc) -> this.equals(npc.getEmotion()));
        this.generator = generator;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new WatchedGoalState(this, this.generator, AbstractNPCEntity.EMOTION);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return Emotion.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return Emotion.values();
    }

    public static Emotion get(String key) {
        try { return Emotion.valueOf(key); } catch (IllegalArgumentException e) {
            return Emotion.NORMAL;
        }
    }

    public static void registerTriggers() {
        Emotion.EMBARRASSED.when(1, (npc) -> npc.isProtagonistBeingPerverted());
    }
}
