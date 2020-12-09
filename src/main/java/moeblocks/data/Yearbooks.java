package moeblocks.data;

import moeblocks.data.dating.Relationship;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.BloodTypes;
import moeblocks.entity.ai.automata.state.Deres;
import moeblocks.entity.ai.automata.state.Emotions;
import moeblocks.init.MoeEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

public class Yearbooks extends WorldSavedData {
    private static final String KEY = "yearbooks";
    private final Map<UUID, BlockPos> cells = new HashMap<>();
    private final Map<UUID, Book> books = new HashMap<>();

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT books = compound.getCompound("Books");
        books.keySet().forEach(key -> this.books.put(UUID.fromString(key), new Book(this, (CompoundNBT) books.get(key))));
        CompoundNBT cells = compound.getCompound("Cells");
        cells.keySet().forEach(key -> this.cells.put(UUID.fromString(key), BlockPos.fromLong(cells.getLong(key))));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT books = new CompoundNBT();
        this.books.keySet().forEach(key -> books.put(key.toString(), this.books.get(key).write()));
        compound.put("Books", books);
        CompoundNBT cells = new CompoundNBT();
        this.cells.keySet().forEach(key -> cells.putLong(key.toString(), this.cells.get(key).toLong()));
        compound.put("Cells", cells);
        return compound;
    }

    public BlockPos get(UUID uuid) {
        if (!this.cells.containsKey(uuid)) { return BlockPos.ZERO; }
        return this.cells.get(uuid);
    }

    public void set(UUID uuid, Book book) {
        this.books.put(uuid, book);
        this.markDirty();
    }

    public static void sync(AbstractNPCEntity entity) {
        Yearbooks instance = getInstance(entity.world);
        if (instance != null) {
            instance.books.forEach((uuid, book) -> book.setPageCautiously(entity, uuid));
            instance.cells.put(entity.getUniqueID(), entity.getPosition());
            instance.markDirty();
        }
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

    public static class Page {
        private final CompoundNBT character;
        private final UUID uuid;

        public Page(INBT compound) {
            this.character = (CompoundNBT) compound;
            this.uuid = this.character.getUniqueId("PageUUID");
        }

        public Page(AbstractNPCEntity entity, UUID uuid) {
            entity.setYearbookPage(this.character = new CompoundNBT(), uuid);
            this.uuid = entity.getUniqueID();
        }

        public AbstractNPCEntity getCharacter(Minecraft minecraft) {
            AbstractNPCEntity character = MoeEntities.MOE.get().create(minecraft.world);
            character.readAdditional(this.character);
            character.setPosition(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
            if (this.getDere() == Deres.YANDERE) { character.setEmotion(Emotions.PSYCHOTIC, 0); }
            if (this.getDere() == Deres.DEREDERE) { character.setEmotion(Emotions.HAPPY, 0); }
            character.isInYearbook = true;
            return character;
        }

        public Deres getDere() {
            return Deres.valueOf(this.character.getString("Dere"));
        }

        public String getName(AbstractNPCEntity character) {
            return character.getFullName();
        }

        public CompoundNBT write() {
            this.character.putUniqueId("PageUUID", this.uuid);
            return this.character;
        }

        public int getAge() {
            return this.character.getInt("AgeInYears");
        }

        public BloodTypes getBloodType() {
            return BloodTypes.valueOf(this.character.getString("BloodType"));
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

        public Relationship.Status getStatus() {
            return Relationship.Status.valueOf(this.character.getString("Status"));
        }

        public float getStress() {
            return this.character.getFloat("Stress");
        }

        public UUID getUUID() {
            return this.uuid;
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

        public Page getPage(int pageNumber) {
            return this.pages.get(pageNumber);
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

        public Page getPage(UUID uuid) {
            return this.pages.stream().filter(page -> page.getUUID().equals(uuid)).findFirst().orElse(null);
        }

        public void setDirty(UUID uuid) {
            this.data.set(uuid, this);
        }

        public int setPageIgnorantly(AbstractNPCEntity entity, UUID uuid) {
            if (!this.setPageCautiously(entity, uuid)) { this.pages.add(new Page(entity, uuid)); }
            this.setDirty(uuid);
            return this.getPageNumber(entity.getUniqueID());
        }

        public boolean setPageCautiously(AbstractNPCEntity entity, UUID uuid) {
            Page page = this.getPage(entity.getUniqueID());
            if (page == null) { return false; }
            this.pages.set(this.pages.indexOf(page), new Page(entity, uuid));
            this.setDirty(uuid);
            return true;
        }

        public int getPageNumber(UUID uuid) {
            Page page = this.getPage(uuid);
            return this.pages.indexOf(page);
        }

        public CompoundNBT write() {
            CompoundNBT compound = new CompoundNBT();
            ListNBT pages = new ListNBT();
            this.pages.forEach(page -> pages.add(page.write()));
            compound.put("Pages", pages);
            return compound;
        }

        public List<Page> getPages() {
            return this.pages;
        }

        public boolean isEmpty() {
            return this.getPageCount() == 0;
        }

        public int getPageCount() {
            return this.pages.size();
        }
    }
}
