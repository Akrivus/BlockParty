package block_party.blocks.grower;

import block_party.registry.CustomBlocks;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;

import java.util.Random;

public class WisteriaTreeGrower extends AbstractTreeGrower {
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random random, boolean hive) {
        return WisteriaTreeGrower.build();
    }

    public static ConfiguredFeature build() {
        return Feature.TREE.configured(new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(CustomBlocks.WISTERIA_BINE.get().defaultBlockState()), new ForkingTrunkPlacer(4, 1, 2), BlockStateProvider.simple(CustomBlocks.WISTERIA_LEAVES.get().defaultBlockState()), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1)), new TwoLayersFeatureSize(1, 0, 2)).ignoreVines().build());
    }
}
