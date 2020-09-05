package moe.blocks.mod.entity.state;

import moe.blocks.mod.entity.util.VoiceLines;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

public class FoodState extends AbstractState {
    private int food = 20;
    private float saturation = 5.0F;
    private float exhaustion;
    private int timeUntilEat = 20;
    private int timer;

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        if (this.canConsume(this.entity.getHeldItem(Hand.OFF_HAND)) && --this.timeUntilEat < 0) {
            ItemStack stack = this.entity.getHeldItem(Hand.OFF_HAND);
            this.entity.getStressState().addStressSilently(-this.entity.getDere().getGiftValue(stack) - 0.5F);
            this.entity.playSound(stack.getEatSound());
            this.consume(stack.getItem().getFood());
            stack.shrink(1);
        }
        this.exhaustion += 0.005F;
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturation > 0.0F) {
                this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            } else {
                this.food = Math.max(this.food - 1, 0);
            }
        }
        ++this.timer;
        if (this.entity.getHealth() < this.entity.getMaxHealth() && this.saturation > 0.0F && this.food >= 10) {
            if (this.timer >= 10) {
                this.entity.heal(1.0F);
                this.exhaustion += 6.0F;
                this.timer = 0;
            }
        } else {
            this.timer = 0;
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
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onSpawn(IWorld world) {

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
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    public boolean canConsume(ItemStack stack) {
        return stack.isFood() && (this.isNotFull() || stack.getItem().getFood().canEatWhenFull());
    }

    public boolean isNotFull() {
        return this.food < 20;
    }

    public void consume(Food food) {
        this.addStats(food.getHealing(), food.getSaturation());
        this.entity.playSound(VoiceLines.EAT.get(this.entity));
        this.timeUntilEat = 20;
        food.getEffects().forEach(pair -> {
            if (pair.getFirst() != null && this.entity.world.rand.nextFloat() < pair.getSecond()) {
                this.entity.addPotionEffect(pair.getFirst());
            }
        });
    }

    public void addStats(int food, float saturation) {
        this.saturation = Math.min(this.saturation + food * saturation * 2.0F, this.food);
        this.food = Math.min(this.food + food, 20);
    }

    public boolean isSatiated() {
        return this.food > 10;
    }

    public boolean isHungry() {
        return this.food < 10;
    }

    public boolean isStarving() {
        return this.food < 5;
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }

    public float getFoodLevel() {
        return this.food;
    }
}