package block_party.world.structure;

import block_party.BlockParty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DesertWellFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MoeStructureTemplates {
    private static final ResourceLocation DESERT_WELL_ID = ResourceLocation.withDefaultNamespace(MoeStructureAssignment.DESERT_WELL);
    private static final ResourceKey<ConfiguredFeature<?, ?>> VANILLA_DESERT_WELL = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            DESERT_WELL_ID);
    private static final MoeStructureTemplate FALLBACK_DESERT_WELL = new MoeStructureTemplate(
            DESERT_WELL_ID,
            MoeStructureTemplate.Source.PROTOTYPE_FALLBACK,
            buildVanillaDesertWellFeatureParts());
    private static MoeStructureTemplate cachedDesertWell;
    private static boolean loggedDesertWellRegistryProbe;

    private MoeStructureTemplates() {
    }

    public static int desertWellPartCount() {
        return FALLBACK_DESERT_WELL.partCount();
    }

    public static int desertWellPartCount(ServerLevel level) {
        return desertWell(level).partCount();
    }

    public static MoeStructureTemplate.Part desertWellPart(int index) {
        return FALLBACK_DESERT_WELL.part(index);
    }

    public static MoeStructureTemplate.Part desertWellPart(ServerLevel level, int index) {
        return desertWell(level).part(index);
    }

    public static MoeStructureTemplate desertWell(ServerLevel level) {
        if (cachedDesertWell == null) {
            cachedDesertWell = fromConfiguredFeature(level, VANILLA_DESERT_WELL).orElse(FALLBACK_DESERT_WELL);
        }
        return cachedDesertWell;
    }

    public static BlockState blockState(MoeStructureAssignment assignment) {
        if (!MoeStructureAssignment.DESERT_WELL.equals(assignment.structureId().getPath())) {
            return Blocks.SANDSTONE.defaultBlockState();
        }
        return FALLBACK_DESERT_WELL.blockState(assignment.partIndex());
    }

    private static Optional<MoeStructureTemplate> fromConfiguredFeature(
            ServerLevel level,
            ResourceKey<ConfiguredFeature<?, ?>> key) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> feature = level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(key);
        if (feature.isEmpty()) {
            logOnce("Vanilla configured feature {} was not present; using prototype structure template", key.location());
            return Optional.empty();
        }

        ConfiguredFeature<?, ?> configured = feature.get().value();
        if (configured.feature() instanceof DesertWellFeature) {
            MoeStructureTemplate template = new MoeStructureTemplate(
                    key.location(),
                    MoeStructureTemplate.Source.FEATURE_REGISTRY,
                    buildVanillaDesertWellFeatureParts());
            logOnce("Loaded Moe structure template {} from configured feature {} with {} Moe-placeable parts",
                    template.id(), configured.feature().getClass().getName(), template.partCount());
            return Optional.of(template);
        }
        logOnce("Configured feature {} resolved to {}, but no Moe extractor exists yet; using prototype structure template",
                key.location(), configured.feature().getClass().getName());
        return Optional.empty();
    }

    private static void logOnce(String message, Object... params) {
        if (!loggedDesertWellRegistryProbe) {
            loggedDesertWellRegistryProbe = true;
            BlockParty.LOGGER.info(message, params);
        }
    }

    private static List<MoeStructureTemplate.Part> buildVanillaDesertWellFeatureParts() {
        List<MoeStructureTemplate.Part> parts = new ArrayList<>();
        BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
        BlockState slab = Blocks.SANDSTONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);

        for (int y = -2; y <= 0; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (isVanillaWaterOrSandPocket(x, y, z)) {
                        continue;
                    }
                    parts.add(part(new BlockPos(x, y, z), sandstone));
                }
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if ((Math.abs(x) == 2 || Math.abs(z) == 2) && !isDesertWellSlabRimPosition(x, z)) {
                    parts.add(part(new BlockPos(x, 1, z), sandstone));
                }
            }
        }
        parts.add(part(new BlockPos(2, 1, 0), slab));
        parts.add(part(new BlockPos(-2, 1, 0), slab));
        parts.add(part(new BlockPos(0, 1, 2), slab));
        parts.add(part(new BlockPos(0, 1, -2), slab));
        for (int x : new int[]{-1, 1}) {
            for (int z : new int[]{-1, 1}) {
                for (int y = 1; y <= 3; y++) {
                    parts.add(part(new BlockPos(x, y, z), sandstone));
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                parts.add(part(new BlockPos(x, 4, z), x == 0 && z == 0 ? sandstone : slab));
            }
        }
        return List.copyOf(parts);
    }

    private static boolean isVanillaWaterOrSandPocket(int x, int y, int z) {
        int horizontalDistance = Math.abs(x) + Math.abs(z);
        return (y == 0 && horizontalDistance <= 1) || (y == -1 && horizontalDistance <= 1);
    }

    private static boolean isDesertWellSlabRimPosition(int x, int z) {
        return Math.abs(x) + Math.abs(z) == 2 && (x == 0 || z == 0);
    }

    private static MoeStructureTemplate.Part part(BlockPos offset, BlockState state) {
        return new MoeStructureTemplate.Part(offset, state, accessibilityBand(offset));
    }

    private static int accessibilityBand(BlockPos offset) {
        if (offset.getY() == 0) {
            return 0;
        }
        if (offset.getY() == 1 && (Math.abs(offset.getX()) == 2 || Math.abs(offset.getZ()) == 2)) {
            return 1;
        }
        if (offset.getY() < 0) {
            return 2;
        }
        if (offset.getY() <= 3) {
            return 3;
        }
        return offset.getX() == 0 && offset.getZ() == 0 ? 5 : 4;
    }
}
