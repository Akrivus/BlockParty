package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.function.Predicate;

public class TryStuffItemGoal<E extends NPCEntity> extends TryEquipItemGoal<E> {
    protected CharacterEntity character;

    public TryStuffItemGoal(E entity, Predicate<ItemStack> check) {
        super(entity, check);
        if (entity instanceof CharacterEntity) { this.character = (CharacterEntity) entity; }
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
        if (this.entity instanceof CharacterEntity && this.character == null) { this.character = (CharacterEntity) entity; }
        if (this.character == null) { return false; }
        return this.check.test(stack) && this.character.getBrassiere().func_233541_b_(stack);
    }
}
