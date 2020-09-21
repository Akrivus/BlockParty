package moe.blocks.mod.entity.partial;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.animation.state.Default;
import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.ai.goal.FollowTargetGoal;
import moe.blocks.mod.entity.ai.goal.WaitGoal;
import moe.blocks.mod.util.sort.EntityDistance;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.UUID;

public abstract class InteractEntity extends NPCEntity {
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(InteractEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(InteractEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(InteractEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(InteractEntity.class, DataSerializers.STRING);
    protected Animation animation = new Default();
    protected PlayerEntity emotionTarget;
    protected PlayerEntity interactTarget;
    protected PlayerEntity stareTarget;
    protected LivingEntity followTarget;
    protected UUID followTargetUUID;
    protected BlockState blockTarget;
    private int timeUntilEmotionExpires;
    private int timeOfInteraction;
    private int timeOfStare;

    protected InteractEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0x1, new WaitGoal(this));
        this.goalSelector.addGoal(0x7, new FollowTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void registerData() {
        this.dataManager.register(ANIMATION, Animations.DEFAULT.name());
        this.dataManager.register(BLOOD_TYPE, BloodTypes.O.name());
        this.dataManager.register(DERE, Deres.HIMEDERE.name());
        this.dataManager.register(EMOTION, Emotions.NORMAL.name());
        super.registerData();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Animation", this.getAnimation().name());
        compound.putString("BloodType", this.getBloodType().name());
        compound.putString("Dere", this.getDere().name());
        compound.putString("Emotion", this.getEmotion().name());
        compound.putInt("EmotionTimer", this.timeUntilEmotionExpires);
        if (this.followTargetUUID != null) {
            compound.putUniqueId("FollowTarget", this.followTargetUUID);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setAnimation(Animations.valueOf(compound.getString("Animation")));
        this.setBloodType(BloodTypes.valueOf(compound.getString("BloodType")));
        this.setDere(Deres.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotions.valueOf(compound.getString("Emotion")), compound.getInt("EmotionTimer"));
        if (compound.hasUniqueId("FollowTarget")) {
            this.setFollowTarget(this.followTargetUUID);
        }
    }

    public void setEmotion(Emotions emotion, int timeout) {
        this.setEmotion(emotion, timeout, null);
    }

    public void setEmotion(Emotions emotion, int timeout, PlayerEntity entity) {
        this.dataManager.set(EMOTION, emotion.name());
        this.timeUntilEmotionExpires = timeout;
        this.emotionTarget = entity;
        if (entity != null) {
            this.world.setEntityState(this, (byte) emotion.id);
            this.playSound(emotion.sound);
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.isLocal()) {
            if (--this.timeUntilEmotionExpires < 0) { this.setEmotion(Emotions.NORMAL, 24000); }
            this.world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().expand(8.0F, 4.0F, 8.0F)).stream().sorted(new EntityDistance(this)).forEach(player -> {
                if (this.isBeingWatchedBy(player)) { this.setStareTarget(player); }
            });
        }
    }

    @Override
    public void registerStates(HashMap<States, State> states) {
        states.put(States.DERE, Deres.HIMEDERE.state.start(this));
        states.put(States.EMOTION, Emotions.NORMAL.state.start(this));
        super.registerStates(states);
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public Emotions getEmotion() {
        return Emotions.valueOf(this.dataManager.get(EMOTION));
    }

    public Deres getDere() {
        return Deres.valueOf(this.dataManager.get(DERE));
    }

    public void setDere(Deres dere) {
        this.dataManager.set(DERE, dere.name());
    }

    public BloodTypes getBloodType() {
        return BloodTypes.valueOf(this.dataManager.get(BLOOD_TYPE));
    }

    public void setBloodType(BloodTypes bloodType) {
        this.dataManager.set(BLOOD_TYPE, bloodType.name());
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setAnimation(Animations animation) {
        this.dataManager.set(ANIMATION, animation.name());
    }

    public PlayerEntity getInteractTarget() {
        return this.interactTarget;
    }

    public void setInteractTarget(PlayerEntity player) {
        this.interactTarget = player;
        this.timeOfInteraction = this.ticksExisted;
    }

    public boolean isInteracted() {
        return this.ticksExisted - this.timeOfInteraction < 20;
    }

    public LivingEntity getStareTarget() {
        return this.stareTarget;
    }

    public void setStareTarget(PlayerEntity player) {
        this.stareTarget = player;
        this.timeOfStare = this.ticksExisted;
    }

    public boolean isStared() {
        return this.ticksExisted - this.timeOfStare < 20;
    }

    public boolean isCompatible(InteractEntity entity) {
        return BloodTypes.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public BlockState getBlockTarget() {
        return this.blockTarget;
    }

    public void setBlockTarget(BlockState target) {
        this.blockTarget = target;
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int) (this.world.getGameTime() - this.age) / 24000 / 366;
    }

    public abstract int getBaseAge();

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.animation = Animations.valueOf(this.dataManager.get(ANIMATION)).get(); }
        if (DERE.equals(key) && this.isLocal()) { this.setNextState(States.DERE, this.getDere().state); }
        if (EMOTION.equals(key) && this.isLocal()) { this.setNextState(States.EMOTION, this.getEmotion().state); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.setHomePosAndDistance(pos, 96);
        super.startSleeping(pos);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
        case 100:
            this.setParticles(ParticleTypes.FLAME);
            return;
        case 101:
            this.setParticles(ParticleTypes.BUBBLE);
            return;
        case 102:
        case 109:
            this.setParticles(ParticleTypes.EFFECT);
            return;
        case 103:
            this.setParticles(ParticleTypes.SPLASH);
            return;
        case 104:
        case 105:
        case 112:
            this.setParticles(ParticleTypes.SMOKE);
            return;
        case 106:
        case 111:
            this.setParticles(ParticleTypes.HEART);
            return;
        case 107:
            return;
        case 108:
        case 110:
            this.setParticles(ParticleTypes.CRIT);
            return;
        default:
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRemote()) { this.getAnimation().tick(this); }
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setBloodType(BloodTypes.weigh(this.rand));
        this.resetAnimationState();
        return data;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ActionResultType result = this.onInteract(player, player.getHeldItem(hand), hand);
        if (result.isSuccessOrConsume()) { this.setInteractTarget(player); }
        return result;
    }

    public abstract ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand);

    public void resetAnimationState() {
        this.setAnimation(this.isFollowing() ? Animations.DEFAULT : Animations.IDLE);
    }

    public boolean isFollowing() {
        return this.canBeTarget(this.getFollowTarget());
    }

    public LivingEntity getFollowTarget() {
        if (this.followTargetUUID == null) { return null; }
        if (this.followTarget == null) {
            this.followTarget = this.getEntityFromUUID(this.followTargetUUID);
        }
        return this.followTarget;
    }

    public void setFollowTarget(LivingEntity target) {
        this.setFollowTarget(target != null ? target.getUniqueID() : null);
        this.followTarget = target;
        this.resetAnimationState();
    }

    public void setFollowTarget(UUID target) {
        this.followTargetUUID = target;
    }

    @OnlyIn(Dist.CLIENT)
    protected void setParticles(IParticleData particle) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(particle, this.getPosXRandom(1.0D), this.getPosYRandom() + 1.0D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }
}
