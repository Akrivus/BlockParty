package block_party.blocks.grower;

import block_party.registry.CustomBlocks;
import block_party.registry.CustomWorldGen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;

import java.util.Random;

public class WhiteSakuraTreeGrower extends AbstractTreeGrower {
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hive) {
        return (ResourceKey<ConfiguredFeature<?, ?>>) CustomWorldGen.ConfiguredFeatures.WHITE_SAKURA;
    }

    public static TreeConfiguration config() {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(CustomBlocks.SAKURA_LOG.get().defaultBlockState()), new ForkingTrunkPlacer(5, 2, 2), BlockStateProvider.simple(CustomBlocks.WHITE_SAKURA_BLOSSOMS.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    }
}
