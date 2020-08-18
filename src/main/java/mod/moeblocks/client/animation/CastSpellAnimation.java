package mod.moeblocks.client.animation;

import mod.moeblocks.client.Animations;
import mod.moeblocks.client.model.MoeModel;
import mod.moeblocks.entity.MoeEntity;

public class CastSpellAnimation extends Animation {

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    @Override
    public Animations getKey() {
        return Animations.CAST_SPELL;
    }
}
