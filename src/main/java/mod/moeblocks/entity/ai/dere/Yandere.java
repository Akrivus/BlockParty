package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.ItemsMoe;
import mod.moeblocks.register.TagsMoe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Yandere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.95F, 0.42F, 0.73F};
    }

    @Override
    public int getNameColor() {
        return 0xf276ba;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.PINK;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(TagsMoe.RELICS)) {
            return 1.0F;
        } else if (item.isIn(TagsMoe.WONDERS)) {
            return 0.5F;
        } else {
            return 0.1F;
        }
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public Deres getKey() {
        return Deres.YANDERE;
    }
}
