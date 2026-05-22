package block_party.entities.data;

import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

public final class HidingSpots extends SavedData {
    public static final String KEY = "BlockParty_HidingSpots";
    public static final Factory<HidingSpots> FACTORY = new Factory<>(
            HidingSpots::new,
            HidingSpots::load);

    private final Map<BlockPos, Long> spots = Maps.newHashMap();

    public static HidingSpots get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, KEY);
    }

    public static HidingSpots load(CompoundTag compound, HolderLookup.Provider provider) {
        HidingSpots data = new HidingSpots();
        compound.getList("HidingSpots", NBT.COMPOUND).forEach(element -> {
            CompoundTag member = (CompoundTag) element;
            data.spots.put(BlockPos.of(member.getLong("BlockPos")), member.getLong("DatabaseID"));
        });
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        this.spots.forEach((pos, id) -> {
            CompoundTag member = new CompoundTag();
            member.putLong("BlockPos", pos.asLong());
            member.putLong("DatabaseID", id);
            list.add(member);
        });
        compound.put("HidingSpots", list);
        return compound;
    }

    public void put(BlockPos pos, long databaseId) {
        this.spots.put(pos, databaseId);
        this.setDirty();
    }

    public void remove(BlockPos pos) {
        this.spots.remove(pos);
        this.setDirty();
    }

    public OptionalLong find(BlockPos pos) {
        Long id = this.spots.get(pos);
        return id == null ? OptionalLong.empty() : OptionalLong.of(id);
    }

    public static Moe reveal(ServerLevel level, BlockPos pos) {
        OptionalLong databaseId = get(level).find(pos);
        if (databaseId.isEmpty()) {
            return null;
        }

        AABB bounds = new AABB(pos).inflate(1.0);
        List<MoeInHiding> hidden = level.getEntitiesOfClass(MoeInHiding.class, bounds, entity ->
                entity.getAttachPos().equals(pos) && entity.getDatabaseID() == databaseId.getAsLong());
        if (hidden.isEmpty()) {
            return null;
        }
        return hidden.getFirst().reveal();
    }
}
