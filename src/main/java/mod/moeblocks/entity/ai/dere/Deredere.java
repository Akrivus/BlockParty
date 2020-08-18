package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.ItemsMoe;
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
        if (item.isIn(ItemsMoe.Tags.WONDERS)) {
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
        return Deres.DEREDERE;
    }
}
