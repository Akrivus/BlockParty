package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.Relationship;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.BossInfo;

public abstract class AbstractDere extends AbstractState {
    public float[] getEyeColor() {
        return new float[]{1.0F, 1.0F, 1.0F};
    }

    public int getNameColor() {
        return 0xffffff;
    }

    public BossInfo.Color getBarColor() {
        return BossInfo.Color.WHITE;
    }

    public void onHello(LivingEntity host, Relationship relationship) {

    }

    public void onStare(LivingEntity host, Relationship relationship) {

    }

    @Override
    public String toString() {
        return this.getKey().name();
    }
}
