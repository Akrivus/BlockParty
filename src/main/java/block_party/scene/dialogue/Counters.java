package block_party.scene.dialogue;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Map;

public class Counters {
    private final Map<String, Integer> counters = Maps.newHashMap();

    public Counters() { }

    public Counters(CompoundTag compound) {
        compound.getList("Counters", NBT.COMPOUND).forEach((element) -> {
            CompoundTag counter = (CompoundTag) element;
            String key = counter.getString("Key");
            this.counters.put(key, counter.getInt("Value"));
        });
    }

    public CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        this.counters.forEach((key, value) -> {
            CompoundTag element = new CompoundTag();
            element.putString("Key", key);
            element.putInt("Value", value);
        });
        compound.put("Counters", list);
        return compound;
    }

    public CompoundTag save() {
        return this.save(new CompoundTag());
    }
}
