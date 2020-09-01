package mod.moeblocks.entity.util.data;

import mod.moeblocks.entity.ai.AbstractState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

public class StressStats extends AbstractState {
    private float multiplier = 1.0F;
    private float stress = 0.0F;

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        this.addStressSilently((1.0F - this.entity.getHealth() / this.entity.getMaxHealth()) * 0.005F * (this.entity.isSprinting() ? 2.0F : 1.0F));
        this.multiplier += 0.0001F;
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.multiplier = compound.getFloat("StressMultiplier");
        this.stress = compound.getFloat("Stress");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putFloat("StressMultiplier", this.multiplier);
        compound.putFloat("Stress", this.stress);
    }

    @Override
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        this.addStress(amount / this.entity.getHealth() + 1.0F);
        this.entity.setEmotionalTimeout(0);
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
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    public void addStress(float stress) {
        this.addStressSilently(stress);
        if (stress > 0) {
            this.multiplier = 1.0F;
        }
    }

    public void addStressSilently(float stress) {
        this.stress = Math.max(Math.min(this.stress + stress * this.multiplier, 20.0F), -10.0F);
    }

    public float getStress() {
        return this.stress;
    }

    public boolean isCalm() {
        return this.stress < 2.0F;
    }

    public boolean isStressed() {
        return this.stress > 2.0F;
    }
}