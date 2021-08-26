package block_party.mob.automata;

import block_party.mob.Partyer;

public interface IState {
    void terminate(Partyer npc);

    void onTransfer(Partyer npc);

    IState transfer(Partyer npc);

    boolean isDone(Partyer npc);
}
