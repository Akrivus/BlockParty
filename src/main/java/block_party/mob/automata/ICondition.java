package block_party.mob.automata;

import block_party.mob.BlockPartyNPC;

public interface ICondition {
    boolean isTrue(BlockPartyNPC entity);
    int getTimeout();
    IState getStemState();
}
