package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum HealthStates implements IStateEnum<AbstractNPCEntity> {
    PERFECT((npc, list) -> {

    }, (npc) -> npc.getHealth(), 16, 20),
    GOOD((npc, list) -> {

    }, (npc) -> npc.getHealth(), 12, 16),
    FAIR((npc, list) -> {

    }, (npc) -> npc.getHealth(), 8, 12),
    SERIOUS((npc, list) -> {

    }, (npc) -> npc.getHealth(), 4, 8),
    CRITICAL((npc, list) -> {

    }, (npc) -> npc.getHealth(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    HealthStates(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState.ValueBased(this, this.generator, this.valuator, this.start, this.end);
    }
}
