package moe.blocks.mod.entity.dere;

import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.init.MoeTags;
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
        if (item.isIn(MoeTags.YANDERE_GIFTS)) {
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
        return Deres.YANDERE;
    }
}
