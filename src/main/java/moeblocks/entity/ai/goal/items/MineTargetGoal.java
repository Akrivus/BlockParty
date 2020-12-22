package moeblocks.entity.ai.goal.items;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.goal.AbstractMoveToBlockGoal;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

public class MineTargetGoal extends AbstractMoveToBlockGoal<AbstractNPCEntity> {

    public MineTargetGoal(AbstractNPCEntity entity) {
        super(entity, 4, 8);
    }

    @Override
    public int getPriority() {
        return 0x7;
    }

    @Override
    public void onArrival() {
        if (this.world.destroyBlock(this.pos, true)) { this.entity.swingArm(Hand.MAIN_HAND); }
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        if (this.isHoldingCorrectItem(this.entity.getHeldItem(Hand.MAIN_HAND))) { return false; }
        return state.getBlock().equals(this.entity.getBlockToMine());
    }

    protected boolean isHoldingCorrectItem(ItemStack stack) {
        BlockState state = this.entity.getBlockToMine();
        if (state == null) { return false; }
        ToolType tool = state.getHarvestTool();
        Item item = stack.getItem();
        if (!item.canHarvestBlock(state)) {
            if (tool == ToolType.AXE) { return item instanceof AxeItem; }
            if (tool == ToolType.PICKAXE) { return item instanceof PickaxeItem; }
            if (tool == ToolType.SHOVEL) { return item instanceof ShovelItem; }
            if (tool == ToolType.HOE) { return item instanceof HoeItem; }
        }
        return false;
    }
}
