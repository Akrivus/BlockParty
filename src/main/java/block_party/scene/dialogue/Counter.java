package block_party.scene.dialogue;

import net.minecraft.nbt.CompoundTag;

public class Counter {
    private final String key;
    private int value;

    public Counter(CompoundTag compound) {
        this.key = compound.getString("Key");
        this.value = compound.getInt("Value");
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putString("Key", this.key);
        compound.putInt("Value", this.value);
        return compound;
    }
}
