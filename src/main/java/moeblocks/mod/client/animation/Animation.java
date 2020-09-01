package moeblocks.mod.client.animation;

import moeblocks.mod.client.Animations;
import moeblocks.mod.client.model.MoeModel;
import moeblocks.mod.client.model.SenpaiModel;
import moeblocks.mod.entity.MoeEntity;
import moeblocks.mod.entity.SenpaiEntity;
import moeblocks.mod.entity.StateEntity;

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
