package moe.blocks.mod.item;

import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.SenpaiEntity;
import moe.blocks.mod.entity.partial.InteractEntity;
import moe.blocks.mod.init.MoeEntities;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.message.YearbookMessages;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class YearBookItem extends Item {

    public YearBookItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    public static InteractEntity getPage(World world, ItemStack stack, int page) {
        CompoundNBT stem = getYearbookInfo(stack);
        CompoundNBT info = (CompoundNBT) stem.get(stem.keySet().toArray(new String[0])[page]);
        InteractEntity entity = info.contains("BlockData") ? new MoeEntity(MoeEntities.MOE.get(), world) : new SenpaiEntity(MoeEntities.SENPAI.get(), world);
        entity.read(info);
        return entity;
    }

    public static CompoundNBT getYearbookInfo(ItemStack stack) {
        CompoundNBT check = stack.getShareTag();
        if (check != null && check.contains("YearbookInfo")) {
            return (CompoundNBT) (check.get("YearbookInfo"));
        }
        return new CompoundNBT();
    }

    public static int getPage(ItemStack stack, InteractEntity entity) {
        CompoundNBT stem = getYearbookInfo(stack);
        String[] keys = stem.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; ++i) {
            if (UUID.fromString(keys[i]).equals(entity.getUniqueID())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld) {
            ItemStack stack = player.getHeldItem(hand);
            updateYearbookInfo(stack, world);
            MoeMessages.send(new YearbookMessages.Open(stack, player));
            return ActionResult.resultSuccess(stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target.world instanceof ServerWorld && target instanceof InteractEntity) {
            InteractEntity entity = (InteractEntity) target;
            updateYearbookInfo(stack, entity.world);
            int page = getPage(stack, entity);
            if (page < 0) {
                page = getPageCount(stack);
                if (page > 50) {
                    player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.fail", entity.getName().getString()), true);
                    return ActionResultType.FAIL;
                } else {
                    setPage(stack, entity);
                }
            }
            player.setHeldItem(hand, stack);
            MoeMessages.send(new YearbookMessages.Open(stack, player, page));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onCreated(ItemStack stack, World world, PlayerEntity player) {
        CompoundNBT compound = stack.hasTag() ? stack.getShareTag() : new CompoundNBT();
        compound.putString("Author", player.getName().getString());
        //compound.putString("Edition", RomanNumerals.convert(YearBookData.get(world).getYearbookEdition(player)));
        stack.setTag(compound);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add((new TranslationTextComponent("item.moeblocks.yearbook.amount", getPageCount(stack))).mergeStyle(TextFormatting.GRAY));
        tooltip.add((new TranslationTextComponent("item.moeblocks.yearbook.author", getAuthorName(stack))).mergeStyle(TextFormatting.GRAY));
        tooltip.add((new TranslationTextComponent("item.moeblocks.yearbook.edition", getEdition(stack))).mergeStyle(TextFormatting.GRAY));
    }

    public static String getAuthorName(ItemStack stack) {
        CompoundNBT check = stack.getShareTag();
        if (check != null && check.contains("Author")) {
            return check.getString("Author");
        } else {
            return "null";
        }
    }

    public static String getEdition(ItemStack stack) {
        CompoundNBT check = stack.getShareTag();
        if (check != null && check.contains("Edition")) {
            return check.getString("Edition");
        } else {
            return "I";
        }
    }

    public static int getPageCount(ItemStack stack) {
        return getYearbookInfo(stack).keySet().size();
    }

    public static void updateYearbookInfo(ItemStack stack, World world) {
        CompoundNBT stem = getYearbookInfo(stack);
        if (world instanceof ServerWorld) {
            ServerWorld server = (ServerWorld) world;
            Iterator<String> it = stem.keySet().iterator();
            while (it.hasNext()) {
                setPage(stack, (InteractEntity) server.getEntityByUuid(UUID.fromString(it.next())));
            }
        }
    }

    public static void setPage(ItemStack stack, InteractEntity entity) {
        if (entity != null) {
            CompoundNBT stem = getYearbookInfo(stack);
            stem.put(entity.getUniqueID().toString(), entity.writeWithoutTypeId(new CompoundNBT()));
            setYearbookInfo(stack, stem);
        }
    }

    public static void setYearbookInfo(ItemStack stack, CompoundNBT compound) {
        CompoundNBT check = stack.getShareTag();
        check.put("YearbookInfo", compound);
        stack.setTag(check);
    }
}
