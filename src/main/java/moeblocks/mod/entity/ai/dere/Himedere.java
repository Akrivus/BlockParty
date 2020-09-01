package moeblocks.mod.entity.ai.dere;

import moeblocks.mod.entity.util.Deres;
import moeblocks.mod.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Himedere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.73F, 0.55F, 0.88F};
    }

    @Override
    public int getNameColor() {
        return 0xba8de0;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.PURPLE;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(MoeTags.HIMEDERE_GIFTS)) {
            return 4.0F;
        }
        return super.getGiftValue(stack);
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    @Override
    public Deres getKey() {
        return Deres.HIMEDERE;
    }
}
