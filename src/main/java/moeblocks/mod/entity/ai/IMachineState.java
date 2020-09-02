package moeblocks.mod.entity.ai;

import moeblocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

public interface IMachineState {
    void start(StudentEntity entity);

    IMachineState stop(IMachineState swap);

    void start();

    void tick();

    void stop();

    void read(CompoundNBT compound);

    void write(CompoundNBT compound);

    void onDeath(DamageSource cause);

    void onSpawn(IWorld world);

    boolean onDamage(DamageSource source, float amount);

    boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand);

    boolean isArmed();

    Enum<?> getKey();

    boolean matches(Enum<?>... keys);

    boolean canAttack(LivingEntity target);

    String toString();
}
