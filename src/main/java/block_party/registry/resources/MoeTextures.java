package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.registry.CustomTags;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class MoeTextures {
    private static Map<Block, List<Override>> overrides = Map.of();

    private MoeTextures() {
    }

    public static ResourceLocation get(Moe moe) {
        return getTextureFor(moe.getVisibleBlockState(), moe.getActualBlockState(), getDefaultPathFor(moe.getVisibleBlockState()));
    }

    public static ResourceLocation getDefaultPathFor(BlockState state) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String file = location.getPath();
        if (BlockParty.isChristmas() && state.is(CustomTags.HAS_FESTIVE_TEXTURES)) {
            file += ".christmas";
        }
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/moe/" + file + ".png");
    }

    public static ResourceLocation getTextureFor(BlockState visibleState, BlockState actualState, ResourceLocation fallback) {
        List<Override> blockOverrides = overrides.get(visibleState.getBlock());
        if (blockOverrides == null) {
            return fallback;
        }
        for (Override override : blockOverrides) {
            if (override.matches(actualState)) {
                return override.texture();
            }
        }
        return fallback;
    }

    public static int overrideCount() {
        return overrides.values().stream().mapToInt(List::size).sum();
    }

    static void replaceOverrides(Map<Block, List<Override>> next) {
        overrides = Map.copyOf(next);
    }

    public record Override(BlockState state, Map<String, Comparable<?>> props, ResourceLocation texture) {
        public Override {
            props = Map.copyOf(new HashMap<>(props));
        }

        public boolean matches(BlockState candidate) {
            if (this.state.getBlock() != candidate.getBlock()) {
                return false;
            }
            for (var property : candidate.getProperties()) {
                Comparable<?> expected = this.props.get(property.getName());
                if (expected != null && !expected.equals(candidate.getValue(property))) {
                    return false;
                }
            }
            return true;
        }
    }
}
