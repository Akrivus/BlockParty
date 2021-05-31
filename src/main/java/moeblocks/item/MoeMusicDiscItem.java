package moeblocks.item;

import moeblocks.MoeMod;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

public class MoeMusicDiscItem extends MusicDiscItem {
    public MoeMusicDiscItem(Supplier<SoundEvent> sound) {
        super(0, sound, new Item.Properties().group(MoeMod.ITEMS).maxStackSize(1));
    }
}
