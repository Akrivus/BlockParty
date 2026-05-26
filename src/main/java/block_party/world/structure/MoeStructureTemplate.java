package block_party.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Comparator;
import java.util.List;

public record MoeStructureTemplate(ResourceLocation id, Source source, List<Part> parts) {
    public MoeStructureTemplate {
        parts = List.copyOf(parts.stream()
                .sorted(Comparator
                        .comparingInt(Part::accessibilityBand)
                        .thenComparingInt(part -> part.offset().getY())
                        .thenComparingInt(part -> Math.abs(part.offset().getX()) + Math.abs(part.offset().getZ()))
                        .thenComparingInt(part -> part.offset().getX())
                        .thenComparingInt(part -> part.offset().getZ()))
                .toList());
    }

    public int partCount() {
        return this.parts.size();
    }

    public Part part(int index) {
        return this.parts.get(Math.floorMod(index, this.parts.size()));
    }

    public BlockState blockState(int index) {
        if (index < 0 || index >= this.parts.size()) {
            return this.parts.isEmpty() ? Blocks.SANDSTONE.defaultBlockState() : this.parts.getFirst().state();
        }
        return this.parts.get(index).state();
    }

    public enum Source {
        FEATURE_REGISTRY,
        PROTOTYPE_FALLBACK
    }

    public record Part(BlockPos offset, BlockState state, int accessibilityBand) {
    }
}
