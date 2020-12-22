package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;

public interface IState<E extends AbstractNPCEntity> {
    void apply(E applicant);

    boolean canApply(E applicant);

    boolean canClear(E applicant);

    void clear(E applicant);

    default void tick(E applicant) { return; }

    default void render(E applicant, MatrixStack stack, float partialTickTime) {
        return;
    }
}
