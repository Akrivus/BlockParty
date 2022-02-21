package block_party.entities;

import block_party.db.records.NPC;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.registry.resources.MoeSounds;
import block_party.scene.filters.traits.Gender;
import block_party.utils.Trans;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Moe extends BlockPartyNPC {
    public Moe(EntityType<Moe> type, Level level) {
        super(type, level);
        this.setGivenName(this.getGender().getUniqueName(this.level));
        this.doSyncWithDatabase(true);
    }

    public Moe(Level level) {
        this(CustomEntities.MOE.get(), level);
    }

    @Override
    public void hide(HideUntil until) {
        if (this.isRemote()) { return; }
        this.getRow().update(this, (row) -> row.get(NPC.HIDING).set(true));
        MoeInHiding ghost = new MoeInHiding(this);
        ghost.setHideUntil(until);
        if (this.level.addFreshEntity(ghost))
            super.hide(until);
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
}
