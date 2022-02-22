package block_party.world.gen.vanilla;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;

public class BiomeCategoryFilter extends PlacementFilter {
    public static Codec<BiomeCategoryFilter> CODEC = Biome.BiomeCategory.CODEC.xmap(BiomeCategoryFilter::new, BiomeCategoryFilter::get).fieldOf("category").codec();
    public static final PlacementModifierType<BiomeCategoryFilter> TYPE = () -> CODEC;

    private final Biome.BiomeCategory category;

    public BiomeCategoryFilter(Biome.BiomeCategory category) {
        this.category = category;
    }

    public Biome.BiomeCategory get() {
        return this.category;
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, Random random, BlockPos pos) {
        WorldGenLevel level = context.getLevel();
        Biome biome = level.getBiome(pos);
        return biome.getRegistryName().equals(this.get());
    }
}
