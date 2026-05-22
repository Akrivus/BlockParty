package block_party.registry;

import block_party.BlockParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class CustomTags {
    public static final TagKey<Block> SPAWNS_MOES = TagKey.create(Registries.BLOCK, BlockParty.source("spawns_moes"));

    private CustomTags() {
    }
}
