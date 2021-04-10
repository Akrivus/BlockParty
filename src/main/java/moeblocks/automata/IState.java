package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface IState {
    IState transfer(AbstractNPCEntity npc);

    void terminate(AbstractNPCEntity npc);

    boolean isDone(AbstractNPCEntity npc);
}
