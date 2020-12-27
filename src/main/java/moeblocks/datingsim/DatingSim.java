package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class DatingSim {
    public final Map<UUID, CacheNPC> characters = new LinkedHashMap<>();
    private final UUID uuid;
    
    public DatingSim(CompoundNBT compound) {
        this(compound.getUniqueId("UUID"));
        ListNBT npcs = compound.getList("NPCs", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < npcs.size(); ++i) {
            CompoundNBT nbt = npcs.getCompound(i);
            this.characters.put(nbt.getUniqueId("UUID"), new CacheNPC(nbt));
        }
    }
    
    public DatingSim(UUID uuid) {
        this.uuid = uuid;
    }
    
    public CacheNPC getNPC(UUID uuid, AbstractNPCEntity entity) {
        CacheNPC npc = getNPC(uuid);
        if (npc == null) { npc = new CacheNPC(entity); }
        this.characters.put(uuid, npc);
        return npc;
    }
    
    public CacheNPC getNPC(UUID uuid) {
        return this.characters.get(uuid);
    }
    
    public CacheNPC removeNPC(UUID uuid) {
        return this.characters.remove(uuid);
    }
    
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT npcs = new ListNBT();
        this.characters.forEach((uuid, npc) -> npcs.add(npc.write(new CompoundNBT())));
        compound.put("NPCs", npcs);
        compound.putUniqueId("UUID", this.uuid);
        return compound;
    }
    
    public List<UUID> getNPCs() {
        List<UUID> npcs = new ArrayList<>();
        this.characters.forEach((npc, data) -> npcs.add(npc));
        return npcs;
    }
}
