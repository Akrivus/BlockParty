package block_party.entities.chores;

import block_party.BlockParty;
import block_party.entities.Moe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public enum NoChore implements MoeChore {
    INSTANCE;

    public static final ResourceLocation ID = BlockParty.source("none");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public boolean active() {
        return false;
    }

    @Override
    public boolean canUse(Moe moe) {
        return false;
    }

    @Override
    public void start(Moe moe) {
    }

    @Override
    public boolean tick(Moe moe) {
        return false;
    }

    @Override
    public void stop(Moe moe) {
    }

    @Override
    public CompoundTag write() {
        return new CompoundTag();
    }
}
