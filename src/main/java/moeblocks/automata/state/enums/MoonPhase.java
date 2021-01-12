package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum MoonPhase implements IStateEnum<AbstractNPCEntity> {
    NEW((npc, goals) -> {

    }, 0.00F, 0.25F),
    CRESCENT((npc, goals) -> {

    }, 0.25F, 0.50F),
    QUARTER((npc, goals) -> {

    }, 0.50F, 0.75F),
    GIBBOUS((npc, goals) -> {

    }, 0.75F, 1.00F),
    FULL((npc, goals) -> {

    }, 1.00F, 1.00F);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    MoonPhase(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(0, (npc) -> Trigger.isBetween(npc.world.getMoonFactor(), start, end));
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
}
