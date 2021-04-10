package moeblocks.automata;

import moeblocks.util.Trans;
import net.minecraft.nbt.CompoundNBT;

public interface ITrait<T extends ITrait> extends ICondition {
    default String getString() {
        return Trans.late(this.getTranslationKey());
    }

    default String getTranslationKey() {
        return String.format("debug.moeblocks.%s.%s", this.getKey().toLowerCase(), this.getValue().toLowerCase());
    }

    String getValue();

    default String getKey() {
        return this.getClass().getSimpleName();
    }

    default T read(CompoundNBT compound) {
        return this.fromValue(compound.getString(this.getKey()));
    }

    T fromValue(String key);

    default void write(CompoundNBT compound) {
        compound.putString(this.getKey(), this.getValue());
    }

    default int getTimeout() {
        return 1000;
    }
}
