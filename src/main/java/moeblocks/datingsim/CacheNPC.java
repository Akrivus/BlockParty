package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class CacheNPC {
    public static final Map<UUID, BlockPos> positions = new HashMap<>();
    protected final UUID uuid;
    protected BlockPos pos;
    protected String name;
    protected CompoundNBT tag;
    protected boolean dead;

    public CacheNPC(AbstractNPCEntity npc) {
        this.uuid = npc.getUniqueID();
        this.sync(npc);
    }

    public CacheNPC(CompoundNBT compound) {
        this.uuid = compound.getUniqueId("UUID");
        this.setPosition(BlockPos.fromLong(compound.getLong("Position")));
        this.setName(compound.getString("Name"));
        this.setTag(compound.getCompound("NPC"));
        this.setDead(compound.getBoolean("Dead"));
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("UUID", this.uuid);
        compound.putLong("Position", this.pos.toLong());
        compound.putString("Name", this.name);
        compound.put("NPC", this.tag);
        compound.putBoolean("Dead", this.dead);
        return compound;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public void setPosition(BlockPos pos) {
        CacheNPC.positions.put(this.uuid, this.pos = pos);
    }

    public CompoundNBT getTag() {
        return this.tag;
    }

    public void setTag(CompoundNBT compound) {
        this.tag = compound;
    }

    public void setTag(AbstractNPCEntity npc) {
        this.setTag(new CompoundNBT());
        npc.writeUnlessPassenger(this.getTag());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDead() {
        return this.dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void sync(AbstractNPCEntity npc) {
        this.setPosition(npc.getPosition());
        this.setName(npc.getFullName());
        this.setTag(npc);
        this.setDead(false);
    }

    public AbstractNPCEntity get(World world, EntityType<? extends AbstractNPCEntity> type) {
        AbstractNPCEntity entity = type.create(world);
        entity.setPosition(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        entity.readCharacter(this.getTag());
        return entity;
    }

    public void set(DatingData data, Consumer<CacheNPC> transaction) {
        transaction.accept(this);
        data.markDirty();
    }
}
