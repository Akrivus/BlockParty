package block_party.registry;

import block_party.BlockParty;
import block_party.world.gen.vanilla.BiomeCategoryFilter;
import block_party.world.gen.vanilla.SelectBiomeFilter;
import block_party.world.gen.vanilla.VanillaBiomeFeature;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class CustomWorldGen {
    public static class Features {
        public static final RegistryObject<VanillaBiomeFeature> VANILLA_WORLDGEN = BlockParty.WORLDGEN_FEATURES.register("vanilla_worldgen", () -> new VanillaBiomeFeature(VanillaBiomeFeature.CODEC));
        public static final List<Tuple<GenerationStep.Decoration, VanillaWorldGen>> VANILLA_WORLDGENS = new ArrayList();

        private static void addVanillaWorldGen(GenerationStep.Decoration step, VanillaWorldGen vanilla) {
            VANILLA_WORLDGENS.add(new Tuple<>(step, vanilla));
        }

        public static void add(DeferredRegister<Feature<?>> registry, IEventBus forge, IEventBus bus) {
            forge.addListener(Features::addVanillaWorldGenFeatures);
            Features.addVanillaWorldGen(GenerationStep.Decoration.VEGETAL_DECORATION, VanillaWorldGens.SAKURA_TREE);
            registry.register(bus);
        }

        public static void setup() {
            PlacementModifiers.add();
            VANILLA_WORLDGENS.forEach((vanilla) -> vanilla.getB().register());
        }

        public static void addVanillaWorldGenFeatures(BiomeLoadingEvent e) {
            VANILLA_WORLDGENS.forEach((vanilla) -> e.getGeneration().addFeature(vanilla.getA(), vanilla.getB().feature));
        }

        public static class VanillaWorldGen {
            private final ResourceKey<PlacedFeature> key;
            private final ResourceLocation delegate;
            public PlacedFeature feature;
            public ConfiguredFeature<?, ?> config;

            public VanillaWorldGen(ResourceLocation key) {
                this.key = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, key);
                this.delegate = key;
            }

            public void register() {
                this.config = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, this.name(), VANILLA_WORLDGEN.get().configured(new VanillaBiomeFeature.Config(this.key())));
                this.feature = BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, this.name(), this.config.placed(BiomeFilter.biome()));
            }

            public ResourceKey<PlacedFeature> key() {
                return this.key;
            }

            public ResourceLocation delegate() {
                return this.delegate;
            }

            private ResourceLocation name() {
                return new ResourceLocation(this.delegate().getNamespace(), "vanilla_" + this.delegate().getPath());
            }
        }

        public static class VanillaWorldGens {
            public static final VanillaWorldGen SAKURA_TREE = register("sakura_tree");

            private static VanillaWorldGen register(String name) {
                return new VanillaWorldGen(BlockParty.source(name));
            }
        }
    }

    public static class PlacementModifiers {
        public static final PlacementModifierType BIOME_FILTER = SelectBiomeFilter.TYPE;
        public static final PlacementModifierType BIOME_CATEGORY_FILTER = BiomeCategoryFilter.TYPE;

        public static void add() {
            register("biome", BIOME_FILTER);
            register("biome_category", BIOME_CATEGORY_FILTER);
        }

        public static void register(String name, PlacementModifierType type) {
            Registry.register(Registry.PLACEMENT_MODIFIERS, BlockParty.source(name), type);
        }
    }
}
