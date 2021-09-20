package block_party.blocks.tree;

import block_party.custom.CustomBlocks;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.Random;

public class GinkgoTree extends AbstractTreeGrower {
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random random, boolean hive) {
        return GinkgoTree.build();
    }

    public static ConfiguredFeature build() {
        return Feature.TREE.configured(new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(CustomBlocks.GINKGO_LOG.get().defaultBlockState()), new StraightTrunkPlacer(7, 1, 0), new SimpleStateProvider(CustomBlocks.GINKGO_LEAVES.get().defaultBlockState()), new SimpleStateProvider(CustomBlocks.GINKGO_SAPLING.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 5), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());
    }
}
