package mod.moeblocks.util;

import mod.moeblocks.entity.MoeDieEntity;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviors {
    public static void register() {
        DispenserBlock.registerDispenseBehavior(ItemsMoe.MOE_DIE.get(), new Die());
    }

    public static class Die extends ProjectileDispenseBehavior {
        @Override
        protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
            return new MoeDieEntity(world, position.getX(), position.getY(), position.getZ());
        }
    }
}
