package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum TimesOfDay implements IStateEnum<AbstractNPCEntity> {
    MORNING((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 0, 3000),
    NOON((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 3000, 6000),
    EVENING((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 6000, 9000),
    DUSK((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 9000, 12000),
    NIGHT((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 12000, 15000),
    MIDNIGHT((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 15000, 18000),
    MORROW((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 18000, 21000),
    DAWN((npc, list) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 21000, 24000);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    TimesOfDay(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start; this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState.ValueBased(this, this.generator, this.valuator, this.start, this.end);
    }
}
