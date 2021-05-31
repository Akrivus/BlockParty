package moeblocks.block.tree;

import moeblocks.init.MoeBlocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

import java.util.Random;

public class GinkgoTree extends Tree {
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean hive) {
        return GinkgoTree.build();
    }

    public static ConfiguredFeature build() {
        return Feature.TREE.withConfiguration(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(MoeBlocks.GINKGO_LOG.get().getDefaultState()), new SimpleBlockStateProvider(MoeBlocks.GINKGO_LEAVES.get().getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 5), new StraightTrunkPlacer(7, 1, 0), new TwoLayerFeature(1, 0, 1)).setIgnoreVines().build());
    }
}
