package moeblocks.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public interface IRiggableModel {
    ModelRenderer getRightArm();
    ModelRenderer getLeftArm();
    ModelRenderer getRightWing();
    ModelRenderer getLeftWing();
    ModelRenderer getRightLeg();
    ModelRenderer getLeftLeg();
    ModelRenderer getHead();
    ModelRenderer getHair();
    ModelRenderer getBody();
    ModelRenderer getTail();
    ModelRenderer getTailTip();

    default ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.getLeftArm() : this.getRightArm();
    }
}
