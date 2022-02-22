package block_party.world.gen.vanilla;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;

public class SelectBiomeFilter extends PlacementFilter {
    public static Codec<SelectBiomeFilter> CODEC = ResourceLocation.CODEC.xmap(SelectBiomeFilter::new, SelectBiomeFilter::get).fieldOf("biome").codec();
    public static final PlacementModifierType<SelectBiomeFilter> TYPE = () -> CODEC;

    private final ResourceLocation location;

    public SelectBiomeFilter(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation get() {
        return this.location;
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
