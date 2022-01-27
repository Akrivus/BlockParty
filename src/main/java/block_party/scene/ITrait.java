package block_party.scene;

import block_party.npc.BlockPartyNPC;
import block_party.utils.Trans;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ITrait<T extends ITrait> extends ISceneFilter {
    default boolean verify(BlockPartyNPC npc) {
        return this.isSharedWith(npc);
    }

    boolean isSharedWith(BlockPartyNPC npc);

    default String getString() {
        return Trans.late(this.getTranslationKey());
    }

    default String getTranslationKey() {
        return String.format("trait.block_party.%s.%s", this.getKey().toLowerCase(), this.getValue().toLowerCase());
    }

    String getValue();

    default String getKey() {
        return this.getClass().getSimpleName();
    }

    default T read(CompoundTag compound) {
        return this.fromValue(compound.getString(this.getKey()));
    }

    T fromValue(String key);

    default T fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }

    default void write(CompoundTag compound) {
        compound.putString(this.getKey(), this.getValue());
    }
}
