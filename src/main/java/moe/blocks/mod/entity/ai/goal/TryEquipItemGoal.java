package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.function.Predicate;

public class TryEquipItemGoal<E extends AbstractNPCEntity> extends AbstractMoveToEntityGoal<E, ItemEntity> {
    protected final Predicate<ItemStack> check;

    public TryEquipItemGoal(E entity) {
        this(entity, MoeTags.EQUIPPABLES);
    }

    public TryEquipItemGoal(E entity, ITag.INamedTag<Item> tag) {
        this(entity, (stack) -> stack.getItem().isIn(tag));
    }

    public TryEquipItemGoal(E entity, Predicate<ItemStack> check) {
        super(entity, ItemEntity.class, 0.5D);
        this.check = check;
    }

    @Override
    public int getPriority() {
        return 0x6;
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
        return this.check.test(stack) && this.entity.canPickUpItem(stack);
    }
}