package block_party.scene;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Map;

public class Cookies {
    private final Map<String, String> cookies = Maps.newHashMap();

    public Cookies() { }

    public Cookies(CompoundTag compound) {
        compound.getList("Cookies", NBT.COMPOUND).forEach((element) -> {
            CompoundTag counter = (CompoundTag) element;
            String key = counter.getString("Key");
            this.cookies.put(key, counter.getString("Value"));
        });
    }

    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        this.cookies.forEach((key, value) -> {
            CompoundTag element = new CompoundTag();
            element.putString("Key", key);
            element.putString("Value", value);
            list.add(element);
        });
        compound.put("Cookies", list);
        return compound;
    }

    public CompoundTag save() {
        return this.save(new CompoundTag());
    }

    public boolean has(String cookie) {
        return this.cookies.containsKey(cookie);
    }

    public String get(String cookie) {
        return this.cookies.get(cookie);
    }

    public void add(String cookie, String value) {
        this.cookies.put(cookie, value);
    }

    public void add(String cookie) {
        this.add(cookie, "");
    }

    public void delete(String cookie) {
        this.cookies.remove(cookie);
    }
}
