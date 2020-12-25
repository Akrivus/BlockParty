package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class CacheNPC {
    public static final Map<UUID, BlockPos> positions = new HashMap<>();
    private final UUID uuid;
    private BlockPos pos;
    private String name;
    private CompoundNBT tag;
    private boolean dead;
    private boolean estranged;

    public CacheNPC(AbstractNPCEntity npc) {
        this.uuid = npc.getUniqueID();
        this.sync(npc);
    }

    public void sync(AbstractNPCEntity npc) {
        this.setPosition(npc.getPosition());
        this.setName(npc.getFullName());
        this.setTag(npc);
        this.setDead(false);
        this.setEstranged(false);
    }

    public CacheNPC(CompoundNBT compound) {
        this.uuid = compound.getUniqueId("UUID");
        this.setPosition(BlockPos.fromLong(compound.getLong("Position")));
        this.setName(compound.getString("Name"));
        this.setTag(compound.getCompound("NPC"));
        this.setDead(compound.getBoolean("Dead"));
        this.setEstranged(compound.getBoolean("Estranged"));
    }

    public AbstractNPCEntity get(Minecraft minecraft, EntityType<? extends AbstractNPCEntity> type) {
        AbstractNPCEntity entity = type.create(minecraft.world);
        entity.setPosition(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
        entity.readCharacter(this.getTag());
        return entity;
    }

    public void set(DatingData data, Consumer<CacheNPC> transaction) {
        transaction.accept(this);
        data.markDirty();
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("UUID", this.uuid);
        compound.putLong("Position", this.pos.toLong());
        compound.putString("Name", this.name);
        compound.put("NPC", this.tag);
        compound.putBoolean("Dead", this.dead);
        compound.putBoolean("Estranged", this.estranged);
        return compound;
    }

    public String getName() {
        return this.name;
    }    public CompoundNBT getTag() {
        return this.tag;
    }

    public void setName(String name) {
        this.name = name;
    }    public void setTag(CompoundNBT compound) {
        this.tag = compound;
    }

    public BlockPos getPosition() {
        return this.pos;
    }    public void setTag(AbstractNPCEntity npc) {
        this.setTag(new CompoundNBT());
        npc.writeUnlessPassenger(this.getTag());
    }

    public void setPosition(BlockPos pos) {
        CacheNPC.positions.put(this.uuid, this.pos = pos);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean isDead() {
        return this.dead;
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






}
