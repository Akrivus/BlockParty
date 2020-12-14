package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.client.screen.CellPhoneScreen;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeItems;
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
}
