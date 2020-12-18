package moeblocks.datingsim;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DatingSim {
    public final Map<UUID, CacheNPC> characters = new LinkedHashMap<>();
    private final UUID uuid;

    public DatingSim(UUID uuid) {
        this.uuid = uuid;
    }

    public DatingSim(CompoundNBT compound) {
        this(compound.getUniqueId("UUID"));
        ListNBT npcs = compound.getList("NPCs", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < npcs.size(); ++i) {
            CompoundNBT nbt = npcs.getCompound(i);
            this.characters.put(nbt.getUniqueId("UUID"), new CacheNPC(nbt));
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        ListNBT npcs = new ListNBT();
        this.characters.forEach((uuid, npc) -> npcs.add(npc.write(new CompoundNBT())));
        compound.put("NPCs", npcs);
        compound.putUniqueId("UUID", this.uuid);
        return compound;
    }

    public CacheNPC getNPC(UUID uuid) {
        AtomicReference<CacheNPC> npc = new AtomicReference<>();
        this.characters.forEach((key, value) -> {
            if (key.equals(uuid)) { npc.set(value); }
        });
        return npc.get();
    }

    public CacheNPC getNPC(UUID uuid, AbstractNPCEntity entity) {
        CacheNPC npc = getNPC(uuid);
        if (npc == null) { npc = new CacheNPC(entity); }
        this.characters.put(uuid, npc);
        return npc;
    }


    public CacheNPC removeNPC(UUID uuid) {
        return this.characters.remove(uuid);
    }

    public int totalNPCs() {
        return this.characters.size();
    }

    public boolean isNPCsEmpty() {
        return this.characters.isEmpty();
    }
}
