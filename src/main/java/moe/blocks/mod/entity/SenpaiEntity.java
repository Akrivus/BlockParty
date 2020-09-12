package moe.blocks.mod.entity;

import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.entity.partial.InteractiveEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SenpaiEntity extends CharacterEntity {
    public SenpaiEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        return ActionResultType.PASS;
    }

    @Override
    public int getBaseAge() {
        return 16;
    }

    @Override
    public ITextComponent getCustomName() {
        String translation = String.format("entity.moeblocks.senpai.%s", this.getDere().toString().toLowerCase());
        TranslationTextComponent component = new TranslationTextComponent(translation);
        return component;
    }
}
