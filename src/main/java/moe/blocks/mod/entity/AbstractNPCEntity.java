package moe.blocks.mod.entity;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.animation.state.Default;
import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.data.dating.Interactions;
import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.data.dating.Tropes;
import moe.blocks.mod.entity.ai.BloodTypes;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.ai.automata.state.ItemStates;
import moe.blocks.mod.entity.ai.goal.*;
import moe.blocks.mod.entity.ai.goal.attack.BasicAttackGoal;
import moe.blocks.mod.entity.ai.goal.items.ConsumeGoal;
import moe.blocks.mod.entity.ai.goal.target.RevengeTarget;
import moe.blocks.mod.entity.ai.routines.Waypoint;
import moe.blocks.mod.entity.ai.trigger.Trigger;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeTags;
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
import net.minecraft.nbt.ListNBT;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
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
    public boolean isInYearbook = false;
    protected final Queue<Consumer<AbstractNPCEntity>> nextTickOps;
    protected final List<Relationship> relationships;
    protected final List<Waypoint> waypoints;
    protected final HashMap<States, State> states;
    protected ChunkPos lastRecordedPos;
    protected String givenName;
    protected int timeOfAvoid;
    protected int timeOfInteraction;
    protected int timeOfStare;
    protected int timeUntilEmotionExpires;
    protected int timeUntilTriggered;
    protected int timeSinceSleep;
    protected long age;
    protected Animation animation = new Default();
    protected PlayerEntity emotionTarget;
    protected PlayerEntity interactTarget;
    protected PlayerEntity stareTarget;
    protected LivingEntity avoidTarget;
    protected LivingEntity followTarget;
    protected UUID followTargetUUID;
    protected BlockState blockTarget;
    private float hunger = 20.0F;
    private float saturation = 5.0F;
    private float exhaustion;
    private float stress;
    private float relaxation;
    private boolean updateItemState = true;

    protected AbstractNPCEntity(EntityType<? extends AbstractNPCEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.nextTickOps = new LinkedList<>();
        this.relationships = new ArrayList<>();
        this.waypoints = new ArrayList<>();
        this.states = new HashMap<>();
        this.states.put(States.DERE, Deres.HIMEDERE.state.start(this));
        this.states.put(States.EMOTION, Emotions.NORMAL.state.start(this));
        this.states.put(States.HELD_ITEM, null);
        this.states.put(States.REACTION, null);
        this.stepHeight = 1.0F;
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getFamilyName(), this.getHonorific());
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

    public Genders getGender() {
        return Genders.FEMININE;
    }

    public String getFamilyName() {
        return "Chara";
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
    }

    @Override
    public void registerData() {
        this.dataManager.register(ANIMATION, Animations.DEFAULT.name());
        this.dataManager.register(BLOOD_TYPE, BloodTypes.O.name());
        this.dataManager.register(DERE, Deres.HIMEDERE.name());
        this.dataManager.register(EMOTION, Emotions.NORMAL.name());
        this.dataManager.register(SITTING, false);
        super.registerData();
    }

    @Override
    public int getTalkInterval() {
        return 200 + this.rand.nextInt(400);
    }

    @Override
    public void playAmbientSound() {
        if (!this.isSleeping()) { super.playAmbientSound(); }
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
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putLong("Age", this.age);
        compound.putString("Animation", this.getAnimation().name());
        compound.putString("BloodType", this.getBloodType().name());
        compound.putString("Dere", this.getDere().name());
        compound.putString("Emotion", this.getEmotion().name());
        compound.putInt("EmotionTimer", this.timeUntilEmotionExpires);
        compound.putFloat("Exhaustion", this.exhaustion);
        if (followTargetUUID != null) { compound.putUniqueId("FollowTarget", this.followTargetUUID); }
        compound.putString("GivenName", this.getGivenName());
        compound.putFloat("Hunger", this.hunger);
        compound.putLong("HomePosition", this.getHomePosition().toLong());
        compound.putLong("LastRecordedPosition", this.lastRecordedPos.asLong());
        ListNBT relationships = new ListNBT();
        this.relationships.forEach(relationship -> relationships.add(relationship.write(new CompoundNBT())));
        compound.put("Relationships", relationships);
        ListNBT waypoints = new ListNBT();
        this.waypoints.forEach(waypoint -> waypoints.add(waypoint.write(new CompoundNBT())));
        compound.put("Waypoints", waypoints);
        compound.putFloat("Relaxation", this.relaxation);
        compound.putFloat("Saturation", this.saturation);
        compound.putFloat("Stress", this.stress);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.age = compound.getInt("Age");
        this.setAnimation(Animations.valueOf(compound.getString("Animation")));
        this.setBloodType(BloodTypes.valueOf(compound.getString("BloodType")));
        this.setDere(Deres.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotions.valueOf(compound.getString("Emotion")), compound.getInt("EmotionTimer"));
        this.exhaustion = compound.getFloat("Exhaustion");
        if (compound.hasUniqueId("FollowTarget")) { this.setFollowTarget(compound.getUniqueId("FollowTarget")); }
        this.givenName = compound.getString("GivenName");
        this.hunger = compound.getFloat("Hunger");
        this.setHomePosition(BlockPos.fromLong(compound.getLong("HomePosition")));
        this.lastRecordedPos = new ChunkPos(compound.getLong("LastRecordedPosition"));
        ListNBT relationships = compound.getList("Relationships", 10);
        relationships.forEach(relationship -> this.relationships.add(new Relationship(relationship)));
        ListNBT waypoints = compound.getList("Waypoints", 10);
        waypoints.forEach(waypoint -> this.waypoints.add(new Waypoint(waypoint)));
        this.relaxation = compound.getFloat("Relaxation");
        this.saturation = compound.getFloat("Saturation");
        this.stress = compound.getFloat("Stress");
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
            this.relationships.forEach(relationship -> relationship.tick());
            this.updateEmotionalState();
            this.updateStareState();
            ++this.age;
            ChunkPos pos = this.getChunkPosition();
            if (!pos.equals(this.lastRecordedPos)) {
                this.lastRecordedPos = pos;
                this.syncYearbooks();
            }
        }
    }

    public void setHomePosition(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int) this.getMaximumHomeDistance());
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

    public String getGivenName() {
        if (this.givenName != null) { return this.givenName; }
        return this.givenName = this.getGender().getName();
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
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) { super.updateFallState(y, onGround, state, pos); }
    }

    @Override
    public void setHealth(float health) {
        this.syncYearbooks();
        super.setHealth(health);
    }

    public void syncYearbooks() {
        if (this.isLocal() && !this.isInYearbook) { Yearbooks.sync(this); }
    }

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.syncYearbooks();
        super.onDeath(cause);
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
        if (DERE.equals(key) && this.isLocal()) { this.setNextState(States.DERE, this.getDere().state); }
        if (EMOTION.equals(key) && this.isLocal()) { this.setNextState(States.EMOTION, this.getEmotion().state); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void onItemUseFinish() {
        this.consume(this.activeItemStack);
        super.onItemUseFinish();
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.timeUntilEmotionExpires = 0;
        this.timeSinceSleep = 0;
        this.setHomePosition(pos);
        super.startSleeping(pos);
        this.addStress(-0.0001F);
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

    public void setNextState(States key, State state) {
        this.addNextTickOp((entity) -> {
            if (this.states != null) {
                if (this.states.get(key) != null) { this.states.get(key).clean(this); }
                this.states.put(key, state.start(this));
            }
        });
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

    public Relationship getRelationshipWith(UUID uuid) {
        Optional<Relationship> optional = this.relationships.stream().filter(index -> index.isUUID(uuid)).findFirst();
        if (optional.isPresent()) { return optional.get(); }
        this.relationships.add(new Relationship(uuid));
        return this.getRelationshipWith(uuid);
    }

    public Relationship getRelationshipWith(PlayerEntity player) {
        return this.getRelationshipWith(player.getUniqueID());
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

    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem().isIn(MoeTags.ADMIN)) { return ActionResultType.FAIL; }
        if (this.isRemote()) { return ActionResultType.PASS; }
        if (player.isSneaking()) {
            Relationship relationship = this.getRelationshipWith(player);
            this.setNextState(States.REACTION, relationship.getReaction(Interactions.HEADPAT));
            if (relationship.can(Relationship.Actions.FOLLOW)) {
                this.setFollowTarget(player.equals(this.getFollowTarget()) ? null : player);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    public CompoundNBT setPhoneContact(CompoundNBT compound) {
        compound.putString("Name", this.getGivenName());
        compound.putUniqueId("UUID", this.getUniqueID());
        return compound;
    }

    public void setWaypoint(BlockPos pos, Waypoint.Origin origin) {
        this.waypoints.add(new Waypoint(pos, origin));
    }

    public void setYearbookPage(CompoundNBT compound, UUID uuid) {
        compound.putString("GivenName", this.getGivenName());
        compound.putString("FamilyName", this.getFamilyName());
        compound.putString("Animation", Animations.IDLE.name());
        compound.putString("Emotion", Emotions.NORMAL.name());
        compound.putFloat("Health", this.getHealth());
        compound.putFloat("Hunger", this.getHunger());
        compound.putFloat("Stress", this.getStress());
        compound.putFloat("Love", this.getRelationshipWith(uuid).getLove());
        compound.putString("Dere", this.getDere().name());
        compound.putString("Status", this.getRelationshipStatus().name());
        compound.putString("BloodType", this.getBloodType().name());
        compound.putInt("AgeInYears", this.getAgeInYears());
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
    public void setItemStackToSlot(EquipmentSlotType slot, ItemStack stack) {
        this.updateItemState |= (slot == EquipmentSlotType.MAINHAND);
        super.setItemStackToSlot(slot, stack);
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
        Yearbooks.sync(this);
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
        ActionResultType result = super.func_230254_b_(player, hand);
        if (result.isSuccessOrConsume()) {
            this.setInteractTarget(player);
            this.syncYearbooks();
        }
        return result;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target);
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

    public LivingEntity getEntityFromUUID(UUID uuid) {
        return getEntityFromUUID(LivingEntity.class, this.world, uuid);
    }

    public static <T extends LivingEntity> T getEntityFromUUID(Class<T> type, World world, UUID uuid) {
        if (uuid != null && world instanceof ServerWorld) {
            BlockPos moe = Yearbooks.getInstance(world).get(uuid);
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
            return this.isFavoriteItem(candidate);
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
        return this.isFavoriteItem(candidate) || existing.isEmpty();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void updateAITasks() {
        if (this.updateItemState) { this.updateItemState(); }
        Consumer<AbstractNPCEntity> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
    }

    public void updateItemState() {
        this.setNextState(States.HELD_ITEM, ItemStates.get(this.getHeldItem(Hand.MAIN_HAND)).state);
        this.updateItemState = false;
    }

    public boolean isFavoriteItem(ItemStack stack) {
        return this.getDere().isFavorite(stack);
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

    public void updateEmotionalState() {
        if (--this.timeUntilEmotionExpires < 0) { this.setEmotion(Emotions.NORMAL, this.getTalkInterval()); }
        for (int i = 0; --this.timeUntilTriggered < 0 && i < Trigger.REGISTRY.size(); ++i) {
            this.timeUntilTriggered = Trigger.REGISTRY.get(i).fire(this);
        }
    }

    public void updateStareState() {
        PlayerEntity player = this.world.getClosestPlayer(this, 8.0D);
        if (this.isBeingWatchedBy(player)) { this.setStareTarget(player); }
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
        this.syncYearbooks();
    }

    public Tropes getTrope() {
        return Tropes.get(this.getDere(), this.getBloodType());
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

    public boolean isDead() {
        return this.getRelationshipStatus() == Relationship.Status.DEAD;
    }

    public Relationship.Status getRelationshipStatus() {
        if (this.getHealth() <= 0.0F) { return Relationship.Status.DEAD; }
        for (Relationship r : this.relationships) {
            if (r.getPhase() == Relationship.Phases.CONFESSION) { return Relationship.Status.TAKEN; }
        }
        return Relationship.Status.SINGLE;
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
        return this.canWander() && !this.world.isDaytime();
    }

    public boolean canFight() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item.isIn(MoeTags.WEAPONS);
    }

    public boolean canWander() {
        if (this.isFighting() || this.isVengeful() || this.isSleeping() || this.isFollowing() || this.isInteracted() || this.hasPath()) { return false; }
        return 16 > this.getHomeDistance() || this.getHomeDistance() > 256;
    }

    public boolean isVengeful() {
        return this.ticksExisted - this.getRevengeTimer() < 500;
    }

    public double getHomeDistance() {
        return Math.sqrt(this.getDistanceSq(Vector3d.copyCentered(this.getHomePosition())));
    }

    public boolean isInteracted() {
        return this.ticksExisted - this.timeOfInteraction < 20;
    }

    public boolean isNightWatch() {
        return this.world.getGameTime() / 24000 % 4 == this.getBloodType().ordinal();
    }

    public enum Genders {
        MASCULINE("Akemi", "Aki", "Akifumi", "Akihisa", "Akihito", "Akinari", "Akitoshi", "Akiya", "Akiyoshi", "Akiyuki", "Arashi", "Arihiro", "Arinaga", "Asahiko", "Asao", "Asayama", "Atomu", "Atsuji", "Azuma", "Banri", "Bunji", "Chikao", "Chikara", "Choei", "Choki", "Daichi", "Daihachi", "Daijiro", "Daikichi", "Daisaku", "Daishin", "Daisuke", "Daizo", "Eiichi", "Eiji", "Eiken", "Eikichi", "Einosuke", "Eishun", "Eita", "Eizo", "Etsuji", "Fumio", "Fusao", "Gakuto", "Genjiro", "Genta", "Gentaro", "Gin", "Go", "Goichi", "Hakaru", "Haruaki", "Haruhisa", "Harunobu", "Harunori", "Haruto", "Hayanari", "Hayao", "Heizo", "Hideharu", "Hidehiko", "Hidehisa", "Hidemaro", "Hidemasa", "Hideo", "Hideomi", "Hideto", "Hideya", "Hideyo", "Hiro", "Hiroaki", "Hirokazu", "Hirokuni", "Hiromori", "Hironari", "Hirooki", "Hirotaka", "Hirotami", "Hirotoki", "Hiroya", "Hiroyasu", "Hisahito", "Hisaichi", "Hisaki", "Hisanobu", "Hisao", "Hisato", "Hisayasu", "Hisayuki", "Hokuto", "Hozumi", "Iehira", "Iemasa", "Iemon", "Iesada", "Ikki", "Inasa", "Isami", "Isao", "Issei", "Itsuo", "Itsuro", "Jiichiro", "Jinpachi", "Jo", "Joji", "Jokichi", "Junpei", "Kagenori", "Kaichi", "Kaisei", "Kamon", "Kanehiro", "Kanesuke", "Kanji", "Katsuki", "Katsuo", "Kazuharu", "Kazuhito", "Kazuki", "Kazuma", "Kazuo", "Kazutoki", "Kazuya", "Keiichi", "Keijiro", "Keiju", "Keisuke", "Keizo", "Ken", "Kenro", "Kenta", "Kentaro", "Kihachi", "Kiichiro", "Kimio", "Kinjiro", "Kinsuke", "Kisaburo", "Kisaku", "Kiyofumi", "Kiyokazu", "Kiyoshi", "Kiyoto", "Konosuke", "Koshiro", "Kozaburo", "Kumataro", "Kunihiko", "Kunitake", "Kyogo", "Kyoji", "Kyosuke", "Mahiro", "Makio", "Mamoru", "Manabu", "Mareo", "Masabumi", "Masahiro", "Masaji", "Masakata", "Masakazu", "Masakuni", "Masanaga", "Masanari", "Masanobu", "Masao", "Masatake", "Masatane", "Masatomi", "Masatomo", "Masayuki", "Masazumi", "Masujiro", "Matsuki", "Matsuo", "Michio", "Mikito", "Mineichi", "Mitsuaki", "Mitsugi", "Mitsugu", "Mitsuo", "Mochiaki", "Morihiko", "Morio", "Moritaka", "Motojiro", "Motokazu", "Motoki", "Motomu", "Motonobu", "Mototada", "Motoyasu", "Motoyuki", "Motozane", "Mukuro", "Munehiro", "Munehisa", "Munetaka", "Musashi", "Nagaharu", "Naganori", "Nagatoki", "Nagayasu", "Namio", "Nankichi", "Naohiko", "Naohiro", "Naohito", "Naotake", "Naoto", "Naoya", "Naritaka", "Nariyasu", "Nobumasa", "Nobunari", "Nobuo", "Nobusada", "Nobusuke", "Nobutada", "Nobuyasu", "Noriaki", "Norifumi", "Norihide", "Norihiko", "Norihiro", "Norihito", "Norimoto", "Norio", "Noriyasu", "Nozomu", "Reizo", "Rentaro", "Rikichi", "Rikio", "Rikiya", "Rinsho", "Risaburo", "Rokuro", "Ryohei", "Ryoichi", "Ryoji", "Ryoki", "Ryota", "Ryotaro", "Ryozo", "Ryu", "Ryuma", "Ryusaku", "Ryusei", "Ryushi", "Ryusuke", "Ryuta", "Ryutaro", "Ryuya", "Sachio", "Sadaharu", "Sadahiro", "Saiichi", "Sanji", "Sanshiro", "Satoshi", "Seigo", "Seiho", "Seijiro", "Seiki", "Seimei", "Seiya", "Sendai", "Setsuji", "Shigemi", "Shigeo", "Shigeto", "Shin", "Shingo", "Shinjo", "Shinta", "Shintaro", "Shoma", "Shota", "Shu", "Shuko", "Shungo", "Shunki", "Shunpei", "Shunsui", "Shunsuke", "Shunta", "Sogen", "Soichiro", "Sosuke", "Sota", "Suenaga", "Suguru", "Sukenobu", "Sukeyuki", "Sumihiro", "Sunao", "Susumu", "Tadaaki", "Tadahiko", "Tadahiro", "Tadamasa", "Tadami", "Tadamori", "Tadanobu", "Tadaoki", "Tadataka", "Tadateru", "Tadayo", "Taichi", "Taichiro", "Taiga", "Taiichi", "Taiji", "Taisei", "Taishin", "Taiyo", "Taizo", "Takaaki", "Takafumi", "Takahide", "Takahira", "Takahiro", "Takaji", "Takaki", "Takamasa", "Takanobu", "Takao", "Takato", "Takatomi", "Takeharu", "Takehisa", "Takehito", "Takeichi", "Takenori", "Takero", "Takeru", "Taketo", "Takuma", "Takumi", "Takumu", "Takuro", "Takuzo", "Tamotsu", "Tasuku", "Tateo", "Tatsuaki", "Tatsuji", "Tatsuma", "Tatsumi", "Tatsuya", "Tatsuzo", "Teizo", "Teruaki", "Teruhiko", "Teruki", "Terumasa", "Tetsu", "Tetsuji", "Tokuji", "Tokuo", "Tomio", "Tomoaki", "Tomoharu", "Tomohisa", "Tomohito", "Tomokazu", "Tomoki", "Tomonori", "Tomoya", "Tomoyasu", "Torahiko", "Toru", "Toshi", "Toshiaki", "Toshio", "Toya", "Toyotaro", "Toyozo", "Tsunemi", "Tsutomu", "Tsutsumi", "Tsuyoshi", "Yahiko", "Yasuharu", "Yasuhide", "Yasuhiro", "Yasuji", "Yasuki", "Yasunari", "Yasunobu", "Yasushi", "Yasutaka", "Yasutaro", "Yasutomo", "Yawara", "Yohei", "Yoichi", "Yoji", "Yoshi", "Yoshinao", "Yoshito", "Yoshiya", "Yozo", "Yugi", "Yuichi", "Yuji", "Yukichi", "Yukihiko", "Yukihiro", "Yukimura", "Yukito", "Yuma", "Yusaku", "Yushi", "Yusuke", "Yutaka", "Yuzo", "Yuzuru", "Zenji", "Zentaro", "Akiho", "Akimi", "Akira", "Anri", "Asuka", "Ayumu", "Chiaki", "Chihiro", "Hajime", "Haru", "Haruka", "Harumi", "Hatsu", "Hayate", "Hazuki", "Hibiki", "Hifumi", "Hikari", "Hikaru", "Hinata", "Hiromi", "Hiromu", "Hisaya", "Hiyori", "Hotaru", "Ibuki", "Iori", "Itsuki", "Izumi", "Jun", "Kagami", "Kaname", "Kaoru", "Katsumi", "Kayo", "Kazu", "Kazumi", "Kei", "Kou", "Kunie", "Kurumi", "Kyo", "Maiko", "Maki", "Mako", "Makoto", "Masaki", "Masami", "Masumi", "Matoi", "Mayumi", "Michi", "Michiru", "Michiyo", "Midori", "Mikoto", "Minori", "Mirai", "Misao", "Mitsue", "Mitsuki", "Mitsuru", "Mitsuyo", "Mizuho", "Mizuki", "Nagisa", "Nao", "Naomi", "Natsu", "Natsuki", "Natsuo", "Nozomi", "Rei", "Ren", "Riku", "Rin", "Rui", "Ryo", "Ryuko", "Sakae", "Satsuki", "Setsuna", "Shigeri", "Shinobu", "Shion", "Shizuka", "Sora", "Subaru", "Takemi", "Tala", "Tamaki", "Tatsuki", "Teru", "Tomo", "Tomoe", "Tomomi", "Toshimi", "Tsubasa", "Tsukasa", "Yoshika", "Yoshimi", "Yosuke", "Yu", "Yuki", "Yuri"),
        FEMININE("Ai", "Aika", "Aiko", "Aimi", "Aina", "Airi", "Akane", "Akari", "Akemi", "Akeno", "Aki", "Akie", "Akiho", "Akiko", "Akimi", "Akina", "Akira", "Akiyo", "Amane", "Ami", "Anri", "Anzu", "Aoi", "Ariko", "Arisa", "Asako", "Asami", "Asuka", "Asumi", "Asuna", "Atsuko", "Atsumi", "Aya", "Ayaka", "Ayako", "Ayame", "Ayami", "Ayana", "Ayane", "Ayano", "Ayu", "Ayuka", "Ayuko", "Ayumi", "Ayumu", "Azumi", "Azura", "Azusa", "Chiaki", "Chidori", "Chie", "Chieko", "Chiemi", "Chigusa", "Chiharu", "Chihiro", "Chiho", "Chika", "Chikage", "Chikako", "Chinami", "Chinatsu", "Chisato", "Chitose", "Chiya", "Chiyako", "Chiyo", "Chiyoko", "Chizuko", "Chizuru", "Eiko", "Eimi", "Emi", "Emika", "Emiko", "Emiri", "Eri", "Erika", "Eriko", "Erina", "Etsuko", "Fujie", "Fujiko", "Fukumi", "Fumi", "Fumie", "Fumika", "Fumiko", "Fumino", "Fumiyo", "Fusako", "Futaba", "Fuyuko", "Fuyumi", "Fuka", "Hajime", "Hana", "Hanae", "Hanako", "Haru", "Harue", "Haruhi", "Haruka", "Haruko", "Harumi", "Haruna", "Haruno", "Haruyo", "Hasumi", "Hatsu", "Hatsue", "Hatsumi", "Hayate", "Hazuki", "Hibiki", "Hideko", "Hidemi", "Hifumi", "Hikari", "Hikaru", "Himawari", "Himeko", "Hina", "Hinako", "Hinata", "Hiroe", "Hiroka", "Hiroko", "Hiromi", "Hiromu", "Hiroyo", "Hisa", "Hisae", "Hisako", "Hisaya", "Hisayo", "Hitomi", "Hiyori", "Honami", "Honoka", "Hotaru", "Ibuki", "Ichiko", "Ikue", "Ikuko", "Ikumi", "Ikuyo", "Io", "Iori", "Itsuki", "Itsuko", "Itsumi", "Izumi", "Jitsuko", "Jun", "Junko", "Juri", "Kagami", "Kaguya", "Kaho", "Kahori", "Kahoru", "Kana", "Kanae", "Kanako", "Kaname", "Kanami", "Kanna", "Kanoko", "Kaori", "Kaoru", "Kaoruko", "Karen", "Karin", "Kasumi", "Katsuko", "Katsumi", "Kawai", "Kaya", "Kayo", "Kayoko", "Kazu", "Kazue", "Kazuha", "Kazuko", "Kazumi", "Kazusa", "Kazuyo", "Kei", "Keiki", "Keiko", "Kiho", "Kiko", "Kikue", "Kikuko", "Kimi", "Kimiko", "Kinuko", "Kira", "Kiyoko", "Koharu", "Komako", "Konomi", "Kotoe", "Kotomi", "Kotono", "Kotori", "Kou", "Kozue", "Kumi", "Kumiko", "Kunie", "Kuniko", "Kurenai", "Kuriko", "Kurumi", "Kyo", "Kyoko", "Maaya", "Machi", "Machiko", "Madoka", "Maho", "Mai", "Maiko", "Maki", "Makiko", "Mako", "Makoto", "Mami", "Mamiko", "Mana", "Manaka", "Manami", "Mao", "Mari", "Marie", "Marika", "Mariko", "Marina", "Masae", "Masaki", "Masako", "Masami", "Masayo", "Masumi", "Matoi", "Matsuko", "Mayako", "Mayo", "Mayu", "Mayuka", "Mayuko", "Mayumi", "Megu", "Megumi", "Mei", "Meiko", "Meisa", "Michi", "Michiko", "Michiru", "Michiyo", "Midori", "Mie", "Mieko", "Miharu", "Miho", "Mihoko", "Miiko", "Mika", "Mikako", "Miki", "Mikiko", "Mikoto", "Miku", "Mikuru", "Mimori", "Mina", "Minae", "Minako", "Minami", "Mineko", "Minori", "Mio", "Miori", "Mira", "Mirai", "Misaki", "Misako", "Misao", "Misato", "Misumi", "Misuzu", "Mitsue", "Mitsuki", "Mitsuko", "Mitsuru", "Mitsuyo", "Miu", "Miwa", "Miwako", "Miya", "Miyabi", "Miyako", "Miyo", "Miyoko", "Miyoshi", "Miyu", "Miyuki", "Miyumi", "Miyu", "Mizue", "Mizuho", "Mizuki", "Mizuko", "Moe", "Moeka", "Moeko", "Momo", "Momoe", "Momoka", "Momoko", "Motoko", "Mutsuko", "Mutsumi", "Nagako", "Nagisa", "Naho", "Nako", "Nami", "Nana", "Nanae", "Nanako", "Nanami", "Nanase", "Nao", "Naoko", "Naomi", "Narumi", "Natsue", "Natsuki", "Natsuko", "Natsume", "Natsumi", "Natsuo", "Noa", "Nobue", "Nobuko", "Nodoka", "Nonoka", "Noriko", "Noriyo", "Nozomi", "Omi", "Otoha", "Otome", "Ran", "Ranko", "Rei", "Reika", "Reiko", "Reina", "Ren", "Rena", "Reona", "Rie", "Rieko", "Riho", "Rika", "Rikako", "Riko", "Riku", "Rin", "Rina", "Rino", "Rio", "Risa", "Risako", "Ritsuko", "Rui", "Rumi", "Rumiko", "Runa", "Ruri", "Ruriko", "Ryo", "Ryoko", "Ryuko", "Ryoka", "Sachi", "Sachie", "Sachiko", "Sadako", "Sae", "Saeko", "Saiko", "Sakae", "Saki", "Sakie", "Sakiko", "Saku", "Sakura", "Sakurako", "Sanae", "Saori", "Sari", "Satoko", "Satomi", "Satsuki", "Sawa", "Sawako", "Saya", "Sayaka", "Sayako", "Sayo", "Sayoko", "Sayumi", "Sayuri", "Seiko", "Setsuko", "Setsuna", "Shigeko", "Shigeri", "Shiho", "Shihori", "Shiina", "Shimako", "Shinako", "Shino", "Shinobu", "Shion", "Shiori", "Shizue", "Shizuka", "Shizuko", "Shizuru", "Shuko", "Shoko", "Sonoko", "Sora", "Subaru", "Sugako", "Sumie", "Sumika", "Sumiko", "Sumire", "Suzue", "Suzuka", "Suzuko", "Taeko", "Takako", "Takayo", "Takeko", "Takemi", "Tala", "Tamaki", "Tamako", "Tamami", "Tamao", "Tamayo", "Tamiko", "Tatsuki", "Tatsuko", "Tazuko", "Teiko", "Teru", "Teruko", "Terumi", "Tokiko", "Tokuko", "Tomie", "Tomiko", "Tomo", "Tomoe", "Tomoka", "Tomoko", "Tomomi", "Tomoyo", "Toshiko", "Toshimi", "Toyoko", "Tsubasa", "Tsukasa", "Tsukiko", "Tsuneko", "Tsuru", "Umeko", "Uta", "Waka", "Wakako", "Wakana", "Yae", "Yaeko", "Yasue", "Yasuko", "Yayoi", "Yoko", "Yoriko", "Yoshika", "Yoshiko", "Yoshimi", "Yoshino", "Yu", "Yui", "Yuika", "Yuiko", "Yuka", "Yukako", "Yukari", "Yuki", "Yukie", "Yukika", "Yukiko", "Yukina", "Yukino", "Yumeko", "Yumi", "Yumie", "Yumika", "Yumiko", "Yuri", "Yuria", "Yurie", "Yurika", "Yuriko", "Yurina", "Yuumi", "Yuuna", "Yuko");

        private final String[] names;

        Genders(String... names) {
            this.names = names;
        }

        public String getName() {
            return this.names[(int) (Math.random() * this.names.length)];
        }
    }
}
