package block_party.scene.data;

import block_party.utils.NBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractVariables<T> {
    private final Map<String, T> map = new HashMap<>();

    protected AbstractVariables() {
    }

    protected AbstractVariables(CompoundTag compound) {
        this.load(compound);
    }

    protected abstract T read(CompoundTag compound);

    protected abstract void write(CompoundTag compound, T value);

    protected abstract String getKey();

    private void load(CompoundTag compound) {
        compound.getList(this.getKey(), NBT.COMPOUND).forEach(member -> {
            CompoundTag element = (CompoundTag) member;
            this.set(element.getString("Key"), this.read(element));
        });
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        this.map.forEach((key, value) -> {
            CompoundTag element = new CompoundTag();
            element.putString("Key", key);
            this.write(element, value);
            list.add(element);
        });
        compound.put(this.getKey(), list);
        return compound;
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
