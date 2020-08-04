package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.AbstractState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public class StressStats extends AbstractState {
    private int stress;
    private float multiplier = 1.0F;

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        this.multiplier += 0.0001F;
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.multiplier = compound.getFloat("StressMultiplier");
        this.stress = compound.getInt("Stress");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putFloat("StressMultiplier", this.multiplier);
        compound.putInt("Stress", this.stress);
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
        return false;
    }

    @Override
    public DataStats getKey() {
        return DataStats.STRESS;
    }

    public void addStress(int stress) {
        this.stress += stress * this.multiplier;
        this.multiplier = 1.0F;
    }

    public int getStress() {
        return this.stress;
    }
}