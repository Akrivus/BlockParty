package block_party.entities.chores;

import block_party.entities.Moe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class ChoreScheduler {
    private final Moe moe;
    private MoeChore active = NoChore.INSTANCE;

    public ChoreScheduler(Moe moe) {
        this.moe = moe;
    }

    public void read(CompoundTag compound) {
        this.active = ChoreTypes.readSaved(compound);
    }

    public void write(CompoundTag compound) {
        ChoreTypes.writeSaved(compound, this.active);
    }

    public void start(MoeChore chore) {
        this.clear();
        this.active = chore == null ? NoChore.INSTANCE : chore;
        this.active.start(this.moe);
    }

    public boolean hasActive(ResourceLocation id) {
        return this.active.active() && this.active.id().equals(id);
    }

    public boolean canRunActive() {
        return this.active.active() && this.active.canUse(this.moe);
    }

    public boolean tickActive() {
        if (!this.active.active()) {
            this.clear();
            return false;
        }
        boolean handled = this.active.tick(this.moe);
        if (!this.active.active()) {
            this.clear();
        }
        return handled;
    }

    public void clear() {
        if (this.active.active()) {
            this.active.stop(this.moe);
        }
        this.active = NoChore.INSTANCE;
    }
}
