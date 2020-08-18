package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Tsundere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.99F, 0.73F, 0.49F};
    }

    @Override
    public int getNameColor() {
        return 0xfdb97c;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.YELLOW;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(ItemsMoe.Tags.RELICS)) {
            return 1.0F;
        } else if (item.isIn(ItemsMoe.Tags.TREASURES)) {
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
        return Deres.TSUNDERE;
    }
}
