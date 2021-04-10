package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface ICondition {
    boolean isTrue(AbstractNPCEntity entity);
    int getTimeout();
    IState getStemState();
}
