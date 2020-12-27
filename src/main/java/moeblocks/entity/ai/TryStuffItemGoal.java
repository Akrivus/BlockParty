package moeblocks.entity.ai;

import moeblocks.entity.MoeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.function.Predicate;

public class TryStuffItemGoal<E extends MoeEntity> extends TryEquipItemGoal<E> {
    public TryStuffItemGoal(E entity, Predicate<ItemStack> check) {
        super(entity, check);
    }
    
    public TryStuffItemGoal(E entity, ITag.INamedTag<Item> tag) {
        super(entity, (stack) -> stack.getItem().isIn(tag));
    }
    
    @Override
    public void onArrival() {
        this.entity.onItemPickup(this.target, this.target.getItem().getCount());
        ItemStack loot = this.entity.getInventory().addItem(this.target.getItem());
        if (loot.isEmpty()) {
            this.target.remove();
        } else {
            this.target.getItem().setCount(loot.getCount());
        }
    }
    
    @Override
    public boolean canPickUp(ItemStack stack) {
        return this.check.test(stack) && this.entity.getInventory().func_233541_b_(stack);
    }
}
