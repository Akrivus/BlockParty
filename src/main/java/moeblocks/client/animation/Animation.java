package moeblocks.client.animation;

import moeblocks.client.Animations;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;

public abstract class Animation {

    public String name() {
        return this.getKey().name();
    }

    public abstract Animations getKey();

    public void render() {

    }

    public abstract void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);

    public void tick(AbstractNPCEntity entity) {

    }
}
