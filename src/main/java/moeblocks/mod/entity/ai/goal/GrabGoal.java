package moeblocks.mod.entity.ai.goal;

import moeblocks.mod.entity.StudentEntity;
import moeblocks.mod.entity.util.Emotions;
import moeblocks.mod.util.DistanceCheck;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.List;

public class GrabGoal extends Goal {
    private final StudentEntity entity;
    private ItemEntity stack;
    private Path path;
    private int timeUntilGiveUp;

    public GrabGoal(StudentEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        List<ItemEntity> items = this.entity.world.getEntitiesWithinAABB(ItemEntity.class, this.entity.getBoundingBox().grow(8.0D, 2.0D, 8.0D));
        items.sort(new DistanceCheck(this.entity));
        for (ItemEntity item : items) {
            if (this.entity.canBeTarget(item) && this.entity.canPickUpItem(item.getItem()) && !item.cannotPickup()) {
                this.path = this.entity.getNavigator().getPathToEntity(item, 0);
                this.stack = item;
                return this.entity.canSee(this.stack);
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilGiveUp > 0 && this.entity.hasPath() && this.entity.canBeTarget(this.stack) && this.entity.canPickUpItem(this.stack.getItem()) && this.entity.canSee(this.stack);
    }

    @Override
    public void startExecuting() {
        this.entity.setEmotion(Emotions.MISCHIEVOUS, this.timeUntilGiveUp = 3600);
        this.entity.getNavigator().setPath(this.path, 0.5F);
        this.entity.setSneaking(true);
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.entity.setEmotionalTimeout(0);
        this.entity.setSneaking(false);
    }

    @Override
    public void tick() {
        if (this.entity.getDistance(this.stack) < 1.0F && this.entity.tryEquipItem(this.stack.getItem())) {
            this.entity.onItemPickup(this.stack, this.stack.getItem().getCount());
            this.stack.remove();
        }
    }
}