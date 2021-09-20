package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;

public interface ICondition {
    boolean isTrue(BlockPartyNPC entity);
    int getTimeout();
    IState getStemState();
}
