package mod.moeblocks.client.animation;

import mod.moeblocks.client.Animations;
import mod.moeblocks.client.model.MoeModel;
import mod.moeblocks.client.model.SenpaiModel;
import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.SenpaiEntity;
import mod.moeblocks.entity.StateEntity;

public class Animation {

    public void setSenpaiRotationAngles(SenpaiModel model, SenpaiEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    public void render() {

    }

    public void tick(StateEntity entity) {

    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Animations getKey() {
        return Animations.DEFAULT;
    }
}
