package block_party.mob.automata;

import block_party.mob.BlockPartyNPC;

public interface IState {
    void terminate(BlockPartyNPC npc);

    void onTransfer(BlockPartyNPC npc);

    IState transfer(BlockPartyNPC npc);

    boolean isDone(BlockPartyNPC npc);
}
