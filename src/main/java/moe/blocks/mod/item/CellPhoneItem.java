package moe.blocks.mod.item;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.client.screen.CellPhoneScreen;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CellPhoneItem extends Item {

    public CellPhoneItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    public static ItemStack removeContact(UUID uuid, ItemStack stack) {
        CompoundNBT compound = stack.getOrCreateTag();
        if (!compound.contains("Contacts")) { compound.put("Contacts", new ListNBT()); }
        ListNBT nbt = compound.getList("Contacts", 10);
        for (int i = 0; i < nbt.size(); ++i) {
            CompoundNBT contact = (CompoundNBT) nbt.get(i);
            if (contact.getUniqueId("UUID").equals(uuid)) {
                nbt.remove(i);
                --i;
            }
        }
        compound.put("Contacts", nbt);
        stack.setTag(compound);
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote()) { Minecraft.getInstance().displayGuiScreen(new CellPhoneScreen(getContacts(stack))); }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!(entity instanceof CharacterEntity)) { return ActionResultType.PASS; }
        player.setHeldItem(hand, addContact((CharacterEntity) entity, stack));
        return ActionResultType.SUCCESS;
    }

    public static ItemStack addContact(CharacterEntity entity, ItemStack stack) {
        CompoundNBT compound = stack.getOrCreateTag();
        if (!compound.contains("Contacts")) { compound.put("Contacts", new ListNBT()); }
        ListNBT nbt = compound.getList("Contacts", 10);
        nbt.add(entity.setPhoneContact(new CompoundNBT()));
        compound.put("Contacts", nbt);
        stack.setTag(compound);
        return stack;
    }

    public static List<CellPhoneScreen.ContactEntry> getContacts(ItemStack stack) {
        List<CellPhoneScreen.ContactEntry> list = new ArrayList<>();
        CompoundNBT compound = stack.getOrCreateTag();
        if (!compound.contains("Contacts")) { compound.put("Contacts", new ListNBT()); }
        ListNBT nbt = compound.getList("Contacts", 10);
        nbt.forEach(tag -> list.add(new CellPhoneScreen.ContactEntry(tag)));
        compound.put("Contacts", nbt);
        return list;
    }

    @Mod.EventBusSubscriber(modid = MoeMod.ID)
    public static class CellPhoneHandler {
        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent e) {
            /*ItemStack stack = e.getItemStack();
            if (stack.getItem() instanceof CellPhoneItem) {
                float degrees = (float) Math.sin(e.getPartialTicks()) * 2.0F;
                MatrixStack matrix = e.getMatrixStack();
                matrix.rotate(Vector3f.YP.rotationDegrees(degrees));
            }*/
        }
    }
}
