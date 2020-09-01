package moeblocks.mod.entity.ai.dere;

import moeblocks.mod.entity.util.Deres;
import moeblocks.mod.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Dandere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.74F, 0.75F, 0.71F};
    }

    @Override
    public int getNameColor() {
        return 0xc1beb5;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.WHITE;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(MoeTags.DANDERE_GIFTS)) {
            return 4.0F;
        }
        return super.getGiftValue(stack);
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public Deres getKey() {
        return Deres.DANDERE;
    }
}
