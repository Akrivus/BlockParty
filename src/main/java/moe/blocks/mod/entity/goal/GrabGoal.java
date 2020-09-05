package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.util.Emotions;
import net.minecraft.entity.item.ItemEntity;

public class GrabGoal extends MoveGoal<ItemEntity> {

    public GrabGoal(FiniteEntity entity) {
        super(entity, ItemEntity.class, 0.5D);
    }

    @Override
    public void startExecuting() {
        this.entity.setEmotion(Emotions.MISCHIEVOUS, 3600);
        this.entity.setSneaking(true);
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        this.entity.setEmotionalTimeout(0);
        this.entity.setSneaking(false);
        super.resetTask();
    }

    @Override
    public void onFollowed() {
        if (this.entity.canSee(this.target) && this.entity.tryEquipItem(this.target.getItem())) {
            this.entity.onItemPickup(this.target, this.target.getItem().getCount());
            this.target.remove();
        }
    }

    @Override
    public boolean canMoveTo(ItemEntity item) {
        return this.entity.canPickUpItem(item.getItem()) && !item.cannotPickup();
    }
}