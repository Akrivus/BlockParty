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
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

public final class HidingSpots extends SavedData {
    public static final String KEY = "BlockParty_HidingSpots";
    public static final String NBT_HIDING_SPOTS = "HidingSpots";
    public static final String NBT_BLOCK_POS = "BlockPos";
    public static final String NBT_DATABASE_ID = "DatabaseID";
    public static final Factory<HidingSpots> FACTORY = new Factory<>(
            HidingSpots::new,
            HidingSpots::load);

    private final Map<BlockPos, Long> spots = Maps.newHashMap();

    public static HidingSpots get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, KEY);
    }

    public static HidingSpots load(CompoundTag compound, HolderLookup.Provider provider) {
        HidingSpots data = new HidingSpots();
        compound.getList(NBT_HIDING_SPOTS, NBT.COMPOUND).forEach(element -> {
            CompoundTag member = (CompoundTag) element;
            data.spots.put(BlockPos.of(member.getLong(NBT_BLOCK_POS)), member.getLong(NBT_DATABASE_ID));
        });
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        this.spots.forEach((pos, id) -> {
            CompoundTag member = new CompoundTag();
            member.putLong(NBT_BLOCK_POS, pos.asLong());
            member.putLong(NBT_DATABASE_ID, id);
            list.add(member);
        });
        compound.put(NBT_HIDING_SPOTS, list);
        return compound;
    }

    public void put(BlockPos pos, long databaseId) {
        Long previous = this.spots.put(pos, databaseId);
        if (previous == null || previous != databaseId) {
            this.setDirty();
        }
    }

    public void remove(BlockPos pos) {
        if (this.spots.remove(pos) != null) {
            this.setDirty();
        }
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

    public static boolean spawn(ServerLevel level, BlockPos pos) {
        return reveal(level, pos) != null;
    }

    public static void onBreakStart(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            reveal(level, event.getPos());
        }
    }

    public static void onBreakEnd(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            reveal(level, event.getPos());
        }
    }

    public static void onPistonPush(PistonEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            reveal(level, event.getPos());
            reveal(level, event.getFaceOffsetPos());
        }
    }

    public static void onFalling(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof FallingBlockEntity fallingBlock
                && event.getLevel() instanceof ServerLevel level
                && spawn(level, fallingBlock.getStartPos())) {
            event.setCanceled(true);
        }
    }
}
