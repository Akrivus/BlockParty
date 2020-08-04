package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.List;

public class GrabGoal extends Goal {
    private final MoeEntity moe;
    private ItemEntity entity;
    private Path path;

    public GrabGoal(MoeEntity moe) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        List<ItemEntity> items = this.moe.world.getEntitiesWithinAABB(ItemEntity.class, this.moe.getBoundingBox().grow(3.0D, 2.0D, 3.0D));
        items.sort(new DistanceCheck(this.moe));
        for (ItemEntity item : items) {
            if (this.moe.canPickUpItem(item.getItem())) {
                this.path = this.moe.getNavigator().getPathToEntity(item, 0);
                this.entity = item;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.moe.hasPath() && this.entity.isAlive();
    }

    @Override
    public void startExecuting() {
        this.moe.getNavigator().setPath(this.path, 0.6F);
    }

    @Override
    public void resetTask() {
        this.moe.getNavigator().clearPath();
        this.entity = null;
    }
}