package block_party.world.gen.vanilla;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VanillaBiomeFeature extends Feature<VanillaBiomeFeature.Config> {
    public static final Codec<Config> CODEC = ResourceKey.codec(Registry.PLACED_FEATURE_REGISTRY)
            .xmap(Config::new, Config::get)
            .fieldOf("feature")
            .codec();

    public VanillaBiomeFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        ResourceKey<PlacedFeature> feature = context.config().get();
        return level.registryAccess()
                .registryOrThrow(Registry.PLACED_FEATURE_REGISTRY)
                .get(feature)
                .place(level, context.chunkGenerator(), context.random(), context.origin());
    }

    public record Config(ResourceKey<PlacedFeature> feature) implements FeatureConfiguration {
        public ResourceKey<PlacedFeature> get() {
            return this.feature;
        }
    }
}
