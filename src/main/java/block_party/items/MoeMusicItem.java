package block_party.items;

import block_party.BlockParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;

public class MoeMusicItem extends Item implements SortableItem {
    public MoeMusicItem(Properties properties, String songPath) {
        super(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(song(songPath)));
    }

    private static ResourceKey<JukeboxSong> song(String path) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, BlockParty.source(path));
    }

    @Override
    public int getSortOrder() {
        return 100;
    }
}
