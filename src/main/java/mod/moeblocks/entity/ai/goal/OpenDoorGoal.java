package mod.moeblocks.entity.ai.goal;


import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.init.MoeTags;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenDoorGoal extends Goal {
    private final StateEntity entity;
    private BlockPos pos = BlockPos.ZERO;
    private boolean hasStoppedDoorInteraction;
    private float x;
    private float z;
    private int timeUntilClosed;

    public OpenDoorGoal(StateEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        Path path = this.entity.getNavigator().getPath();
        if (path != null && !path.isFinished() && (this.entity.collidedHorizontally || this.entity.collidedVertically)) {
            for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
                PathPoint point = path.getPathPointFromIndex(i);
                this.pos = new BlockPos(point.x, point.y + 1, point.z);
                if (this.entity.getDistanceSq(this.pos.getX(), this.entity.getPosY(), this.pos.getZ()) < 2.25D) {
                    return canOpenDoor(this.entity.world, this.pos);
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.hasStoppedDoorInteraction && this.timeUntilClosed > 0;
    }

    @Override
    public void startExecuting() {
        this.hasStoppedDoorInteraction = false;
        this.x = (float) ((this.pos.getX() + 0.5F) - this.entity.getPosX());
        this.z = (float) ((this.pos.getZ() + 0.5F) - this.entity.getPosZ());
        this.timeUntilClosed = 20;
        this.setDoorState(true);
    }

    protected void setDoorState(boolean open) {
        BlockState state = this.entity.world.getBlockState(this.pos);
        Block block = state.getBlock();
        if (block instanceof FenceGateBlock) {
            this.entity.world.setBlockState(this.pos, state.with(FenceGateBlock.OPEN, open));
        }
        if (block instanceof DoorBlock) {
            this.entity.world.setBlockState(this.pos, state.with(DoorBlock.OPEN, open));
        }
        if (block instanceof TrapDoorBlock) {
            this.entity.world.setBlockState(this.pos, state.with(TrapDoorBlock.OPEN, open));
        }
    }

    @Override
    public void resetTask() {
        this.setDoorState(false);
    }

    @Override
    public void tick() {
        if (this.hasStoppedDoorInteraction) {
            --this.timeUntilClosed;
        } else {
            float dX = (float) ((this.pos.getX() + 0.5F) - this.entity.getPosX());
            float dZ = (float) ((this.pos.getZ() + 0.5F) - this.entity.getPosZ());
            float dD = this.x * dX + this.z * dZ;
            this.hasStoppedDoorInteraction = dD < 0.0F;
        }
    }

    public static boolean canOpenDoor(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock().isIn(MoeTags.DOORS);
    }
}