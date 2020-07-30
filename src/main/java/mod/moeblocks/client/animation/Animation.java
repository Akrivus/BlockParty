package mod.moeblocks.client.animation;

import mod.moeblocks.client.Animations;
import mod.moeblocks.entity.MoeEntity;

public class Animation {

    public void render(MoeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks) {

    }

    public void tick(MoeEntity entity) {

    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Animations getKey() {
        return Animations.DEFAULT;
    }
}
