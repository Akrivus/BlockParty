package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatingSim {
    public final List<CacheNPC> characters = new ArrayList<>();

    public DatingSim(CompoundNBT compound) {
        ListNBT npcs = compound.getList("NPCs", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < npcs.size(); ++i) {
            this.characters.add(new CacheNPC(npcs.getCompound(i)));
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        ListNBT npcs = new ListNBT();
        this.characters.forEach((npc) -> npcs.add(npc.getTag()));
        compound.put("NPCs", npcs);
        return compound;
    }

    public Optional<CacheNPC> getNPC(UUID uuid) {
        return this.characters.stream().filter(npc -> npc.getUUID().equals(uuid)).findFirst();
    }

    public CacheNPC getNPC(UUID uuid, AbstractNPCEntity entity) {
        Optional<CacheNPC> _npc = this.getNPC(uuid);
        if (_npc.isPresent()) { return _npc.get(); }
        CacheNPC npc = new CacheNPC(entity);
        this.characters.add(npc);
        return npc;
    }

    public int getI(UUID uuid) {
        Optional<CacheNPC> _npc = this.getNPC(uuid);
        if (_npc.isPresent()) { return this.characters.indexOf(_npc.get()); }
        return -1;
    }

    public CacheNPC get(int index) {
        return this.characters.get(index);
    }

    public CacheNPC remove(UUID uuid) {
        return this.characters.remove(this.getI(uuid));
    }

    public int size() {
        return this.characters.size();
    }

    public boolean isEmpty() {
        return this.characters.isEmpty();
    }
}
