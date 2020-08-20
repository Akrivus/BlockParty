package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.ItemsMoe;
import mod.moeblocks.register.TagsMoe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Kuudere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.56F, 0.87F, 0.99F};
    }

    @Override
    public int getNameColor() {
        return 0x8edffd;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.BLUE;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(TagsMoe.TREASURES)) {
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
        return Deres.KUUDERE;
    }
}
