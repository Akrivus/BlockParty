package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

import java.util.List;

public class BlankState<E extends AbstractNPCEntity> extends State<E> {
    @Override
    public void reset(E entity) {

    }

    @Override
    public void apply(List<IStateGoal> goals, E entity) {

    }
}
