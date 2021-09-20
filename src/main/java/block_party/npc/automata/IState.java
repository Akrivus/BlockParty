package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;

public interface IState {
    void terminate(BlockPartyNPC npc);

    void onTransfer(BlockPartyNPC npc);

    IState transfer(BlockPartyNPC npc);

    boolean isDone(BlockPartyNPC npc);
}
