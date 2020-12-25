package moeblocks.entity.ai.goal;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.math.BlockPos;

public class FindBedGoal extends AbstractMoveToBlockGoal<AbstractNPCEntity> {
    private int tolerableRange = 0;
    
    public FindBedGoal(AbstractNPCEntity entity) {
        super(entity, 7, 16);
    }
    
    @Override
    public int getPriority() {
        return 0x9;
    }
    
    @Override
    public boolean shouldExecute() {
        if (!this.entity.isTimeToSleep()) { return false; }
        if (this.entity.getHomeDistance() < 16 + this.tolerableRange++) {
            this.pos = this.entity.getHomePosition();
            if (this.canMoveTo(this.pos, this.entity.getBlockState(this.pos))) {
                this.path = this.entity.getNavigator().getPathToPos(this.pos, 0);
                if (this.path != null) { return true; }
            }
        }
        return super.shouldExecute();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        if (this.world.isDaytime()) { return false; }
        return super.shouldContinueExecuting();
    }
    
    @Override
    public void onArrival() {
        this.entity.startSleeping(this.pos);
        this.tolerableRange = 0;
    }
    
    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        return state.isBed(this.world, pos, this.entity) && !state.get(BedBlock.OCCUPIED) && state.get(BedBlock.PART) == BedPart.HEAD;
    }
}
