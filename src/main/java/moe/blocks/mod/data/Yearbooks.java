package moe.blocks.mod.data;

import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.init.MoeEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

public class Yearbooks extends WorldSavedData {
    private static final String KEY = "yearbooks";
    private final Map<UUID, Book> books = new HashMap<>();

    public static void sync(CharacterEntity entity) {
        getInstance(entity.world).books.forEach((uuid, book) -> book.setPageCautiously(entity, uuid));
    }

    public static Yearbooks getInstance(World world) {
        if (world.isRemote()) { return null; }
        ServerWorld server = ((ServerWorld) world).getServer().getWorld(World.OVERWORLD);
        DimensionSavedDataManager storage = server.getSavedData();
        return storage.getOrCreate(Yearbooks::new, KEY);
    }

    public Yearbooks() {
        this(KEY);
    }

    public Yearbooks(String name) {
        super(name);
    }

    public static Book getBook(PlayerEntity player) {
        Yearbooks data = getInstance(player.world);
        if (data != null) { return data.books.getOrDefault(player.getUniqueID(), new Book(data)); }
        return null;
    }

    public void set(UUID uuid, Book book) {
        this.books.put(uuid, book);
        this.markDirty();
    }

    @Override
    public void read(CompoundNBT compound) {
        compound.keySet().forEach(key -> this.books.put(UUID.fromString(key), new Book(this, (CompoundNBT) compound.get(key))));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        this.books.keySet().forEach(key -> compound.put(key.toString(), this.books.get(key).write()));
        return compound;
    }

    public static class Page {
        private final CompoundNBT character;
        private final UUID uuid;

        public Page(INBT compound) {
            this.character = (CompoundNBT) compound;
            this.uuid = this.character.getUniqueId("PageUUID");
        }

        public Page(CharacterEntity entity, UUID uuid) {
            entity.setYearbookPage(this.character = new CompoundNBT(), uuid);
            this.uuid = entity.getUniqueID();
        }

        public CompoundNBT write() {
            this.character.putUniqueId("PageUUID", this.uuid);
            return this.character;
        }

        public CharacterEntity getCharacter(Minecraft minecraft) {
            CharacterEntity character = MoeEntities.MOE.get().create(minecraft.world);
            character.readAdditional(this.character);
            character.setPosition(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
            character.rotationYaw = 0.75F * -(character.rotationYawHead = 180.0F);
            if (this.getDere() == Deres.YANDERE) { character.setEmotion(Emotions.PSYCHOTIC, 0); }
            if (this.getDere() == Deres.DEREDERE) { character.setEmotion(Emotions.HAPPY, 0); }
            character.isInYearbook = true;
            return character;
        }

        public Deres getDere() {
            return Deres.valueOf(this.character.getString("Dere"));
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName(CharacterEntity character) {
            return character.getFullName();
        }

        public float getHealth() {
            return this.character.getFloat("Health");
        }

        public float getHunger() {
            return this.character.getFloat("Hunger");
        }

        public float getLove() {
            return this.character.getFloat("Love");
        }

        public float getStress() {
            return this.character.getFloat("Stress");
        }

        public Relationship.Status getStatus() {
            return Relationship.Status.valueOf(this.character.getString("Status"));
        }

        public BloodTypes getBloodType() {
            return BloodTypes.valueOf(this.character.getString("BloodType"));
        }

        public int getAge() {
            return this.character.getInt("AgeInYears");
        }
    }

    public static class Book {
        private final List<Page> pages = new ArrayList<>();
        private final Yearbooks data;

        public Book(Yearbooks data) {
            this(data, new CompoundNBT());
        }

        public Book(Yearbooks data, CompoundNBT compound) {
            this.data = data;
            ListNBT list = compound.getList("Pages", 10);
            list.forEach(nbt -> {
                this.pages.add(new Page(nbt));
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

        public void setPageIgnorantly(CharacterEntity entity, UUID uuid) {
            if (this.setPageCautiously(entity, uuid)) { return; }
            this.pages.add(new Page(entity, uuid));
            this.setDirty(uuid);
        }

        public boolean setPageCautiously(CharacterEntity entity, UUID uuid) {
            Page page = this.getPage(entity.getUniqueID());
            if (page == null) { return false; }
            this.pages.set(this.pages.indexOf(page), new Page(entity, uuid));
            this.data.set(uuid, this);
            return true;
        }

        public Page getPage(UUID uuid) {
            return this.pages.stream().filter(page -> page.getUUID().equals(uuid)).findFirst().orElse(null);
        }

        public void setDirty(UUID uuid) {
            this.data.set(uuid, this);
        }

        public Page removePage(UUID pageUUID, PlayerEntity player) {
            return this.removePage(pageUUID, player.getUniqueID());
        }

        public Page removePage(UUID pageUUID, UUID uuid) {
            Page page = this.getPage(pageUUID);
            if (page == null) { return null; }
            this.pages.remove(page);
            this.setDirty(uuid);
            return page;
        }

        public List<Page> getPages() {
            return this.pages;
        }

        public int getPageNumber(UUID uuid) {
            Page page = this.getPage(uuid);
            return this.pages.indexOf(page);
        }

        public boolean isEmpty() {
            return this.getPageCount() == 0;
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public Page getPage(int pageNumber) {
            return this.pages.get(pageNumber);
        }
    }
}
