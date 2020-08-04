package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public interface IMachineState {
    void start(MoeEntity moe);

    IMachineState stop(IMachineState swap);

    void start();

    void tick();

    void stop();

    void read(CompoundNBT compound);

    void write(CompoundNBT compound);

    boolean onDamage(DamageSource cause, float amount);

    boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand);

    boolean isArmed();

    Enum<?> getKey();

    String toString();
}
