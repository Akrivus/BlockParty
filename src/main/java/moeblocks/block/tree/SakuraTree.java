package moeblocks.block.tree;

import moeblocks.init.MoeBlocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.DarkOakFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;

import java.util.Random;

public class SakuraTree extends Tree {
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean hive) {
        return Feature.TREE.withConfiguration(
                new BaseTreeFeatureConfig.Builder(
                        new SimpleBlockStateProvider(MoeBlocks.SAKURA_LOG.get().getDefaultState()),
                        new SimpleBlockStateProvider(MoeBlocks.SAKURA_LEAVES.get().getDefaultState()),
                        new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 2),
                        new ForkyTrunkPlacer(5, 0, 2),
                        new TwoLayerFeature(2, 1, 2)
                ).setIgnoreVines().build());
    }
}
