package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface IState {
    void terminate(AbstractNPCEntity npc);

    void onTransfer(AbstractNPCEntity npc);

    IState transfer(AbstractNPCEntity npc);

    boolean isDone(AbstractNPCEntity npc);
}
