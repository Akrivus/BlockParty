package block_party.entities.chores;

import block_party.entities.Moe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class ChoreTypes {
    private static final String NBT_TYPE = "Type";

    private static final Map<ResourceLocation, Reader> READERS = Map.of(
            CardinalForestChore.ID, CardinalForestChore::read);

    private ChoreTypes() {
    }

    public static MoeChore readSaved(CompoundTag compound) {
        if (compound.contains(Moe.NBT_CHORE)) {
            return read(compound.getCompound(Moe.NBT_CHORE));
        }
        return NoChore.INSTANCE;
    }

    public static void writeSaved(CompoundTag compound, MoeChore chore) {
        if (chore.active()) {
            compound.put(Moe.NBT_CHORE, write(chore));
        }
    }

    private static MoeChore read(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString(NBT_TYPE));
        Reader reader = id == null ? null : READERS.get(id);
        if (reader == null) {
            return NoChore.INSTANCE;
        }
        return reader.read(tag);
    }

    private static CompoundTag write(MoeChore chore) {
        CompoundTag tag = chore.write();
        tag.putString(NBT_TYPE, chore.id().toString());
        return tag;
    }

    private interface Reader {
        MoeChore read(CompoundTag tag);
    }
}
