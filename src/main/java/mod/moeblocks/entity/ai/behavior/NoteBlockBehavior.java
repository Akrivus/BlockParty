package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;

public class NoteBlockBehavior extends BasicBehavior {
    protected float pitch;
    protected int note;

    @Override
    public void start() {
        this.pitch = (float) (Math.pow(2.0D, ((this.note = this.getBlockState().get(NoteBlock.NOTE)) - 12) / 12.0D));
    }

    @Override
    public void read(CompoundNBT compound) {
        this.pitch = compound.getFloat("NoteBlockPitch");
        this.note = compound.getInt("NoteBlockNote");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putFloat("NoteBlockPitch", this.pitch);
        compound.putInt("NoteBlockNote", this.note);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.NOTE_BLOCK;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.pitch = (float) (Math.pow(2.0D, ((this.note = Math.max(this.note + 1 % 25, 0)) - 12) / 12.0D));
            this.moe.world.addParticle(ParticleTypes.NOTE, this.moe.getPosXRandom(0.375D), this.moe.getPosYRandom() + 0.25D, this.moe.getPosZRandom(0.375D), this.note / 24.0D, 0.0D, 0.0D);
            this.moe.playSound(SoundEventsMoe.SING.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }
}
