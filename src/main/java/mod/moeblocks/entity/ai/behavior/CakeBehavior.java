package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.entity.util.VoiceLines;
import net.minecraft.block.Blocks;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;

public class CakeBehavior extends BasicBehavior {
    protected int bites;

    @Override
    public void start() {
        this.bites = this.moe.getBlockData().getBlock() == Blocks.CAKE ? this.moe.getBlockData().get(CakeBlock.BITES) : 0;
    }

    @Override
    public void read(CompoundNBT compound) {
        this.bites = compound.getInt("CakeBites");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("CakeBites", this.bites);
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.moe.attackEntityFrom(new DamageSource("vore"), this.moe.getHealth() / ++this.bites);
            player.playSound(SoundEvents.ENTITY_GENERIC_EAT, 3.0F, 1.0F);
            this.moe.playSound(VoiceLines.HURT.get(this.entity));
            return true;
        }
        return false;
    }

    @Override
    public String getFile() {
        return String.format("%s.%d", super.getFile(), this.bites);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.NOTE_BLOCK;
    }
}
