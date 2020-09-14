package moe.blocks.mod.data;

import moe.blocks.mod.data.yearbook.Book;
import moe.blocks.mod.entity.partial.CharacterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Yearbooks extends WorldSavedData {
    private static final String KEY = "yearbooks";
    private final Map<UUID, Book> books = new HashMap<>();

    public static void sync(CharacterEntity entity) {
        getInstance(entity.world).books.forEach((uuid, book) -> book.setPageCautiously(entity, uuid));
    }

    public static Yearbooks getInstance(World world) {
        if (world.isRemote()) { return null; }
        ServerWorld server = ((ServerWorld) world).getServer().getWorld(World.field_234918_g_);
        DimensionSavedDataManager storage = server.getSavedData();
        return storage.getOrCreate(Yearbooks::new, KEY);
    }

    public Yearbooks() {
        this(KEY);
    }

    public Yearbooks(String name) {
        super(name);
    }

    public static Book get(PlayerEntity player) {
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
        compound.keySet().forEach(key -> this.books.put(UUID.fromString(key), new Book(this, compound.get(key))));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        this.books.keySet().forEach(key -> compound.put(key.toString(), this.books.get(key).write()));
        return compound;
    }
}
