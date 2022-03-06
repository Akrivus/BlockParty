package block_party.registry;

import block_party.blocks.grower.GinkgoTreeGrower;
import block_party.blocks.grower.SakuraTreeGrower;
import block_party.blocks.grower.WhiteSakuraTreeGrower;
import block_party.blocks.grower.WisteriaTreeGrower;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class CustomWorldGen {
    public static class ConfiguredFeatures {
        public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> GINKGO = FeatureUtils.register("ginkgo", Feature.TREE, GinkgoTreeGrower.config());
        public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> SAKURA = FeatureUtils.register("sakura", Feature.TREE, SakuraTreeGrower.config());
        public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> WHITE_SAKURA = FeatureUtils.register("white_sakura", Feature.TREE, WhiteSakuraTreeGrower.config());
        public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> WISTERIA = FeatureUtils.register("wisteria", Feature.TREE, WisteriaTreeGrower.config());
    }

    public static class Features {
        public static void add(DeferredRegister<Feature<?>> registry, IEventBus forge, IEventBus bus) {
            registry.register(bus);
        }
    }
}
