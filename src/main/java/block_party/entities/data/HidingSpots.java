package block_party.entities.data;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class HidingSpots extends SavedData {
    public static String KEY = "BlockParty_HidingSpots";
    public Map<BlockPos, Long> list = Maps.newHashMap();

    public HidingSpots(CompoundTag compound) {
        ListTag list = compound.getList("HidingSpots", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            BlockPos pos = BlockPos.of(member.getLong("BlockPos"));
            long id = member.getLong("DatabaseID");
            this.list.put(pos, id);
        });
    }

    public HidingSpots() {
        this(new CompoundTag());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        this.list.forEach((pos, id) -> {
            CompoundTag member = new CompoundTag();
            member.putLong("BlockPos", pos.asLong());
            member.putLong("DatabaseID", id);
            list.add(member);
        });
        compound.put("HidingSpots", list);
        return compound;
    }

    public static void remove(MoeInHiding moe) {
        if (moe.level() instanceof ServerLevel level)
            set(level, (spot) -> spot.list.remove(moe.blockPosition(), moe.getDatabaseID()));
    }

    public static void add(Moe moe) {
        if (moe.level() instanceof ServerLevel level)
            set(level, (spot) -> spot.list.put(moe.blockPosition(), moe.getDatabaseID()));
    }

    private static HidingSpots get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(HidingSpots::new, HidingSpots::new, KEY);
    }

    private static void set(ServerLevel level, Consumer<HidingSpots> action) {
        HidingSpots spot = get(level);
        action.accept(spot);
        spot.setDirty(true);
    }

    private static long get(ServerLevel level, BlockPos pos) {
        HidingSpots spot = get(level);
        return spot.list.get(pos);
    }

    private static boolean isNormalBlock(ServerLevel level, BlockPos pos) {
        HidingSpots spot = get(level);
        return spot.list.get(pos) == null;
    }

    public static boolean spawn(ServerLevel level, DimBlockPos pos) {
        if (isNormalBlock(level, pos.getPos()))
            return false;
        NPC record = BlockPartyDB.NPCs.find(get(level, pos.getPos()));
        record.update((row) -> row.get(NPC.HIDING).set(false));
        List<MoeInHiding> moesInHiding = level.getEntitiesOfClass(MoeInHiding.class, pos.getAABB());
        if (moesInHiding.isEmpty())
            return false;
        MoeInHiding moeInHiding = moesInHiding.get(0);
        return moeInHiding.spawn();
    }

    @SubscribeEvent
    public static void onBreakStart(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getLevel() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }

    @SubscribeEvent
    public static void onBreakEnd(BlockEvent.BreakEvent e) {
        if (e.getLevel() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }

    @SubscribeEvent
    public static void onPistonPush(PistonEvent.Pre e) {
        if (e.getLevel() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }

    @SubscribeEvent
    public static void onFalling(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof FallingBlockEntity entity) {
            if (entity.level().isClientSide())
                return;
            ServerLevel level = (ServerLevel) entity.level();
            BlockPos pos = entity.getStartPos();
            if (spawn(level, new DimBlockPos(level.dimension(), pos)))
                e.setCanceled(true);
        }
    }
}
