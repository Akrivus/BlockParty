package block_party.mob.automata;

import block_party.mob.Partyer;

public interface ICondition {
    boolean isTrue(Partyer entity);
    int getTimeout();
    IState getStemState();
}
