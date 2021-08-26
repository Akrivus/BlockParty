package block_party.blocks.tree;

import block_party.init.BlockPartyBlocks;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;

import java.util.Random;

public class WhiteSakuraTree extends AbstractTreeGrower {
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random random, boolean hive) {
        return WhiteSakuraTree.build();
    }

    public static ConfiguredFeature<TreeConfiguration, ?> build() {
        return Feature.TREE.configured(new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(BlockPartyBlocks.SAKURA_LOG.get().defaultBlockState()), new ForkingTrunkPlacer(5, 2, 2), new SimpleStateProvider(BlockPartyBlocks.WHITE_SAKURA_BLOSSOMS.get().defaultBlockState()), new SimpleStateProvider(BlockPartyBlocks.WHITE_SAKURA_SAPLING.get().defaultBlockState()), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2), new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());
    }
}
