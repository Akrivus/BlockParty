package block_party.entities.chores;

import block_party.entities.Moe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface MoeChore {
    ResourceLocation id();

    boolean active();

    boolean canUse(Moe moe);

    void start(Moe moe);

    boolean tick(Moe moe);

    void stop(Moe moe);

    CompoundTag write();
}
