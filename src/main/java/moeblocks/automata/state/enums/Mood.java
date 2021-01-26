package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum Mood implements IStateEnum<AbstractNPCEntity> {
    WANDER((npc, goals) -> {

    }),
    WORK((npc, goals) -> {

    }),
    PLAY((npc, goals) -> {

    }),
    GATHER((npc, goals) -> {

    }),
    RELAX((npc, goals) -> {

    }),
    SLEEP((npc, goals) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    Mood(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator) {
        this.generator = generator;
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
        return MoonPhase.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return MoonPhase.values();
    }

    public static MoonPhase get(String key) {
        try { return MoonPhase.valueOf(key); } catch (IllegalArgumentException e) {
            return MoonPhase.FULL;
        }
    }

    public static void registerTriggers() {
        Mood.SLEEP.when(1, (npc) -> npc.is(TimeOfDay.DAWN) && npc.isTimeToSleep());
        Mood.SLEEP.when(1, (npc) -> npc.is(TimeOfDay.MIDNIGHT) && npc.isTimeToSleep());
        Mood.SLEEP.when(1, (npc) -> npc.is(TimeOfDay.NIGHT) && npc.isTimeToSleep());
        Mood.RELAX.when(1, (npc) -> npc.is(TimeOfDay.EVENING));
        Mood.WORK.when(1, (npc) -> npc.is(TimeOfDay.NOON));
        Mood.GATHER.when(1, (npc) -> npc.is(TimeOfDay.MORNING));
    }
}
