package moe.blocks.mod.entity.partial;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.ai.goal.FollowTargetGoal;
import moe.blocks.mod.util.VoiceLines;
import moe.blocks.mod.util.sort.EntityDistance;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public abstract class InteractiveEntity extends NPCEntity {
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(InteractiveEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(InteractiveEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(InteractiveEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(InteractiveEntity.class, DataSerializers.STRING);
    protected Animation animation = new Animation();
    protected LivingEntity emotionTarget;
    protected PlayerEntity interactTarget;
    protected LivingEntity stareTarget;
    protected UUID followTargetUUID;
    protected BlockState blockTarget;
    private int emotionTimer;
    private int interactTimer;
    private int stareTimer;

    protected InteractiveEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0x7, new FollowTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void registerStates(HashMap<States, State> states) {
        states.put(States.DERE, Deres.HIMEDERE.state.start(this));
        states.put(States.EMOTION, Emotions.NORMAL.state.start(this));
        super.registerStates(states);
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
        compound.putString("Animation", this.getAnimation().toString());
        compound.putString("BloodType", this.getBloodType().toString());
        compound.putString("Dere", this.getDere().toString());
        compound.putString("Emotion", this.getEmotion().toString());
        compound.putInt("EmotionTimer", this.emotionTimer);
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

    public void setEmotion(Emotions emotion, int timeout, LivingEntity entity) {
        this.dataManager.set(EMOTION, emotion.name());
        this.emotionTimer = this.ticksExisted + timeout;
        this.emotionTarget = entity;
    }

    @Override
    public void livingTick() {
        this.getAnimation().tick(this);
        super.livingTick();
        this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().expand(8.0F, 4.0F, 8.0F)).stream().sorted(new EntityDistance(this)).forEach(entity -> {
            if (this.isBeingWatchedBy(entity)) { this.setStareTarget(entity); }
        });
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.setHomePosAndDistance(pos, 96);
        super.startSleeping(pos);
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

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ActionResultType result = this.onInteract(player, player.getHeldItem(hand), hand);
        if (result.isSuccessOrConsume()) { this.setInteractTarget(player); }
        return result;
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setBloodType(BloodTypes.weigh(this.rand));
        return data;
    }

    public abstract ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand);

    public PlayerEntity getInteractTarget() {
        return this.interactTarget;
    }

    public void setInteractTarget(PlayerEntity target) {
        this.interactTarget = target;
        this.interactTimer = this.ticksExisted;
    }

    public boolean isInteracted() {
        return this.ticksExisted - this.interactTimer < 20;
    }

    public LivingEntity getStareTarget() {
        return this.stareTarget;
    }

    public void setStareTarget(LivingEntity target) {
        this.stareTarget = target;
        this.stareTimer = this.ticksExisted;
    }

    public boolean isStared() {
        return this.ticksExisted - this.stareTimer < 20;
    }

    public boolean isEmotional() {
        return this.ticksExisted - this.emotionTimer < 0;
    }

    public boolean isCompatible(InteractiveEntity entity) {
        return BloodTypes.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public void resetAnimationState() {
        this.setAnimation(this.isFollowing() ? Animations.DEFAULT : Animations.WAITING);
    }

    public boolean isFollowing() {
        return this.canBeTarget(this.getFollowTarget());
    }

    public LivingEntity getFollowTarget() {
        return this.getEntityFromUUID(this.followTargetUUID);
    }

    public void setFollowTarget(LivingEntity target) {
        this.setFollowTarget(target.getUniqueID());
    }

    public void setFollowTarget(UUID target) {
        this.followTargetUUID = target;
    }

    public void setBlockTarget(BlockState target) {
        this.blockTarget = target;
    }

    public BlockState getBlockTarget() {
        return this.blockTarget;
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int) (this.world.getGameTime() - this.age) / 24000 / 366;
    }

    public abstract int getBaseAge();

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return VoiceLines.HURT.get(this);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return VoiceLines.DEAD.get(this);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.animation = Animations.valueOf(this.dataManager.get(ANIMATION)).get(); }
        if (DERE.equals(key) && this.isLocal()) { this.setNextState(States.DERE, this.getDere().state); }
        if (EMOTION.equals(key) && this.isLocal()) { this.setNextState(States.EMOTION, this.getEmotion().state); }
        super.notifyDataManagerChange(key);
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }
}
