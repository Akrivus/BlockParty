package block_party.blocks.grower;

import block_party.registry.CustomBlocks;
import block_party.registry.CustomWorldGen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;

public class WisteriaTreeGrower {
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hive) {
        return (ResourceKey<ConfiguredFeature<?, ?>>) CustomWorldGen.ConfiguredFeatures.WISTERIA;
    }

    public static TreeConfiguration config() {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(CustomBlocks.WISTERIA_BINE.get().defaultBlockState()), new ForkingTrunkPlacer(4, 1, 2), BlockStateProvider.simple(CustomBlocks.WISTERIA_LEAVES.get().defaultBlockState()), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1)), new TwoLayersFeatureSize(1, 0, 2)).ignoreVines().build();
    }
}
