package block_party.entities.abstraction;

import block_party.entities.BlockPartyNPC;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.util.ITeleporter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstraction layer 1: pathfinding, sound, and bug fixes.
 */
public abstract class Layer1 extends PathfinderMob {
    protected Layer1(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, 0.0F);
        this.restrictTo(this.blockPosition(), 16);
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        this.setUUID(UUID.randomUUID());
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean attacked = this.doHurtTarget(target);
        if (attacked) { this.playSound(this.getAttackSound()); }
        return attacked;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) { return 0.0F; }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) { return; }

    @Override
    public Component getTypeName() {
        return super.getTypeName();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(this.getStepSound(), 0.15F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
    }

    public SoundEvent getStepSound() {
        return null;
    }

    public SoundEvent getAttackSound() {
        return null;
    }

    public BlockPartyNPC teleport(ServerLevel level, ITeleporter teleporter) {
        if (this.changeDimension(level, teleporter) instanceof BlockPartyNPC entity) { return this.onTeleport(entity.cast()); }
        return this.cast();
    }

    public BlockPartyNPC onTeleport(BlockPartyNPC entity) {
        return entity.cast();
    }

    public boolean isSitting() {
        return false;
    }

    public boolean isLocal() {
        return !this.isRemote();
    }

    public boolean isRemote() {
        return this.level.isClientSide();
    }

    protected float[] getRGB(int hex) {
        float r = ((hex & 0xff0000) >> 16) / 255.0F;
        float g = ((hex & 0xff00) >> 8) / 255.0F;
        float b = ((hex & 0xff) >> 1) / 255.0F;
        return new float[] { r, g, b };
    }

    public boolean isTimeBetween(int start, int end) {
        long time = this.level.getDayTime();
        return start <= time && time <= end;
    }

    public BlockPartyNPC cast() {
        return (BlockPartyNPC) this;
    }
}
