package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;

public class GrabItemsGoal<E extends NPCEntity> extends AbstractMoveToEntityGoal<E, ItemEntity> {

    public GrabItemsGoal(E entity) {
        super(entity, ItemEntity.class, 0.5D);
    }

    @Override
    public void startExecuting() {
        this.entity.setSneaking(true);
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        this.entity.setSneaking(false);
        super.resetTask();
    }

    @Override
    public void onArrival() {
        if (this.entity.tryEquipItem(this.target.getItem())) {
            this.entity.onItemPickup(this.target, this.target.getItem().getCount());
            this.target.remove();
        }
    }

    @Override
    public float getStrikeZone(ItemEntity target) {
        return this.entity.getStrikingDistance(target.getWidth());
    }

    @Override
    public float getSafeZone(ItemEntity target) {
        return 0.0F;
    }

    @Override
    public boolean canMoveTo(ItemEntity item) {
        if (item == null || item.removed || item.getItem().isEmpty() || item.cannotPickup()) { return false; }
        return this.canPickUp(item.getItem()) && this.entity.canSee(item);
    }

    public boolean canPickUp(ItemStack stack) {
        return this.entity.canPickUpItem(stack);
    }

    @Override
    public int getPriority() {
        return 0x5;
    }
}