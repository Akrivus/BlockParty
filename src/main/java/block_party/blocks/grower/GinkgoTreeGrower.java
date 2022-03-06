package block_party.blocks.grower;

import block_party.registry.CustomBlocks;
import block_party.registry.CustomWorldGen;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.Random;

public class GinkgoTreeGrower extends AbstractTreeGrower {
    @Override
    protected Holder<ConfiguredFeature<TreeConfiguration, ?>> getConfiguredFeature(Random random, boolean hive) {
        return CustomWorldGen.ConfiguredFeatures.GINKGO;
    }

    public static TreeConfiguration config() {
        return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(CustomBlocks.GINKGO_LOG.get().defaultBlockState()), new StraightTrunkPlacer(7, 1, 0), BlockStateProvider.simple(CustomBlocks.GINKGO_LEAVES.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 5), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build();
    }
}
