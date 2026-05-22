package block_party.scene.data;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Map;

public abstract class AbstractVariables<T> {
    protected final Map<String, T> map;

    public AbstractVariables() {
        this.map = Maps.newHashMap();
    }

    public AbstractVariables(CompoundTag compound) {
        this();
        this.read(compound);
    }

    public abstract T read(CompoundTag compound);

    public abstract CompoundTag write(CompoundTag compound, T value);

    public abstract String getKey();

    public void load(CompoundTag compound) {
        compound.getList(this.getKey(), NBT.COMPOUND).forEach((member) -> {
            CompoundTag element = (CompoundTag) member;
            String key = element.getString("Key");
            this.set(key, this.read(element));
        });
    }

    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        this.map.forEach((key, value) -> {
            CompoundTag element = new CompoundTag();
            element.putString("Key", key);
            list.add(this.write(compound, value));
        });
        compound.put(this.getKey(), list);
        return compound;
    }

    public CompoundTag save() {
        return this.save(new CompoundTag());
    }

    public void set(String key, T value) {
        this.map.put(key, value);
    }

    public void delete(String key) {
        this.map.remove(key);
    }

    public T get(String key) {
        return this.map.get(key);
    }

    public boolean has(String key) {
        return this.get(key) != null;
    }
}
