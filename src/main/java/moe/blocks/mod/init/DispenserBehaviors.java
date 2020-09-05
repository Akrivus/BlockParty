package moe.blocks.mod.init;

import moe.blocks.mod.entity.MoeDieEntity;
import moe.blocks.mod.init.MoeItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviors {
    public static void register() {
        DispenserBlock.registerDispenseBehavior(MoeItems.MOE_DIE.get(), new Die());
    }

    public static class Die extends ProjectileDispenseBehavior {
        @Override
        protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
            return new MoeDieEntity(world, position.getX(), position.getY(), position.getZ());
        }
    }
}
