package moe.blocks.mod.data.yearbook;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Book {
    private final Map<UUID, Page> pages = new LinkedHashMap<>();
    private Yearbooks data;

    public Book(Yearbooks data) {
        this(data, new CompoundNBT());
    }

    public Book(Yearbooks data, INBT nbt) {
        this.data = data;
        CompoundNBT compound = (CompoundNBT) nbt;
        compound.keySet().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            this.pages.put(uuid, new Page(uuid, compound.get(key)));
        });
    }

    public Book(INBT nbt) {
        this(null, nbt);
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        this.pages.keySet().forEach(key -> compound.put(key.toString(), this.pages.get(key).write()));
        return compound;
    }

    public void setPageCautiously(CharacterEntity entity, UUID uuid) {
        if (this.pages.containsKey(entity.getUniqueID())) { this.setPageIgnorantly(entity, uuid); }
    }

    public void setPageIgnorantly(CharacterEntity entity, UUID uuid) {
        this.pages.put(entity.getUniqueID(), new Page(entity, entity.getRelationshipWith(uuid)));
        this.data.set(uuid, this);
    }

    public void ripPage(CharacterEntity entity, UUID uuid) {
        this.pages.remove(entity.getUniqueID());
        this.data.set(uuid, this);
    }

    public int getPageCount() {
        return this.getPages().length;
    }

    public Page[] getPages() {
        return this.pages.values().toArray(new Page[0]);
    }

    public int getPageNumber(UUID uuid) {
        for (int i = 0; i < this.getPages().length; ++i) {
            Page page = this.getPages()[i];
            if (page.getUUID().equals(uuid)) { return i; }
        }
        return 0;
    }
}
