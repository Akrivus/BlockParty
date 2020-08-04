package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.BossInfo;

public class TsunDere extends AbstractDere {
    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {

    }

    @Override
    public void write(CompoundNBT compound) {

    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        return false;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        return false;
    }

    @Override
    public boolean isArmed() {
        return true;
    }

    @Override
    public Deres getKey() {
        return Deres.TSUNDERE;
    }

    @Override
    public float[] getEyeColor() {
        return new float[]{0.99F, 0.73F, 0.49F};
    }

    @Override
    public int getNameColor() {
        return 0xfdb97c;
    }

    @Override
    public BossInfo.Color getBarColor() {
        return BossInfo.Color.YELLOW;
    }
}
