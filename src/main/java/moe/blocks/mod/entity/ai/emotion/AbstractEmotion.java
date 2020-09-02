package moe.blocks.mod.entity.ai.emotion;

import moe.blocks.mod.entity.ai.AbstractState;
import moe.blocks.mod.entity.util.VoiceLines;
import moe.blocks.mod.entity.util.Emotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.IWorld;

public class AbstractEmotion extends AbstractState {
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_NORMAL.get(this.entity);
    }

    public String getPath() {
        return this.toString().toLowerCase();
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
        Emotions emotion = (Emotions) this.entity.getEmotion().getKey();
        return emotion.matches(keys);
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
}
