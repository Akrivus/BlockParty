package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import moe.blocks.mod.entity.util.VoiceLines;
import net.minecraft.block.Blocks;
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
        this.pitch = this.moe.getBlockData().getBlock() == Blocks.NOTE_BLOCK ? (float) (Math.pow(2.0D, ((this.note = this.moe.getBlockData().get(NoteBlock.NOTE)) - 12) / 12.0D)) : 1.0F;
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
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.pitch = (float) (Math.pow(2.0D, ((this.note = Math.max(this.note + 1 % 25, 0)) - 12) / 12.0D));
            this.moe.world.addParticle(ParticleTypes.NOTE, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), this.note / 24.0D, 0.0D, 0.0D);
            this.moe.playSound(VoiceLines.SING.get(this.entity));
            return true;
        }
        return false;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public String getFile() {
        return String.format("%s.%d", super.getFile(), this.note);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.NOTE_BLOCK;
    }
}
