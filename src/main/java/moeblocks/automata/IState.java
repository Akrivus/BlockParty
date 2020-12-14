package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface IState<E extends AbstractNPCEntity> {
    boolean canApply(E applicant);
    boolean canClear(E applicant);
    void apply(E applicant);
    void clear(E applicant);
}
