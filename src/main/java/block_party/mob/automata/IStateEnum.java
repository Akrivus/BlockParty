package block_party.mob.automata;

import block_party.mob.Partyer;

public interface IStateEnum extends IState {
    @Override
    default void terminate(Partyer npc) { }

    @Override
    default void onTransfer(Partyer npc) { }

    @Override
    default boolean isDone(Partyer npc) {
        return true;
    }
}
