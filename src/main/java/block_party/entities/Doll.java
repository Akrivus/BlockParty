package block_party.entities;

import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.registry.CustomEntities;
import block_party.registry.CustomSounds;
import block_party.registry.CustomTags;
import block_party.registry.resources.DollSounds;
import block_party.scene.filters.Dere;
import block_party.scene.filters.Gender;
import block_party.utils.Trans;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

public class Doll extends BlockPartyNPC {
    public Doll(EntityType<Doll> type, Level level) {
        super(type, level);
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
    public BlockPartyNPC onTeleport(BlockPartyNPC entity) {
        entity.setFollowing(true);
        entity.playSound(CustomSounds.NPC_FOLLOW.get());
        return entity;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return this.is(CustomTags.HAS_CAT_FEATURES) ? DollSounds.get(this, DollSounds.Sound.MEOW) : super.getAmbientSound();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource cause) {
        return DollSounds.get(this, DollSounds.Sound.HURT);
    }

    @Override
    public SoundEvent getAttackSound() {
        return DollSounds.get(this, DollSounds.Sound.ATTACK);
    }

    @Override
    public SoundEvent getDeathSound() {
        return DollSounds.get(this, DollSounds.Sound.DEAD);
    }

    @Override
    public SoundEvent getStepSound() {
        return DollSounds.get(this, DollSounds.Sound.STEP);
    }

    @Override
    public SoundEvent getSpeakSound() {
        return DollSounds.get(this.cast(), DollSounds.Sound.SAY);
    }

    public static boolean spawn(Level level, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, Player player) {
        BlockState state = level.getBlockState(block);
        if (!state.is(CustomTags.Blocks.SPAWNS_DOLLS)) { return false; }
        BlockEntity extra = level.getBlockEntity(block);
        BlockPartyNPC npc = CustomEntities.NPC.get().create(level);
        npc.absMoveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        /**
         * TODO: Need to move this to {@link ShimenawaBlockEntity#getNewRow()} and {@link CustomSpawnEggItem#useOn(UseOnContext)}
         */
        npc.setDatabaseID(block.asLong());
        npc.setBlockState(state);
        npc.setTileEntityData(extra != null ? extra.getTileData() : new CompoundTag());
        npc.setDere(dere);
        npc.claim(player);
        if (level.addFreshEntity(npc)) {
            npc.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(spawn), MobSpawnType.TRIGGERED, null, null);
            if (player != null) { npc.setPlayer(player); }
            return level.destroyBlock(block, false);
        }
        return false;
    }
}
