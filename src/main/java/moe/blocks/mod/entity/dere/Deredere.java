package moe.blocks.mod.entity.dere;

import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BossInfo;

public class Deredere extends AbstractDere {
    @Override
    public float[] getEyeColor() {
        return new float[]{0.51F, 0.84F, 0.46F};
    }

    @Override
    public int getNameColor() {
        return 0x81d575;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.GREEN;
    }

    @Override
    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(MoeTags.DEREDERE_GIFTS)) {
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
        return Deres.DEREDERE;
    }
}
