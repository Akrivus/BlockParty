package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.Trans;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum StoryPhase implements IStateEnum<AbstractNPCEntity> {
    INTRODUCTION((npc, goals) -> {

    }, (npc) -> npc.getProgress(), 0, 1),
    INFATUATION((npc, goals) -> {

    }, (npc) -> npc.getProgress(), 1, 2),
    CONFUSION((npc, goals) -> {

    }, (npc) -> npc.getProgress(), 2, 3),
    RESOLUTION((npc, goals) -> {

    }, (npc) -> npc.getProgress(), 3, 4),
    TRAGEDY((npc, goals) -> {

    }, (npc) -> npc.getProgress(), 4, 5);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    StoryPhase(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
        this.generator = generator;
        this.when(0, (npc) -> Trigger.isBetween(function.apply(npc), start, end));
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState(this, this.generator);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return StoryPhase.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return StoryPhase.values();
    }

    public static StoryPhase get(String key) {
        try {
            return StoryPhase.valueOf(key);
        } catch (IllegalArgumentException e) {
            return StoryPhase.INTRODUCTION;
        }
    }

    @Override
    public String toString() {
        return Trans.late(String.format("debug.moeblocks.story.%s", this.name().toLowerCase()));
    }
}
