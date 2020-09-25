package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToBlockGoal;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;

public class DumpChestGoal extends AbstractMoveToBlockGoal<CharacterEntity> {

    public DumpChestGoal(CharacterEntity entity) {
        super(entity, 4, 8);
    }

    @Override
    protected boolean isHoldingCorrectItem(ItemStack stack) {
        return this.entity.getHomePosition().withinDistance(this.entity.getPosition(), 8.0F);
    }

    @Override
    public void onArrival() {
        if (!this.canMoveTo(this.pos, this.state)) { return; }
        IInventory inv = HopperTileEntity.getInventoryAtPosition(this.world, this.pos);
        for (int x = 0; x < this.entity.getCupSize().getSize(); ++x) {
            ItemStack bra = this.entity.getBrassiere().getStackInSlot(x);
            if (bra.isEmpty()) { continue; }
            for (int y = 0; y < inv.getSizeInventory(); ++y) {
                ItemStack stack = inv.getStackInSlot(y);
                if (stack.getItem() == bra.getItem()) {
                    ItemStack contents = new ItemStack(bra.getItem(), stack.getCount() + bra.getCount());
                    inv.setInventorySlotContents(y, contents);
                    bra.setCount(contents.getMaxStackSize() - contents.getCount());
                } else if (stack.isEmpty()) {
                    inv.setInventorySlotContents(y, bra.split(bra.getCount()));
                }
            }
        }
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        IInventory inv = HopperTileEntity.getInventoryAtPosition(this.world, pos);
        if (inv == null) { return false; }
        for (int x = 0; x < this.entity.getCupSize().getSize(); ++x) {
            ItemStack bra = this.entity.getBrassiere().getStackInSlot(x);
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

    @Override
    public int getPriority() {
        return 0x7;
    }
}
