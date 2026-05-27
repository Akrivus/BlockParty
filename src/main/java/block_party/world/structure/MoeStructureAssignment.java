package block_party.world.structure;

import block_party.db.DimBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.UUID;

public record MoeStructureAssignment(
        UUID cohortId,
        ResourceLocation structureId,
        int partIndex,
        BlockPos offset,
        DimBlockPos target,
        State state) {
    public static final String DESERT_WELL = "desert_well";
    public static final String SANDSTONE_PYRAMID = "sandstone_pyramid";
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);
    private static final ResourceLocation EMPTY_STRUCTURE = ResourceLocation.withDefaultNamespace("none");

    public static MoeStructureAssignment none() {
        return new MoeStructureAssignment(EMPTY_UUID, EMPTY_STRUCTURE, -1, BlockPos.ZERO, new DimBlockPos(), State.NONE);
    }

    public static MoeStructureAssignment desertWell(UUID cohortId, int partIndex, BlockPos offset, DimBlockPos target) {
        return new MoeStructureAssignment(cohortId, ResourceLocation.withDefaultNamespace(DESERT_WELL), partIndex, offset, target, State.IDLE);
    }

    public static MoeStructureAssignment sandstonePyramid(UUID cohortId, int partIndex, BlockPos offset, DimBlockPos target) {
        return new MoeStructureAssignment(cohortId, block_party.BlockParty.source(SANDSTONE_PYRAMID), partIndex, offset, target, State.IDLE);
    }

    public boolean assigned() {
        return !this.cohortId.equals(EMPTY_UUID) && this.partIndex >= 0 && !this.structureId.getPath().isBlank();
    }

    public MoeStructureAssignment withState(State state) {
        return new MoeStructureAssignment(this.cohortId, this.structureId, this.partIndex, this.offset, this.target, state);
    }

    public MoeStructureAssignment withTarget(DimBlockPos target) {
        return new MoeStructureAssignment(this.cohortId, this.structureId, this.partIndex, this.offset, target, this.state);
    }

    public BlockState blockState() {
        return MoeStructureTemplates.blockState(this);
    }

    public CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        compound.putString("CohortUUID", this.cohortId.toString());
        compound.putString("StructureId", this.structureId.toString());
        compound.putInt("PartIndex", this.partIndex);
        compound.putLong("Offset", this.offset.asLong());
        compound.put("Target", this.target.write());
        compound.putString("State", this.state.name());
        return compound;
    }

    public static MoeStructureAssignment read(CompoundTag compound) {
        if (compound == null || !compound.contains("CohortUUID")) {
            return none();
        }
        UUID cohort = parseUuid(compound.getString("CohortUUID"));
        ResourceLocation structure = parseResource(compound.getString("StructureId"));
        State state = State.fromValue(compound.getString("State"));
        return new MoeStructureAssignment(
                cohort,
                structure,
                compound.getInt("PartIndex"),
                BlockPos.of(compound.getLong("Offset")),
                compound.contains("Target") ? new DimBlockPos(compound.getCompound("Target")) : new DimBlockPos(),
                state);
    }

    private static UUID parseUuid(String value) {
        try {
            return value == null || value.isBlank() ? EMPTY_UUID : UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return EMPTY_UUID;
        }
    }

    private static ResourceLocation parseResource(String value) {
        try {
            return value == null || value.isBlank() ? EMPTY_STRUCTURE : ResourceLocation.parse(value);
        } catch (IllegalArgumentException ignored) {
            return EMPTY_STRUCTURE;
        }
    }

    public enum State {
        NONE,
        IDLE,
        ASSEMBLING,
        MOVING,
        HIDDEN;

        public static State fromValue(String value) {
            if (value == null || value.isBlank()) {
                return NONE;
            }
            try {
                return valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return NONE;
            }
        }
    }
}
