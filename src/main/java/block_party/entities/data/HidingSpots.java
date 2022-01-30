package block_party.entities.data;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.scene.SceneTrigger;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        if (moe.level instanceof ServerLevel level)
            set(level, (spot) -> spot.list.remove(moe.blockPosition(), moe.getDatabaseID()));
    }

    public static void add(Moe moe) {
        if (moe.level instanceof ServerLevel level)
            set(level, (spot) -> spot.list.put(moe.blockPosition(), moe.getDatabaseID()));
    }

    private static HidingSpots get(ServerLevel level) {
        HidingSpots spot = level.getDataStorage().get(HidingSpots::new, KEY);
        if (spot == null) { spot = new HidingSpots(); }
        return spot;
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

    private static void spawn(ServerLevel level, DimBlockPos pos) {
        NPC record = BlockPartyDB.NPCs.find(get(level, pos.getPos()));
        record.update((row) -> row.get(NPC.HIDING).set(false));
        List<MoeInHiding> npcs = level.getEntitiesOfClass(MoeInHiding.class, pos.getAABB());
        for (MoeInHiding npc : npcs) {
            Moe moe = new Moe(level);
            moe.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            moe.sceneManager.trigger(SceneTrigger.HIDING_SPOT_DISCOVERED);
            npc.getRow().load(moe);
            moe.setBlockState(level.getBlockState(pos));
            if (moe.getActualBlockState().hasBlockEntity())
                moe.setTileEntityData(level.getBlockEntity(pos).getTileData());
            level.destroyBlock(pos, false);
            if (level.addFreshEntity(moe)) { npc.setRemoved(Entity.RemovalReason.DISCARDED); }
            return;
        }
    }

    @SubscribeEvent
    public static void onBlockBreaking(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getWorld() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        if (e.getWorld() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }

    @SubscribeEvent
    public static void onBlockMove(PistonEvent.Pre e) {
        if (e.getWorld() instanceof ServerLevel level)
            spawn(level, new DimBlockPos(level.dimension(), e.getPos()));
    }
}
