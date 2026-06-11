package block_party.entities.inventory;

import block_party.entities.Moe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public final class MoeInventoryMenus {
    private final Moe moe;
    private final SimpleContainer inventory = new SimpleContainer(36);

    public MoeInventoryMenus(Moe moe) {
        this.moe = moe;
        this.inventory.addListener(moe);
    }

    public SimpleContainer container() {
        return this.inventory;
    }

    public void read(ListTag tag) {
        this.inventory.fromTag(tag, this.moe.registryAccess());
    }

    public void write(CompoundTag compound, String key) {
        compound.put(key, this.inventory.createTag(this.moe.registryAccess()));
    }

    public void dropContents(ServerLevel level) {
        for (int slot = 0; slot < this.inventory.getContainerSize(); ++slot) {
            ItemStack stack = this.inventory.getItem(slot);
            if (!stack.isEmpty()) {
                this.moe.spawnAtLocation(level, stack);
            }
        }
    }

    public AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return ChestMenu.threeRows(id, playerInventory, this.inventory);
    }

    public boolean openChestFor(Player player) {
        player.openMenu(this.moe);
        return true;
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
    }

    public boolean isBeingLookedThrough() {
        if (!this.moe.isPlayerBusy()) {
            return false;
        }
        Player player = this.moe.getPlayer();
        return player != null && player.containerMenu instanceof ChestMenu menu && menu.getContainer().equals(this.inventory);
    }

    public float recalcSlouch() {
        float size = 0.0F;
        for (int slot = 0; slot < this.inventory.getContainerSize(); ++slot) {
            if (!this.inventory.getItem(slot).isEmpty()) {
                size += 0.0277777778F;
            }
        }
        return size;
    }
}
