package mod.moeblocks.entity.ai.goal;


import mod.moeblocks.entity.MoeEntity;
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
    private final MoeEntity moe;
    private BlockPos pos = BlockPos.ZERO;
    private boolean nearDoor;
    private int timeUntilClosed;
    private boolean hasStoppedDoorInteraction;
    private float x;
    private float z;

    public OpenDoorGoal(MoeEntity moe) {
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        if (this.moe.collidedHorizontally) {
            PathNavigator navigator = this.moe.getNavigator();
            Path path = navigator.getPath();
            if (path != null && !path.isFinished()) {
                for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
                    PathPoint point = path.getPathPointFromIndex(i);
                    this.pos = new BlockPos(point.x, point.y + 1, point.z);
                    if (this.moe.getDistanceSq(this.pos.getX(), this.moe.getPosY(), this.pos.getZ()) < 2.25D) {
                        return this.nearDoor = canOpenDoor(this.moe.world, this.pos);
                    }
                }
                this.pos = this.moe.getPosition().up();
                this.nearDoor = canOpenDoor(this.moe.world, this.pos);
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
        this.x = (float) ((this.pos.getX() + 0.5F) - this.moe.getPosX());
        this.z = (float) ((this.pos.getZ() + 0.5F) - this.moe.getPosZ());
        this.toggleDoor(true);
        this.timeUntilClosed = 20;
    }

    protected void toggleDoor(boolean open) {
        BlockState state = this.moe.world.getBlockState(this.pos);
        if (state.getBlock() instanceof DoorBlock) {
            DoorBlock door = (DoorBlock) state.getBlock();
            door.toggleDoor(this.moe.world, this.pos, open);
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
            float dX = (float) ((this.pos.getX() + 0.5F) - this.moe.getPosX());
            float dZ = (float) ((this.pos.getZ() + 0.5F) - this.moe.getPosZ());
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