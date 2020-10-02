package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.function.Predicate;

public class TryStuffItemGoal<E extends AbstractNPCEntity> extends TryEquipItemGoal<E> {
    protected AbstractNPCEntity character;

    public TryStuffItemGoal(E entity, Predicate<ItemStack> check) {
        super(entity, check);
    }

    public TryStuffItemGoal(E entity, ITag.INamedTag<Item> tag) {
        super(entity, (stack) -> stack.getItem().isIn(tag));
    }

    @Override
    public void onArrival() {
        this.entity.onItemPickup(this.target, this.target.getItem().getCount());
        ItemStack loot = this.character.getBrassiere().addItem(this.target.getItem());
        if (loot.isEmpty()) {
            this.target.remove();
        } else {
            this.target.getItem().setCount(loot.getCount());
        }
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        if (this.entity instanceof AbstractNPCEntity && this.character == null) { this.character = (AbstractNPCEntity) entity; }
        if (this.character == null) { return false; }
        return this.check.test(stack) && this.character.getBrassiere().func_233541_b_(stack);
    }
}
