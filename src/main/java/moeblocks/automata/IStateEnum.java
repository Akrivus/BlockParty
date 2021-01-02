package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTriggers;

import java.util.function.Predicate;

public interface IStateEnum<E extends AbstractNPCEntity> {
    IState getState(E applicant);
    
    String toKey();
    
    IStateEnum<E> fromKey(String key);
    
    IStateEnum<E>[] getKeys();

    default IStateEnum<E> trigger(E npc) {
        return MoeTriggers.find(this, npc).getState();
    }

    default void when(int priority, Predicate<E> function) {
        MoeTriggers.register(priority, this, function);
    }
}
