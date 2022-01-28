package block_party.entities.data;

import block_party.entities.Moe;
import block_party.scene.SceneTrigger;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Map;

public class HidingSpots extends SavedData {
    public static String KEY = "blockparty_hidingspots";
    public Map<BlockPos, Long> list = Maps.newHashMap();

    public HidingSpots(CompoundTag compound) {
        this();
        ListTag list = compound.getList("HidingSpots", NBT.COMPOUND);
        list.forEach((element) -> {
            CompoundTag member = (CompoundTag) element;
            BlockPos pos = BlockPos.of(member.getLong("BlockPos"));
            long id = member.getLong("DatabaseID");
            this.list.put(pos, id);
        });
    }

    public HidingSpots() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onBlockBreak);
        bus.addListener(this::onBlockMove);
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

    public void onBlockBreak(BlockEvent.BreakEvent e) {
        BlockPos pos = e.getPos();
        if (this.list.get(pos) == null)
            return;
        LevelAccessor level = e.getWorld();
        level.addFreshEntity(this.getNewMoe(level, pos, this.list.get(pos)));
        e.setCanceled(true);
    }

    public void onBlockMove(PistonEvent.Pre e) {
        BlockPos pos = e.getPos();
        if (this.list.get(pos) == null)
            return;
        LevelAccessor level = e.getWorld();
        level.addFreshEntity(this.getNewMoe(level, pos, this.list.get(pos)));
        e.setCanceled(true);
    }

    public Moe getNewMoe(LevelAccessor level, BlockPos pos, long id) {
        BlockState state = level.getBlockState(pos);
        BlockEntity extra = level.getBlockEntity(pos);
        Moe moe = new Moe((Level) level);
        moe.setDatabaseID(id);
        moe.getRow().load(moe);
        moe.setBlockState(state);
        moe.setTileEntityData(extra != null ? extra.getTileData() : new CompoundTag());
        moe.absMoveTo(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
        moe.sceneManager.trigger(SceneTrigger.HIDING_SPOT_DISCOVERED);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        return moe;
    }

    public static HidingSpots get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(HidingSpots::new, HidingSpots::new, KEY);
    }

    public static void add(Moe moe) {
        HidingSpots places = HidingSpots.get((ServerLevel) moe.level);
        places.list.put(moe.getBlockPos(), moe.getDatabaseID());
        places.setDirty();
    }
}
