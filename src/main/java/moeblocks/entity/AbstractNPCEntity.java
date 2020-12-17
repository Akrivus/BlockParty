package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.*;
import moeblocks.client.Animations;
import moeblocks.client.animation.Animation;
import moeblocks.client.animation.state.Default;
import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.DatingData;
import moeblocks.datingsim.DatingSim;
import moeblocks.entity.ai.VoiceLines;
import moeblocks.entity.ai.goal.*;
import moeblocks.entity.ai.goal.attack.BasicAttackGoal;
import moeblocks.entity.ai.goal.items.ConsumeGoal;
import moeblocks.entity.ai.goal.target.RevengeTarget;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractNPCEntity extends CreatureEntity implements IInventoryChangedListener, INamedContainerProvider {
    public static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> FAMILY_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> GIVEN_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    protected Queue<Consumer<AbstractNPCEntity>> nextTickOps;
    protected HashMap<Class<? extends IStateEnum>, Automaton> states;
    protected Animation animation = new Default();
    protected PlayerEntity emotionTarget;
    protected PlayerEntity interactTarget;
    protected PlayerEntity stareTarget;
    protected LivingEntity avoidTarget;
    protected LivingEntity followTarget;
    protected UUID followTargetUUID;
    protected PlayerEntity protagonist;
    protected UUID protagonistUUID;
    protected BlockState blockTarget;
    protected ChunkPos lastRecordedPos;
    protected int timeOfAvoid;
    protected int timeOfInteraction;
    protected int timeOfStare;
    protected int timeSinceLastInteraction;
    protected int timeSinceSleep;
    protected int timeUntilHungry;
    protected int timeUntilLove;
    protected long age;
    private float hunger = 20.0F;
    private float saturation = 5.0F;
    private float exhaustion;
    private float love;
    private float infatuation;
    private float stress;
    private float relaxation;
    private float progress;

    protected AbstractNPCEntity(EntityType<? extends AbstractNPCEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.lastRecordedPos = new ChunkPos(0, 0);
        this.nextTickOps = new LinkedList<>();
        this.stepHeight = 1.0F;
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getGivenName(), this.getHonorific());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.canFly() && source == DamageSource.FALL;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    public boolean canFly() {
        return this.moveController instanceof FlyingMovementController;
    }

    public String getHonorific() {
        return this.getGender() == Genders.FEMININE ? "chan" : "kun";
    }

    public String getGivenName() {
        String name = this.dataManager.get(GIVEN_NAME);
        if (name == null) {
            name = this.getGender().toString();
            this.setGivenName(name);
        }
        return name;
    }

    public Genders getGender() {
        return Genders.FEMININE;
    }

    public void setGivenName(String name) {
        this.dataManager.set(GIVEN_NAME, name);
    }

    public void registerStates() {
        this.states.put(BloodTypes.class, new Automaton(this, BloodTypes.O));
        this.states.put(Deres.class, new Automaton(this, Deres.KUUDERE));
        this.states.put(Emotions.class, new Automaton(this, Emotions.NORMAL));
        this.states.put(Genders.class, new Automaton(this, Genders.FEMININE));
        this.states.put(HealthStates.class, new Automaton(this, HealthStates.PERFECT));
        this.states.put(HungerStates.class, new Automaton(this, HungerStates.SATISFIED));
        this.states.put(ItemStates.class, new Automaton(this, ItemStates.DEFAULT));
        this.states.put(LoveStates.class, new Automaton(this, LoveStates.FRIENDLY));
        this.states.put(MoonPhases.class, new Automaton(this, MoonPhases.FULL));
        this.states.put(PeriodsOfTime.class, new Automaton(this, PeriodsOfTime.ATTACHED));
        this.states.put(StressStates.class, new Automaton(this, StressStates.RELAXED));
        this.states.put(TimesOfDay.class, new Automaton(this, TimesOfDay.MORNING));
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0x0, new OpenDoorGoal(this));
        this.goalSelector.addGoal(0x0, new SleepGoal(this));
        this.goalSelector.addGoal(0x0, new SwimGoal(this));
        this.goalSelector.addGoal(0x1, new LookAtInteractiveGoal(this));
        this.goalSelector.addGoal(0x2, new BasicAttackGoal(this));
        this.goalSelector.addGoal(0x3, new ConsumeGoal(this));
        this.goalSelector.addGoal(0x4, new AvoidTargetGoal(this));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this, (stack) -> stack.isFood()));
        this.goalSelector.addGoal(0x6, new FollowTargetGoal(this));
        this.goalSelector.addGoal(0x8, new FindBedGoal(this));
        this.goalSelector.addGoal(0xA, new StayHomeGoal(this));
        this.goalSelector.addGoal(0xB, new WanderGoal(this));
        this.targetSelector.addGoal(0x1, new RevengeTarget(this));
        this.states = new HashMap<>();
        this.registerStates();
    }

    @Override
    public void registerData() {
        this.dataManager.register(ANIMATION, Animations.DEFAULT.name());
        this.dataManager.register(BLOOD_TYPE, BloodTypes.O.name());
        this.dataManager.register(DERE, Deres.HIMEDERE.name());
        this.dataManager.register(EMOTION, Emotions.NORMAL.name());
        this.dataManager.register(FAMILY_NAME, "Moe");
        this.dataManager.register(GIVEN_NAME, "Kawaii");
        this.dataManager.register(SITTING, false);
        super.registerData();
    }

    @Override
    public int getTalkInterval() {
        return 150 + this.rand.nextInt(150);
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
        case 111:
            this.setParticles(ParticleTypes.EFFECT);
            return;
        case 103:
            this.setParticles(ParticleTypes.SPLASH);
            return;
        case 104:
        case 105:
        case 114:
            this.setParticles(ParticleTypes.SMOKE);
            return;
        case 106:
        case 113:
            this.setParticles(ParticleTypes.HEART);
            return;
        case 107:
            return;
        case 108:
        case 110:
        case 112:
            this.setParticles(ParticleTypes.CRIT);
            return;
        default:
            super.handleStatusUpdate(id);
        }
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

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) { return VoiceLines.SLEEPING.get(this); }
        return VoiceLines.NEUTRAL.get(this);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        if (this.followTargetUUID != null) { compound.putUniqueId("FollowTarget", this.followTargetUUID); }
        if (this.protagonistUUID != null) { compound.putUniqueId("Protagonist", this.protagonistUUID); }
        super.writeAdditional(compound);
        compound.putString("Animation", this.getAnimation().name());
        compound.putLong("HomePosition", this.getHomePosition().toLong());
        compound.putLong("LastRecordedPosition", this.lastRecordedPos.asLong());
        compound.putInt("TimeSinceLastInteraction", this.timeSinceLastInteraction);
        compound.putInt("TimeSinceSleep", this.timeSinceSleep);
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLove", this.timeUntilLove);
        this.writeCharacter(compound);
    }

    public void writeCharacter(CompoundNBT compound) {
        compound.putLong("Age", this.age);
        compound.putString("BloodType", this.getBloodType().name());
        compound.putString("Dere", this.getDere().name());
        compound.putString("Emotion", this.getEmotion().name());
        compound.putFloat("Exhaustion", this.exhaustion);
        compound.putFloat("Love", this.love);
        compound.putFloat("Infatuation", this.infatuation);
        compound.putString("FamilyName", this.getFamilyName());
        compound.putString("GivenName", this.getGivenName());
        compound.putFloat("Health", this.getHealth());
        compound.putFloat("Hunger", this.hunger);
        compound.putFloat("Relaxation", this.relaxation);
        compound.putFloat("Saturation", this.saturation);
        compound.putFloat("Stress", this.stress);
        compound.putFloat("Progress", this.progress);
    }

    public String getFamilyName() {
        return this.dataManager.get(FAMILY_NAME);
    }

    public void setFamilyName(String name) {
        this.dataManager.set(FAMILY_NAME, name);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("FollowTarget")) { this.setFollowTarget(compound.getUniqueId("FollowTarget")); }
        if (compound.hasUniqueId("Protagonist")) { this.setProtagonist(compound.getUniqueId("Protagonist")); }
        super.readAdditional(compound);
        this.setAnimation(Animations.valueOf(compound.getString("Animation")));
        this.setHomePosition(BlockPos.fromLong(compound.getLong("HomePosition")));
        this.lastRecordedPos = new ChunkPos(compound.getLong("LastRecordedPosition"));
        this.timeSinceLastInteraction = compound.getInt("TimeSinceLastInteraction");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLove = compound.getInt("TimeUntilLove");
        this.readCharacter(compound);
    }

    public void readCharacter(CompoundNBT compound) {
        this.age = compound.getInt("Age");
        this.setBloodType(BloodTypes.valueOf(compound.getString("BloodType")));
        this.setDere(Deres.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotions.valueOf(compound.getString("Emotion")));
        this.exhaustion = compound.getFloat("Exhaustion");
        this.love = compound.getFloat("Love");
        this.infatuation = compound.getFloat("Infatuation");
        this.setFamilyName(compound.getString("FamilyName"));
        this.setGivenName(compound.getString("GivenName"));
        this.hunger = compound.getFloat("Hunger");
        this.setHealth(compound.getFloat("Health"));
        this.relaxation = compound.getFloat("Relaxation");
        this.saturation = compound.getFloat("Saturation");
        this.stress = compound.getFloat("Stress");
        this.progress = compound.getFloat("Progress");
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        this.entityDropItem(MoeItems.MOE_DIE.get());
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            this.entityDropItem(this.getItemStackFromSlot(slot));
        }
    }

    @Override
    public void livingTick() {
        this.updateArmSwingProgress();
        this.getAnimation().tick(this);
        super.livingTick();
        if (this.isLocal()) {
            if (++this.timeSinceSleep > 24000) { this.addStress(0.0005F); }
            this.states.forEach((state, machine) -> machine.update());
            this.updateStareState();
            this.updateHungerState();
            this.updateLoveState();
            ++this.age;
            ChunkPos pos = this.getChunkPosition();
            if (!pos.equals(this.lastRecordedPos)) {
                this.lastRecordedPos = pos;
                this.setCharacterData();
            }
        }
    }

    public void setHomePosition(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int) this.getMaximumHomeDistance());
    }

    public void setEmotion(Emotions emotion) {
        this.setEmotion(emotion, null);
    }

    public void setEmotion(Emotions emotion, PlayerEntity entity) {
        this.dataManager.set(EMOTION, emotion.name());
        this.emotionTarget = entity;
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    public Emotions getEmotion() {
        return Emotions.valueOf(this.dataManager.get(EMOTION));
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setAnimation(Animations animation) {
        this.dataManager.set(ANIMATION, animation.name());
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

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) { super.updateFallState(y, onGround, state, pos); }
    }

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.setCharacter((npc) -> npc.setDead(true));
        super.onDeath(cause);
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
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return !this.canFly() || super.onLivingFall(distance, damageMultiplier);
    }

    @Override
    public void swingArm(Hand hand) {
        this.addExhaustion(0.1F);
        super.swingArm(hand);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public void jump() {
        this.addExhaustion(this.isSprinting() ? 0.2F : 0.05F);
        super.jump();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.animation = Animations.valueOf(this.dataManager.get(ANIMATION)).get(); }
        if (BLOOD_TYPE.equals(key)) { this.setNextState(BloodTypes.class, this.getBloodType()); }
        if (DERE.equals(key)) { this.setNextState(Deres.class, this.getDere()); }
        if (EMOTION.equals(key)) { this.setNextState(Emotions.class, this.getEmotion()); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void onItemUseFinish() {
        this.consume(this.activeItemStack);
        super.onItemUseFinish();
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.setHomePosition(pos);
        this.addStress(-0.0001F);
        this.timeSinceSleep = 0;
        super.startSleeping(pos);
    }

    public void addStress(float stress) {
        this.setStress(this.stress + stress);
    }

    public void consume(ItemStack stack) {
        if (this.canConsume(stack)) {
            Food food = stack.getItem().getFood();
            this.addSaturation(food.getSaturation());
            this.addHungerLevel(food.getHealing());
            food.getEffects().forEach(pair -> {
                if (pair.getFirst() != null && this.world.rand.nextFloat() < pair.getSecond()) {
                    this.addPotionEffect(pair.getFirst());
                }
            });
        }
    }

    public void addHungerLevel(float hungerLevel) {
        this.hunger = Math.min(this.hunger + hungerLevel, 20.0F);
    }

    public void addSaturation(float saturation) {
        this.saturation = Math.min(this.saturation + saturation, 20.0F);
    }

    public boolean canConsume(ItemStack stack) {
        return stack.isFood() && (this.isHungry() || stack.getItem().getFood().canEatWhenFull());
    }

    public boolean isHungry() {
        return this.hunger < 19.0F;
    }

    public void setNextState(Class<? extends IStateEnum> key, IStateEnum state, int timeout) {
        this.addNextTickOp((entity) -> this.states.get(key).setNextState(state, timeout));
    }

    public void setNextState(Class<? extends IStateEnum> key, IStateEnum state) {
        this.addNextTickOp((entity) -> this.states.get(key).setNextState(state));
    }

    public void addNextTickOp(Consumer<AbstractNPCEntity> op) {
        this.nextTickOps.add(op);
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }

    public void attackEntityFromRange(LivingEntity victim, double factor) {
        AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, this.getHeldItem(Hand.OFF_HAND), (float) factor);
        double dX = victim.getPosX() - this.getPosX();
        double dY = victim.getPosYHeight(3 / 10) - arrow.getPosY();
        double dZ = victim.getPosZ() - this.getPosZ();
        double d = MathHelper.sqrt(dX * dX + dZ * dZ);
        arrow.shoot(dX, dY + d * 0.2, dZ, 1.6F, 1.0F);
        arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(arrow);
        this.getHeldItem(Hand.OFF_HAND).shrink(1);
    }

    public boolean canSee(Entity entity) {
        if (this.canBeTarget(entity) && this.getEntitySenses().canSee(entity)) {
            this.getLookController().setLookPositionWithEntity(entity, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
            return true;
        }
        return false;
    }

    public boolean canBeTarget(Entity target) {
        return target != null && target.isAlive() && !target.equals(this);
    }

    public boolean canWander() {
        return this.isWithinHomingDistance() && !this.isOccupied();
    }

    public boolean isWithinHomingDistance() {
        return 16 > this.getHomeDistance() || this.getHomeDistance() > 256;
    }

    public double getHomeDistance() {
        return Math.sqrt(this.getDistanceSq(Vector3d.copyCentered(this.getHomePosition())));
    }

    public BlockState getBlockState(BlockPos pos) {
        IChunk chunk = this.getChunk(new ChunkPos(pos));
        return chunk == null ? Blocks.AIR.getDefaultState() : chunk.getBlockState(pos);
    }

    public IChunk getChunk(ChunkPos pos) {
        return this.isThreadSafe() ? this.world.getChunk(pos.x, pos.z, ChunkStatus.FULL, false) : null;
    }

    public boolean isThreadSafe() {
        return this.world.getServer().getExecutionThread().equals(Thread.currentThread());
    }

    public double getGaussian(double factor) {
        return this.rand.nextGaussian() * factor;
    }

    public float getStrikingDistance(Entity target) {
        return this.getStrikingDistance(target.getWidth());
    }

    public float getStrikingDistance(float distance) {
        return (float) (Math.pow(this.getWidth() * 2.0F, 2) + distance);
    }

    public boolean hasAmmo() {
        return this.isAmmo(this.getHeldItem(Hand.OFF_HAND).getItem());
    }

    public boolean isAmmo(Item item) {
        return item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        if (!this.canBeTarget(entity)) { return false; }
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public boolean isCompatible(AbstractNPCEntity entity) {
        return BloodTypes.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public int getTimeSinceLastInteraction() {
        return this.timeSinceLastInteraction;
    }
    
    public int getTimeSinceSleep() {
        return this.timeSinceSleep;
    }

    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem().isIn(MoeTags.ADMIN)) { return ActionResultType.FAIL; }
        if (this.isRemote() || hand != Hand.MAIN_HAND) { return ActionResultType.PASS; }
        this.setFollowTarget(player.equals(this.getFollowTarget()) ? null : player);
        return ActionResultType.SUCCESS;
    }

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    public void say(String key, Object... params) {
        this.world.getPlayers().forEach((player) -> {
            if (player.getDistance(this) < 8.0D) { this.say(player, key, params); }
        });
    }

    public boolean tryEquipItem(ItemStack stack) {
        EquipmentSlotType slot = this.getSlotForStack(stack);
        ItemStack shift = this.getItemStackFromSlot(slot);
        int max = shift.getMaxStackSize();
        if (ItemStack.areItemsEqual(shift, stack) && ItemStack.areItemStackTagsEqual(shift, stack) && shift.getCount() < max) {
            int total = shift.getCount() + stack.getCount();
            if (total > max) {
                this.entityDropItem(stack.split(total - max));
                shift.setCount(max);
                return false;
            } else {
                shift.setCount(total);
                stack.setCount(0);
                return true;
            }
        } else if (this.shouldExchangeEquipment(stack, shift)) {
            this.entityDropItem(shift);
            this.setItemStackToSlot(slot, stack.split(1));
            return true;
        }
        return false;
    }

    @Override
    protected float getDropChance(EquipmentSlotType slot) {
        return 0.0F;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.setBloodType(BloodTypes.weigh(this.rand));
        this.resetAnimationState();
        this.setCharacterData();
        return data;
    }

    @Override
    public boolean canPickUpItem(ItemStack stack) {
        EquipmentSlotType slot = this.getSlotForStack(stack);
        ItemStack shift = this.getItemStackFromSlot(slot);
        int max = shift.getMaxStackSize();
        if (ItemStack.areItemsEqual(shift, stack) && ItemStack.areItemStackTagsEqual(shift, stack) && shift.getCount() < max) {
            return shift.getCount() + stack.getCount() <= max;
        }
        return this.shouldExchangeEquipment(stack, shift);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ActionResultType result = this.onInteract(player, player.getHeldItem(hand), hand);
        if (result.isSuccessOrConsume()) { this.setInteractTarget(player); }
        this.setCharacterData();
        return result;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            this.playSound(VoiceLines.ATTACK.get(this));
            return true;
        }
        return false;
    }

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

    public PlayerEntity getProtagonist() {
        if (this.protagonistUUID == null) { return null; }
        if (this.protagonist == null) {
            this.protagonist = this.world.getPlayerByUuid(this.protagonistUUID);
        }
        return this.protagonist;
    }

    public void setProtagonist(PlayerEntity player) {
        this.setCharacter((npc) -> npc.setDead(true));
        this.setProtagonist(player != null ? player.getUniqueID() : null);
        this.setCharacterData();
        this.protagonist = player;
    }

    public void setProtagonist(UUID uuid) {
        this.protagonistUUID = uuid;
    }

    public DatingSim getDatingSim() {
        return DatingData.get(this.world, this.protagonistUUID);
    }

    public CacheNPC getCharacter() {
        return this.getDatingSim().getNPC(this.getUniqueID(), this);
    }

    public void setCharacter(Consumer<CacheNPC> transaction) {
        CacheNPC character = this.getCharacter();
        if (character == null) { return; }
        character.set(DatingData.get(this.world), transaction);
    }

    public void setCharacterData() {
        this.setCharacter((npc) -> npc.sync(this));
    }

    public LivingEntity getEntityFromUUID(UUID uuid) {
        return getEntityFromUUID(LivingEntity.class, this.world, uuid);
    }

    public static <T extends LivingEntity> T getEntityFromUUID(Class<T> type, World world, UUID uuid) {
        if (uuid != null && world instanceof ServerWorld) {
            BlockPos moe = CacheNPC.positions.get(uuid);
            int eX, bX, x, eZ, bZ, z;
            eX = 16 + (bX = 16 * (x = moe.getX() << 4));
            eZ = 16 + (bZ = 16 * (z = moe.getZ() << 4));
            IChunk chunk = world.getChunk(x, z, ChunkStatus.FULL, false);
            if (chunk == null) { return null; }
            List<T> entities = world.getLoadedEntitiesWithinAABB(type, new AxisAlignedBB(bX, 0, bZ, eX, 255, eZ), (entity) -> entity.getUniqueID().equals(uuid));
            if (entities.size() > 0) { return entities.get(0); }
        }
        return null;
    }

    public void setFollowTarget(LivingEntity target) {
        this.setFollowTarget(target != null ? target.getUniqueID() : null);
        this.followTarget = target;
        this.resetAnimationState();
    }

    public void setFollowTarget(UUID target) {
        this.followTargetUUID = target;
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        if (EnchantmentHelper.hasBindingCurse(existing)) { return false; }
        EquipmentSlotType slot = this.getSlotForStack(candidate);
        if (slot == EquipmentSlotType.OFFHAND) {
            if (this.canConsume(candidate)) { return true; }
            if (this.isWieldingBow()) { return candidate.getItem().isIn(ItemTags.ARROWS); }
            return false;
        }
        if (existing.getItem().getClass() == candidate.getItem().getClass()) {
            if (existing.getItem() instanceof TieredItem) {
                IItemTier a = ((TieredItem) candidate.getItem()).getTier();
                IItemTier b = ((TieredItem) existing.getItem()).getTier();
                return a.getAttackDamage() > b.getAttackDamage();
            }
            if (existing.getItem() instanceof ArmorItem) {
                ArmorItem a = (ArmorItem) candidate.getItem();
                ArmorItem b = (ArmorItem) existing.getItem();
                return a.getDamageReduceAmount() > b.getDamageReduceAmount();
            }
        }
        return existing.isEmpty();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void updateAITasks() {
        Consumer<AbstractNPCEntity> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
    }

    public Automaton getState(Class<? extends IStateEnum> state) {
        return this.states.get(state);
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (stack.getItem().isIn(MoeTags.OFFHAND) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public void updateStareState() {
        PlayerEntity player = this.world.getClosestPlayer(this, 8.0D);
        if (this.isBeingWatchedBy(player)) { this.setStareTarget(player); }
    }

    public void updateHungerState() {
        if (--this.timeUntilHungry < 0) {
            if (this.exhaustion > 4.0F) {
                this.exhaustion -= 4.0F;
                this.saturation -= 1.0F;
                if (this.saturation < 0) {
                    this.saturation = 0.0F;
                    this.hunger -= 1.0F;
                }
            }
            if (this.saturation > 0.0F) {
                this.timeUntilHungry = 80;
                this.addExhaustion(6.0F);
                this.heal(1.0F);
            }
        }
    }

    public void updateLoveState() {
        if (--this.timeUntilLove < 0) {
            this.infatuation -= this.stress;
            this.love += this.infatuation;
            this.timeUntilLove = 1000;
            if (this.infatuation < 0) {
                this.infatuation = 0;
            }
        }
    }

    public void setCanFly(boolean fly) {
        this.setMoveController(fly ? new FlyingMovementController(this, 10, false) : new MovementController(this));
        this.setNavigator(fly ? new FlyingPathNavigator(this, this.world) : new GroundPathNavigator(this, this.world));
    }

    public void setMoveController(MovementController moveController) {
        this.moveController = moveController;
    }

    public void setNavigator(PathNavigator navigator) {
        this.navigator = navigator;
        if (this.navigator instanceof GroundPathNavigator) {
            ((GroundPathNavigator) this.navigator).setBreakDoors(true);
        }
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int) (this.world.getGameTime() - this.age) / 24000 / 366;
    }

    public int getAttackCooldown() {
        return (int) (this.getAttribute(Attributes.ATTACK_SPEED).getValue() * 4.0F);
    }

    public abstract int getBaseAge();

    public BlockState getBlockData() {
        return Blocks.AIR.getDefaultState();
    }

    public CompoundNBT getExtraBlockData() {
        return new CompoundNBT();
    }

    public float getScale() {
        return 1.0F;
    }

    public float getBlockStrikingDistance() {
        return this.getStrikingDistance(1.0F);
    }

    public BlockState getBlockTarget() {
        return this.blockTarget;
    }

    public void setBlockTarget(BlockState target) {
        this.blockTarget = target;
    }

    public double getCenteredRandomPosX() {
        return this.getPosXRandom(this.getWidth() / 2.0F);
    }

    public double getCenteredRandomPosY() {
        return this.getPosYRandom() + this.getHeight() / 2.0F;
    }

    public double getCenteredRandomPosZ() {
        return this.getPosZRandom(this.getWidth() / 2.0F);
    }

    public ChunkPos getChunkPosition() {
        return new ChunkPos(this.getPosition());
    }

    public String getFullName() {
        return String.format("%s %s", this.getFamilyName(), this.getGivenName());
    }

    public float getHunger() {
        return this.hunger;
    }

    public float getLove() {
        return this.love;
    }

    public void setLoveBoost(float love) {
        this.infatuation += love;
    }

    public PlayerEntity getInteractTarget() {
        return this.interactTarget;
    }

    public void setInteractTarget(PlayerEntity player) {
        this.interactTarget = player;
        this.timeOfInteraction = this.ticksExisted;
    }

    public LivingEntity getStareTarget() {
        return this.stareTarget;
    }

    public void setStareTarget(PlayerEntity player) {
        this.stareTarget = player;
        this.timeOfStare = this.ticksExisted;
    }

    public float getStress() {
        return this.stress;
    }

    public void setStress(float stress) {
        this.stress = Math.max(Math.min(stress, 20.0F), 0.0F);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public StoryStates getStory() {
        return (StoryStates) this.getState(StoryStates.class).getToken();
    }

    public boolean isAtEase() {
        return !this.isFighting();
    }

    public boolean isFighting() {
        return this.canBeTarget(this.getAttackTarget()) || this.canBeTarget(this.getRevengeTarget()) || this.canBeTarget(this.getAvoidTarget());
    }

    public LivingEntity getAvoidTarget() {
        return this.avoidTarget;
    }

    public void setAvoidTarget(LivingEntity target) {
        this.avoidTarget = target;
        this.timeOfAvoid = this.ticksExisted;
    }

    public boolean isAttacking() {
        return this.canBeTarget(this.getAttackTarget());
    }

    public boolean isAvoiding() {
        return this.ticksExisted - this.timeOfAvoid < 500;
    }

    public boolean isFull() {
        return this.hunger > 19.0F;
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING) || this.isPassenger() && (this.getRidingEntity() != null && this.getRidingEntity().shouldRiderSit());
    }

    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
    }

    public boolean isStared() {
        return this.ticksExisted - this.timeOfStare < 20;
    }

    public boolean isTimeToSleep() {
        if (this.canFight() && this.isNightWatch()) { return false; }
        return this.isOccupied() && !this.world.isDaytime();
    }

    public boolean canFight() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item.isIn(MoeTags.WEAPONS);
    }

    public boolean isOccupied() {
        return this.isFighting() || this.isVengeful() || this.isSleeping() || this.isFollowing() || this.isInteracted() || this.hasPath();
    }

    public boolean isVengeful() {
        return this.ticksExisted - this.getRevengeTimer() < 500;
    }

    public boolean isInteracted() {
        return this.ticksExisted - this.timeOfInteraction < 20;
    }

    public boolean isNightWatch() {
        return this.world.getGameTime() / 24000 % 4 == this.getBloodType().ordinal();
    }

}
