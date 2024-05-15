package block_party.entities.abstraction;

import block_party.entities.BlockPartyNPC;
import block_party.utils.NBT;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/**
 * Abstraction layer 6: inventory and menu handling.
 */
public abstract class Layer6 extends Layer5 implements ContainerListener, MenuProvider {
    public final SimpleContainer inventory = new SimpleContainer(36);

    public Layer6(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Inventory", this.inventory.createTag());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.inventory.fromTag(compound.getList("Inventory", NBT.COMPOUND));
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void dropEquipment() {
        for (int slot = 0; slot < 36; ++slot) {
            ItemStack stack = this.inventory.getItem(slot);
            if (stack.isEmpty()) { continue; }
            this.spawnAtLocation(stack);
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource cause, boolean player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (stack.isEmpty()) { continue; }
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x3, id, inventory, this.inventory, 3);
    }

    @Override
    public void containerChanged(Container inventory) {
        this.setSlouch(this.recalcSlouch());
    }

    public boolean openChestFor(Player player) {
        player.openMenu(this);
        return true;
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
    }

    public boolean isBeingLookedThrough() {
        if (!this.isPlayerBusy()) { return false; }
        AbstractContainerMenu container = this.getPlayer().containerMenu;
        if (container instanceof ChestMenu) {
            Container inventory = ((ChestMenu) container).getContainer();
            return inventory.equals(this.inventory);
        }
        return false;
    }

    public float recalcSlouch() {
        float size = 0.0F;
        for (int i = 0; i < 36; ++i) {
            if (inventory.getItem(i).isEmpty()) { continue; }
            size += 0.0277777778F;
        }
        return size;
    }
}
