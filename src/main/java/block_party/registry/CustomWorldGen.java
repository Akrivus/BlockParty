package block_party.registry;

import block_party.BlockParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class CustomWorldGen {
    public static final ResourceKey<ConfiguredFeature<?, ?>> GINKGO_TREE = configured("ginkgo_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAKURA_TREE = configured("sakura_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_SAKURA_TREE = configured("white_sakura_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WISTERIA_TREE = configured("wisteria_tree");

    private CustomWorldGen() {
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configured(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, BlockParty.source(path));
    }
}
