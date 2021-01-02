package moeblocks.automata;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IState<E extends AbstractNPCEntity> {
    void apply(E applicant);
    
    boolean canClear(E applicant);
    
    void clear(E applicant);

    default void tick(E applicant) { return; }
    
    default void render(E applicant, MatrixStack stack, float partialTickTime) {
        return;
    }

    default void setRotationAngles(IRiggableModel model, E applicant, float limbSwing, float limbSwingAmount, float ageInTicks) { return; }

    default void read(CompoundNBT compound) { return; }

    default CompoundNBT write(CompoundNBT compound) {
        return compound;
    }
}
