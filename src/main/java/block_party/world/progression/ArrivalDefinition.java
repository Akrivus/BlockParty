package block_party.world.progression;

import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Data-backed rule describing one way a Moe can become eligible to arrive. */
public record ArrivalDefinition(
        ResourceLocation id, ItemSelector collected, int threshold, BlockSelector placed,
        BlockSelector support, Block result, double exclusionRadius, int homeSearchRadius, int homeSearchVerticalRadius) {
    public boolean matches(BlockState placedState, BlockState supportState) {
        return this.placed.matches(placedState) && this.support.matches(supportState);
    }

    public int placementSpecificity() {
        return this.placed.specificity() + this.support.specificity();
    }

    public int collectedCount(ServerLevel level, UUID player) {
        return this.collected.count(level, player);
    }

    public record ItemSelector(Item item, TagKey<Item> tag) {
        public int count(ServerLevel level, UUID player) {
            return this.tag != null
                    ? PlayerProgressionCounters.countItems(level, player, this.tag)
                    : PlayerProgressionCounters.countItem(level, player, this.item);
        }

        public static ItemSelector item(ResourceLocation id) {
            return new ItemSelector(BuiltInRegistries.ITEM.getValue(id), null);
        }

        public static ItemSelector tag(ResourceLocation id) {
            return new ItemSelector(null, TagKey.create(Registries.ITEM, id));
        }
    }

    public record BlockSelector(Block block, TagKey<Block> tag, boolean any) {
        public boolean matches(BlockState state) {
            return state != null && !state.isAir()
                    && (this.any || (this.tag != null ? state.is(this.tag) : state.is(this.block)));
        }

        public int specificity() {
            return this.any ? 0 : this.tag != null ? 1 : 2;
        }

        public static BlockSelector block(ResourceLocation id) {
            return new BlockSelector(BuiltInRegistries.BLOCK.getValue(id), null, false);
        }

        public static BlockSelector tag(ResourceLocation id) {
            return new BlockSelector(null, TagKey.create(Registries.BLOCK, id), false);
        }

        public static BlockSelector anyBlock() {
            return new BlockSelector(null, null, true);
        }
    }
}
