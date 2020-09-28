package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToBlockGoal;
import moe.blocks.mod.entity.partial.InteractEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ToolType;

public class MineTargetGoal extends AbstractMoveToBlockGoal<InteractEntity> {

    public MineTargetGoal(InteractEntity entity) {
        super(entity, 4, 8);
    }

    protected boolean isHoldingCorrectItem(ItemStack stack) {
        BlockState state = this.entity.getBlockTarget();
        if (state != null) {
            Item item = this.entity.getHeldItem(Hand.MAIN_HAND).getItem();
            if (item.canHarvestBlock(state)) {
                ToolType tool = state.getHarvestTool();
                if (tool == ToolType.PICKAXE) {
                    return item instanceof PickaxeItem;
                } else if (tool == ToolType.SHOVEL) {
                    return item instanceof ShovelItem;
                } else if (tool == ToolType.HOE) {
                    return item instanceof HoeItem;
                } else if (tool == ToolType.AXE) {
                    return item instanceof AxeItem;
                }
            }
        }
        return false;
    }

    @Override
    public void onArrival() {
        if (this.world.destroyBlock(this.pos, true)) { this.entity.swingArm(Hand.MAIN_HAND); }
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        if (this.isHoldingCorrectItem(this.entity.getHeldItem(Hand.MAIN_HAND))) { return false; }
        return state.getBlock().equals(this.entity.getBlockTarget());
    }

    @Override
    public int getPriority() {
        return 0x7;
    }
}
