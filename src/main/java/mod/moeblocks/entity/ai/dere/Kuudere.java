package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.init.MoeTags;
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
        if (item.isIn(MoeTags.KUUDERE_GIFTS)) {
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
        return Deres.KUUDERE;
    }
}
