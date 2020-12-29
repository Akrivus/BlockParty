package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Predicate;

public class Trigger<E extends AbstractNPCEntity, T extends IStateEnum> {
    private final T state;
    private final Predicate<E> function;
    
    public Trigger(T state, Predicate<E> function) {
        this.state = state;
        this.function = function;
    }
}
