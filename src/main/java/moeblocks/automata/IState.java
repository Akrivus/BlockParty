package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

public interface IState<E extends AbstractNPCEntity> {
    void apply(E applicant);

    boolean canApply(E applicant);

    boolean canClear(E applicant);

    void clear(E applicant);
}
