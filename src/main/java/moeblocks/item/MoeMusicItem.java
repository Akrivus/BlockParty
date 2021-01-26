package moeblocks.item;

import moeblocks.MoeMod;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

public class MoeMusicItem extends MusicDiscItem {

    public MoeMusicItem(int value, Supplier<SoundEvent> sound) {
        super(value, sound, new Item.Properties().group(MoeMod.ITEMS));
    }
}
