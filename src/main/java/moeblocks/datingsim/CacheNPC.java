package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.ChunkScheduler;
import moeblocks.util.DimBlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CacheNPC {
    public static final Map<UUID, DimBlockPos> positions = new HashMap<>();
    private final UUID uuid;
    private DimBlockPos pos;
    private String name;
    private CompoundNBT tag;
    private boolean dead;
    private boolean estranged;
    
    public CacheNPC(AbstractNPCEntity npc) {
        this.uuid = npc.getUUID();
        this.sync(npc);
    }
    
    public void sync(AbstractNPCEntity npc) {
        this.setPosition(new DimBlockPos(npc));
        this.setName(npc.getFullName());
        this.setTag(npc);
        this.setDead(false);
        this.setEstranged(false);
    }
    
    public CacheNPC(CompoundNBT compound) {
        this.uuid = compound.getUniqueId("UUID");
        this.setPosition(new DimBlockPos(compound.getCompound("Position")));
        this.setName(compound.getString("Name"));
        this.setTag(compound.getCompound("NPC"));
        this.setDead(compound.getBoolean("Dead"));
        this.setEstranged(compound.getBoolean("Estranged"));
    }
    
    public AbstractNPCEntity clone(World world, BlockPos pos, EntityType<? extends AbstractNPCEntity> type) {
        AbstractNPCEntity entity = type.create(world);
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        entity.readCharacter(this.getTag());
        return entity;
    }
    
    public AbstractNPCEntity clone(Minecraft minecraft, EntityType<? extends AbstractNPCEntity> type) {
        return this.clone(minecraft.world, minecraft.player.getPosition(), type);
    }
    
    public AbstractNPCEntity get(MinecraftServer server) {
        DimBlockPos coord = CacheNPC.positions.get(this.getUUID());
        if (coord == null) { return null; }
        ServerWorld world = server.getWorld(coord.getDim());
        if (world == null) { return null; }
        List<AbstractNPCEntity> npcs = world.getEntitiesWithinAABB(AbstractNPCEntity.class, coord.getAABB());
        for (AbstractNPCEntity npc : npcs) {
            if (this.getUUID().equals(npc.getUUID())) { return npc; }
        }
        return null;
    }
    
    public void set(DatingData data, Consumer<CacheNPC> transaction) {
        transaction.accept(this);
        data.markDirty();
    }
    
    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("UUID", this.uuid);
        compound.put("Position", this.pos.write());
        compound.putString("Name", this.name);
        compound.put("NPC", this.tag);
        compound.putBoolean("Dead", this.dead);
        compound.putBoolean("Estranged", this.estranged);
        return compound;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CompoundNBT getTag() {
        return this.tag;
    }
    
    public DimBlockPos getPosition() {
        return this.pos;
    }
    
    public void setPosition(DimBlockPos pos) {
        CacheNPC.positions.put(this.uuid, this.pos = pos);
    }
    
    public void setTag(CompoundNBT compound) {
        this.tag = compound;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public boolean isDead() {
        return this.dead;
    }
    
    public void setTag(AbstractNPCEntity npc) {
        this.setTag(new CompoundNBT());
        npc.writeUnlessPassenger(this.getTag());
    }
    
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    
    public boolean isEstranged() {
        return this.estranged;
    }
    
    public void setEstranged(boolean estranged) {
        this.estranged = estranged;
    }
    
    public boolean isRemovable() {
        return this.isDead() || this.isEstranged();
    }
}
