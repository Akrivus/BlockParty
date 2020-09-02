package moe.blocks.mod.entity.ai.dere;

import moe.blocks.mod.entity.ai.AbstractState;
import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.BossInfo;
import net.minecraft.world.IWorld;

public class AbstractDere extends AbstractState {
    public float[] getEyeColor() {
        return new float[]{1.0F, 1.0F, 1.0F};
    }

    public int getNameColor() {
        return 0xffffff;
    }

    public BossInfo.Color getBarColor() {
        return BossInfo.Color.WHITE;
    }

    public float getGiftValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item.isIn(MoeTags.GIFTS)) {
            return 2.0F;
        }
        if (item.isIn(ItemTags.FLOWERS)) {
            return 1.0F;
        }
        return 0.0F;
    }

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
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource source, float amount) {
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

    @Override
    public String toString() {
        return this.getKey().name();
    }

    @Override
    public Enum<?> getKey() {
        return null;
    }

    @Override
    public boolean matches(Enum<?>... keys) {
        Deres dere = (Deres) this.entity.getDere().getKey();
        return dere.matches(keys);
    }
}
