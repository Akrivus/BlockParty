package moeblocks.item;

import moeblocks.entity.MoeDieEntity;
import moeblocks.init.MoeItems;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MoeDieItem extends Item {
    public static final ProjectileDispenseBehavior DISPENSER_BEHAVIOR = new ProjectileDispenseBehavior() {
        @Override
        protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
            return new MoeDieEntity(world, position.getX(), position.getY(), position.getZ());
        }
    };

    public MoeDieItem() {
        super(new Item.Properties().group(MoeItems.CreativeTab.INSTANCE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote) { return ActionResult.resultPass(player.getHeldItem(hand)); }
        ItemStack stack = player.getHeldItem(hand);
        MoeDieEntity die = new MoeDieEntity(world, player);
        die.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.8F, 1.0F);
        die.setItem(stack);
        if (world.addEntity(die)) {
            player.addStat(Stats.ITEM_USED.get(this));
            if (!player.abilities.isCreativeMode) { stack.shrink(1); }
            return ActionResult.resultSuccess(stack);
        } else {
            return ActionResult.resultFail(stack);
        }
    }
}