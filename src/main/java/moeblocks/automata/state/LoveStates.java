package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum LoveStates implements IStateEnum<AbstractNPCEntity> {
    INTIMATE((npc, list) -> {

    }, (npc) -> npc.getLove(), 16, 20),
    CLOSE((npc, list) -> {

    }, (npc) -> npc.getLove(), 12, 16),
    FRIENDLY((npc, list) -> {

    }, (npc) -> npc.getLove(), 8, 12),
    ACQUAINTED((npc, list) -> {

    }, (npc) -> npc.getLove(), 4, 8),
    ESTRANGED((npc, list) -> {

    }, (npc) -> npc.getLove(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    LoveStates(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start; this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState.ValueBased(this, this.generator, this.valuator, this.start, this.end);
    }
}
