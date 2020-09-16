package moe.blocks.mod.data.yearbook;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

import java.util.*;

public class Book {
    private final List<Page> pages = new ArrayList<>();
    private Yearbooks data;

    public Book(Yearbooks data) {
        this(data, new CompoundNBT());
    }

    public Book(Yearbooks data, CompoundNBT compound) {
        this.data = data;
        ListNBT list = compound.getList("Pages", 10);
        list.forEach(nbt -> {
            this.pages.add(new Page((CompoundNBT) nbt));
        });
    }

    public Book(INBT nbt) {
        this(null, (CompoundNBT) nbt);
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT pages = new ListNBT();
        this.pages.forEach(page -> pages.add(page.write()));
        compound.put("Pages", pages);
        return compound;
    }

    public boolean setPageCautiously(CharacterEntity entity, UUID uuid) {
        Page page = this.getPage(entity.getUniqueID());
        if (page == null) { return false; }
        this.pages.set(this.pages.indexOf(page), new Page(entity, uuid));
        this.data.set(uuid, this);
        return true;
    }

    public void setPageIgnorantly(CharacterEntity entity, UUID uuid) {
        if (this.setPageCautiously(entity, uuid)) { return; }
        this.pages.add(new Page(entity, uuid));
        this.setDirty(uuid);
    }

    public Page removePage(UUID pageUUID, UUID uuid) {
        Page page = this.getPage(pageUUID);
        if (page == null) { return null; }
        this.pages.remove(page);
        this.setDirty(uuid);
        return page;
    }

    public Page removePage(UUID pageUUID, PlayerEntity player) {
        return this.removePage(pageUUID, player.getUniqueID());
    }

    public int getPageCount() {
        return this.pages.size();
    }

    public Page getPage(UUID uuid) {
        return this.pages.stream().filter(page -> page.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public List<Page> getPages() {
        return this.pages;
    }

    public void setDirty(UUID uuid) {
        this.data.set(uuid, this);
    }

    public int getPageNumber(UUID uuid) {
        Page page = this.getPage(uuid);
        return this.pages.indexOf(page);
    }

    public boolean isEmpty() {
        return this.getPageCount() == 0;
    }

    public Page getPage(int pageNumber) {
        return this.pages.get(pageNumber);
    }
}
