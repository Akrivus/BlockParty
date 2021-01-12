package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum StoryPhase implements IStateEnum<AbstractNPCEntity> {
    INTRODUCTION((npc, goals) -> {

    }),
    INFATUATION((npc, goals) -> {

    }),
    CONFUSION((npc, goals) -> {

    }),
    HAPPY_ENDING((npc, goals) -> {

    }),
    BITTERSWEET_ENDING((npc, goals) -> {

    }),
    TRAGIC_ENDING((npc, goals) -> {

    }),
    DEAD((npc, goals) -> {

    }),
    ESTRANGED((npc, goals) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    StoryPhase(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator) {
        this.when(1, (npc) -> this.equals(npc.getStoryPhase()));
        this.generator = generator;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new WatchedGoalState(this, this.generator, AbstractNPCEntity.STORY_PHASE);
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
        try { return StoryPhase.valueOf(key); } catch (IllegalArgumentException e) {
            return StoryPhase.INTRODUCTION;
        }
    }
}
