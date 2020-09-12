package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.item.ItemStack;

public class GrabFoodGoal<E extends NPCEntity> extends GrabItemsGoal<E> {

    public GrabFoodGoal(E entity) {
        super(entity);
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        return this.entity.canConsume(stack);
    }
}
