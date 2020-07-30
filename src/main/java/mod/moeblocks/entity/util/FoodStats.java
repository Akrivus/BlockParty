package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.AbstractState;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

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

    public void consume(ItemStack stack) {
        if (stack.isFood()) {
            Food food = stack.getItem().getFood();
            this.addStats(food.getHealing(), food.getSaturation());
        }
    }

    public void addStats(int food, float saturation) {
        this.saturation = Math.min(this.saturation + food * saturation * 2.0F, this.food);
        this.food = Math.min(this.food + food, 20);
    }

    public boolean isHungry() {
        return this.food < 15;
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }
}