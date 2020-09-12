package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class SleepInBedGoal extends AbstractMoveToBlockGoal<NPCEntity> {
    public SleepInBedGoal(NPCEntity entity) {
        super(entity, 4, 8);
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (this.world.isDaytime()) { return false; }
        if (this.entity.isWithinHomeDistanceCurrentPosition()) {
            this.pos = this.entity.getHomePosition();
            if (this.canMoveTo(this.pos, this.world.getBlockState(this.pos))) {
                this.path = this.entity.getNavigator().getPathToPos(this.pos, 0);
                return this.path != null;
            }
        }
        return super.shouldExecute();
    }

    @Override
    protected boolean isHoldingCorrectItem(ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.world.isDaytime()) { return false; }
        if (this.entity.isSleeping()) { return true; }
        return super.shouldContinueExecuting();
    }

    @Override
    public void resetTask() {
        this.entity.clearBedPosition();
        super.resetTask();
    }

    @Override
    public void onArrival() {
        this.entity.startSleeping(this.pos);
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        return state.isBed(this.world, pos, this.entity) && !state.get(BedBlock.OCCUPIED) && state.get(BedBlock.PART) == BedPart.HEAD;
    }

    @Override
    public int getPriority() {
        return 0x2;
    }
}
