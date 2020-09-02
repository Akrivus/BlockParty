package moe.blocks.mod.entity.ai.dere;

import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.init.MoeTags;
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
        if (item.isIn(MoeTags.TSUNDERE_GIFTS)) {
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
        return Deres.TSUNDERE;
    }
}
