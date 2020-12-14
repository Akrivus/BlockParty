package moeblocks.item;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class InviteItem extends Item {

    public InviteItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        if (!LetterItem.isOpen(stack)) { return ActionResultType.FAIL; }
        CompoundNBT tag = this.getShareTag(stack);
        if (tag == null) { tag = new CompoundNBT(); }
        tag.putLong("Position", context.getPos().toLong());
        tag.putBoolean("IsClosed", true);
        stack.setTag(tag);
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!LetterItem.isOpen(stack) && entity instanceof AbstractNPCEntity) {
            AbstractNPCEntity npc = (AbstractNPCEntity) entity;
            //npc.setWaypoint(BlockPos.fromLong(stack.getTag().getLong("Position")));
            if (!player.isSneaking()) { stack.shrink(1); }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        TranslationTextComponent component;
        if (LetterItem.isOpen(stack)) {
            component = new TranslationTextComponent("item.moeblocks.invite.opened");
        } else {
            BlockPos pos = BlockPos.fromLong(stack.getShareTag().getLong("Position"));
            component = new TranslationTextComponent("item.moeblocks.invite.closed", pos.getX(), pos.getY(), pos.getZ());
        }
        list.add(component.mergeStyle(TextFormatting.GRAY));
    }
}
