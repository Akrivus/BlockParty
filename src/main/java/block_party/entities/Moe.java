package block_party.entities;

import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.registry.resources.MoeSounds;
import block_party.scene.filters.traits.Dere;
import block_party.scene.filters.traits.Gender;
import block_party.utils.Trans;
import block_party.entities.data.HidingSpots;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Moe extends BlockPartyNPC {
    public Moe(EntityType<Moe> type, Level level) {
        super(type, level);
        this.doSyncWithDatabase(true);
    }

    public Moe(Level level) {
        this(CustomEntities.MOE.get(), level);
    }

    @Override
    public void hide() {
        HidingSpots.add(this);
        super.hide();
    }

    @Override
    public BlockPartyNPC onTeleport(BlockPartyNPC entity) {
        entity.setFollowing(true);
        MoeSounds.get(entity, MoeSounds.Sound.FOLLOW);
        return entity;
    }

    public String getFamilyName() {
        ResourceLocation block = this.getBlock().getRegistryName();
        return Trans.late(String.format("entity.block_party.%s.%s", block.getNamespace(), block.getPath()));
    }

    @Override
    public Gender getGender() {
        if (this.is(CustomTags.HAS_MALE_PRONOUNS)) { return Gender.MALE; }
        if (this.is(CustomTags.HAS_NONBINARY_PRONOUNS)) { return Gender.NONBINARY; }
        return Gender.FEMALE;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return this.is(CustomTags.HAS_CAT_FEATURES) ? MoeSounds.get(this, MoeSounds.Sound.MEOW) : super.getAmbientSound();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource cause) {
        return MoeSounds.get(this, MoeSounds.Sound.HURT);
    }

    @Override
    public SoundEvent getAttackSound() {
        return MoeSounds.get(this, MoeSounds.Sound.ATTACK);
    }

    @Override
    public SoundEvent getDeathSound() {
        return MoeSounds.get(this, MoeSounds.Sound.DEAD);
    }

    @Override
    public SoundEvent getStepSound() {
        return MoeSounds.get(this, MoeSounds.Sound.STEP);
    }

    @Override
    public SoundEvent getSpeakSound() {
        return MoeSounds.get(this.cast(), MoeSounds.Sound.SAY);
    }

    public static boolean spawn(Level level, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, Player player) {
        BlockState state = level.getBlockState(block);
        if (!state.is(CustomTags.Blocks.SPAWNS_MOES)) { return false; }
        BlockEntity extra = level.getBlockEntity(block);
        Moe moe = CustomEntities.MOE.get().create(level);
        moe.absMoveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        /**
         * TODO: Need to move this to {@link ShimenawaBlockEntity#getNewRow()} and {@link CustomSpawnEggItem#useOn(UseOnContext)}
         */
        moe.setDatabaseID(block.asLong());
        moe.setBlockState(state);
        moe.setTileEntityData(extra != null ? extra.getTileData() : new CompoundTag());
        moe.setDere(dere);
        moe.claim(player);
        if (level.addFreshEntity(moe)) {
            moe.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(spawn), MobSpawnType.TRIGGERED, null, null);
            if (player != null) { moe.setPlayer(player); }
            return level.destroyBlock(block, false);
        }
        return false;
    }
}
