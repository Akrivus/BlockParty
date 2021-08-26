package block_party.mob.automata;

import block_party.util.Trans;
import net.minecraft.nbt.CompoundTag;

public interface ITrait<T extends ITrait> extends ICondition {
    default String getString() {
        return Trans.late(this.getTranslationKey());
    }

    default String getTranslationKey() {
        return String.format("characteristic.block_party.%s.%s", this.getKey().toLowerCase(), this.getValue().toLowerCase());
    }

    String getValue();

    default String getKey() {
        return this.getClass().getSimpleName();
    }

    default T read(CompoundTag compound) {
        return this.fromValue(compound.getString(this.getKey()));
    }

    T fromValue(String key);

    default void write(CompoundTag compound) {
        compound.putString(this.getKey(), this.getValue());
    }

    default int getTimeout() {
        return 1000;
    }
}
