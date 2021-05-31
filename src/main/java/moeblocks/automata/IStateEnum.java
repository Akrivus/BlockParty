package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface IStateEnum extends IState {
    @Override
    default void terminate(AbstractNPCEntity npc) { }

    @Override
    default void onTransfer(AbstractNPCEntity npc) { }

    @Override
    default boolean isDone(AbstractNPCEntity npc) {
        return true;
    }
}
