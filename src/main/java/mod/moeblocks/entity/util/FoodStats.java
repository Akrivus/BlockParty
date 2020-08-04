package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public class FoodStats extends AbstractState {
    private int food = 20;
    private float saturation;
    private float exhaustion;
    private int timer;

    @Override
    public void start() {
        this.saturation = 5.0F;
    }

    @Override
    public void tick() {
        ++this.timer;
        this.exhaustion -= 0.0001F;
        if (this.moe.getHealth() < this.moe.getMaxHealth() && this.saturation > 0.0F && this.food >= 18) {
            if (this.timer >= 10) {
                this.moe.heal(1.0F);
                this.exhaustion += 6.0F;
                this.timer = 0;
            }
        } else {
            this.timer = 0;
        }
        if (this.exhaustion > 4.0F) {
            this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            this.exhaustion -= 4.0F;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.saturation = compound.getFloat("Saturation");
        this.exhaustion = compound.getFloat("Exhaustion");
        this.food = compound.getInt("Food");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putFloat("Saturation", this.saturation);
        compound.putFloat("Exhaustion", this.exhaustion);
        compound.putInt("Food", this.food);
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
        return DataStats.FOOD;
    }

    public boolean canConsume(ItemStack stack) {
        return stack.isFood() && (this.isHungry() || stack.getItem().getFood().canEatWhenFull());
    }

    public boolean isHungry() {
        return this.food < 15;
    }

    public void consume(Food food) {
        this.addStats(food.getHealing(), food.getSaturation());
        this.moe.playSound(SoundEventsMoe.EAT.get());
        food.getEffects().forEach(pair -> {
            if (pair.getLeft() != null && this.moe.world.rand.nextFloat() < pair.getRight()) {
                this.moe.addPotionEffect(pair.getLeft());
            }
        });
    }

    public void addStats(int food, float saturation) {
        this.saturation = Math.min(this.saturation + food * saturation * 2.0F, this.food);
        this.food = Math.min(this.food + food, 20);
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }
}