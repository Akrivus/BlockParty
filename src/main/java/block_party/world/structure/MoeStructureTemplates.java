package block_party.world.structure;

import block_party.BlockParty;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class MoeStructureTemplates {
    private static final ResourceLocation SANDSTONE_PYRAMID_ID = BlockParty.source(MoeStructureAssignment.SANDSTONE_PYRAMID);
    private static final MoeStructureTemplate SANDSTONE_PYRAMID = new MoeStructureTemplate(
            SANDSTONE_PYRAMID_ID,
            MoeStructureTemplate.Source.PROCEDURAL_GENERATOR,
            buildSandstonePyramid());

    private MoeStructureTemplates() {
    }

    public static int sandstonePyramidPartCount() {
        return SANDSTONE_PYRAMID.partCount();
    }

    public static MoeStructureTemplate.Part sandstonePyramidPart(int index) {
        return SANDSTONE_PYRAMID.part(index);
    }

    public static MoeStructureTemplate sandstonePyramid() {
        return SANDSTONE_PYRAMID;
    }

    public static BlockState blockState(MoeStructureAssignment assignment) {
        if (MoeStructureAssignment.SANDSTONE_PYRAMID.equals(assignment.structureId().getPath())) {
            return SANDSTONE_PYRAMID.blockState(assignment.partIndex());
        }
        return Blocks.SANDSTONE.defaultBlockState();
    }

    private static List<MoeStructureTemplate.Part> buildSandstonePyramid() {
        List<MoeStructureTemplate.Part> parts = new ArrayList<>();
        BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        addSolidLayer(parts, 0, 2, sandstone);
        addSolidLayer(parts, 1, 1, sandstone);
        parts.add(part(new BlockPos(0, 2, 0), sandstone, 2));
        return List.copyOf(parts);
    }

    private static void addSolidLayer(List<MoeStructureTemplate.Part> parts, int y, int radius, BlockState state) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                parts.add(part(new BlockPos(x, y, z), state, y));
            }
        }
    }

    private static MoeStructureTemplate.Part part(BlockPos offset, BlockState state, int accessibilityBand) {
        return new MoeStructureTemplate.Part(offset, state, accessibilityBand);
    }
}
