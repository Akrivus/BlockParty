package moeblocks.entity.ai.items;

import moeblocks.entity.MoeEntity;
import moeblocks.entity.ai.AbstractMoveToBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.math.BlockPos;

public class DumpChestGoal extends AbstractMoveToBlockGoal<MoeEntity> {
    
    public DumpChestGoal(MoeEntity entity) {
        super(entity, 3, 8);
        this.timeUntilNextMove = 12000;
    }
    
    @Override
    public int getPriority() {
        return 0x8;
    }
    
    @Override
    public void onArrival() {
        if (!this.canMoveTo(this.pos, this.state)) { return; }
        IInventory chest = HopperTileEntity.getInventoryAtPosition(this.world, this.pos);
        Inventory inventory = this.entity.getInventory();
        for (int x = 0; x < inventory.getSizeInventory(); ++x) {
            ItemStack bra = inventory.getStackInSlot(x);
            if (bra.isEmpty()) { continue; }
            for (int y = 0; y < chest.getSizeInventory(); ++y) {
                ItemStack stack = chest.getStackInSlot(y);
                if (stack.getItem() == bra.getItem() || stack.isEmpty()) {
                    ItemStack contents = new ItemStack(bra.getItem(), stack.getCount() + bra.getCount());
                    chest.setInventorySlotContents(y, contents);
                    bra.setCount(contents.getMaxStackSize() - contents.getCount());
                    inventory.setInventorySlotContents(x, bra);
                    break;
                }
            }
        }
    }
    
    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        IInventory inv = HopperTileEntity.getInventoryAtPosition(this.world, pos);
        if (inv == null) { return false; }
        for (int x = 0; x < this.entity.getInventory().getSizeInventory(); ++x) {
            ItemStack bra = this.entity.getInventory().getStackInSlot(x);
            if (bra.isEmpty()) { continue; }
            boolean flag = false;
            for (int y = 0; y < inv.getSizeInventory(); ++y) {
                ItemStack stack = inv.getStackInSlot(y);
                flag |= stack.isEmpty();
                if (stack.getItem() == bra.getItem() && ItemStack.areItemStackTagsEqual(stack, bra)) {
                    flag |= stack.getCount() + bra.getCount() <= stack.getMaxStackSize();
                }
            }
            if (flag) { return true; }
        }
        return false;
    }
}
