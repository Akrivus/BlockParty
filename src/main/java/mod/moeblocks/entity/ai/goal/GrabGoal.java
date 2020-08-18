package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.Path;

import java.util.List;

public class GrabGoal extends Goal {
    private final StateEntity entity;
    private ItemEntity stack;
    private Path path;

    public GrabGoal(StateEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        List<ItemEntity> items = this.entity.world.getEntitiesWithinAABB(ItemEntity.class, this.entity.getBoundingBox().grow(8.0D, 2.0D, 8.0D));
        items.sort(new DistanceCheck(this.entity));
        for (ItemEntity item : items) {
            if (this.entity.canPickUpItem(item.getItem()) && item.isAlive() && !item.cannotPickup()) {
                this.path = this.entity.getNavigator().getPathToEntity(item, 0);
                this.stack = item;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath() && this.entity.canPickUpItem(this.stack.getItem());
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, 1.0F);
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.stack = null;
    }

    @Override
    public void tick() {
        if (this.entity.getEntitySenses().canSee(this.stack) && this.entity.getDistance(this.stack) < 1.0F) {
            if (this.entity.tryEquipItem(this.stack.getItem())) {
                this.entity.onItemPickup(this.stack, this.stack.getItem().getCount());
                this.stack.remove();
            }
        }
    }
}