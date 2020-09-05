package moe.blocks.mod.client.animation;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.client.model.SenpaiModel;
import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.SenpaiEntity;

public class Animation {

    public void setSenpaiRotationAngles(SenpaiModel model, SenpaiEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    public void render() {

    }

    public void tick(FiniteEntity entity) {

    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Animations getKey() {
        return Animations.DEFAULT;
    }
}
