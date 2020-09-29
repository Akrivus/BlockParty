package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToBlockGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Optional;

public class FindBedGoal extends AbstractMoveToBlockGoal<NPCEntity> {
    public FindBedGoal(NPCEntity entity) {
        super(entity, 7, 16);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.isTimeToSleep()) { return false; }
        if (this.entity.getHomeDistance() < 256) {
            this.pos = this.entity.getHomePosition();
            if (this.canMoveTo(this.pos, this.world.getBlockState(this.pos))) {
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
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        return state.isBed(this.world, pos, this.entity) && !state.get(BedBlock.OCCUPIED) && state.get(BedBlock.PART) == BedPart.HEAD;
    }

    @Override
    public int getPriority() {
        return 0x9;
    }
}
