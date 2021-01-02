package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Predicate;

public class Trigger<E extends AbstractNPCEntity, T extends IStateEnum> {
    private final int priority;
    private final T state;
    private final Predicate<E> function;
    
    public Trigger(int priority, T state, Predicate<E> function) {
        this.priority = priority;
        this.state = state;
        this.function = function;
    }

    public boolean fire(E npc) {
        return this.function.test(npc);
    }

    public int getPriority() {
        return this.priority;
    }

    public IStateEnum<E> getState() {
        return this.state;
    }

    public static boolean isBetween(float value, float start, float end) {
        return start <= value && value <= end;
    }
}
