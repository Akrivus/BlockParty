package mod.moeblocks.entity.ai.goal;


import mod.moeblocks.entity.StateEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
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
    private boolean nearDoor;
    private int timeUntilClosed;
    private boolean hasStoppedDoorInteraction;
    private float x;
    private float z;

    public OpenDoorGoal(StateEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.collidedHorizontally) {
            PathNavigator navigator = this.entity.getNavigator();
            Path path = navigator.getPath();
            if (path != null && !path.isFinished()) {
                for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
                    PathPoint point = path.getPathPointFromIndex(i);
                    this.pos = new BlockPos(point.x, point.y + 1, point.z);
                    if (this.entity.getDistanceSq(this.pos.getX(), this.entity.getPosY(), this.pos.getZ()) < 2.25D) {
                        return this.nearDoor = canOpenDoor(this.entity.world, this.pos);
                    }
                }
                this.pos = this.entity.getPosition().up();
                this.nearDoor = canOpenDoor(this.entity.world, this.pos);
                return this.nearDoor;
            }
            return false;
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
        this.toggleDoor(true);
        this.timeUntilClosed = 20;
    }

    protected void toggleDoor(boolean open) {
        BlockState state = this.entity.world.getBlockState(this.pos);
        Block block = state.getBlock();
        if (block instanceof DoorBlock) {
            ((DoorBlock) block).func_242663_a(this.entity.world, state, this.pos, open);
        }
    }

    @Override
    public void resetTask() {
        this.toggleDoor(false);
    }

    @Override
    public void tick() {
        if (this.hasStoppedDoorInteraction) {
            --this.timeUntilClosed;
        } else {
            float dX = (float) ((this.pos.getX() + 0.5F) - this.entity.getPosX());
            float dZ = (float) ((this.pos.getZ() + 0.5F) - this.entity.getPosZ());
            float dD = this.x * dX + this.z * dZ;
            if (dD < 0.0F) {
                this.hasStoppedDoorInteraction = true;
            }
        }
    }

    public static boolean canOpenDoor(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof DoorBlock && state.getMaterial() == Material.WOOD;
    }
}