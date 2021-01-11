package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.enums.*;
import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.DatingData;
import moeblocks.datingsim.DatingSim;
import moeblocks.datingsim.convo.Dialogue;
import moeblocks.datingsim.convo.Scene;
import moeblocks.datingsim.convo.enums.Interaction;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.ai.*;
import moeblocks.entity.ai.attack.BasicAttackGoal;
import moeblocks.entity.ai.items.ConsumeGoal;
import moeblocks.entity.ai.target.RevengeTarget;
import moeblocks.init.MoeConvos;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeTags;
import moeblocks.message.SCloseDialogue;
import moeblocks.message.SOpenDialogue;
import moeblocks.util.ChunkScheduler;
import moeblocks.util.DimBlockPos;
import moeblocks.util.VoiceLines;
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
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
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
import net.minecraftforge.common.util.ITeleporter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractNPCEntity extends CreatureEntity {
    public static final DataParameter<Optional<UUID>> GLOBAL_UUID = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public static final DataParameter<Optional<UUID>> PROTAGONIST = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> STORY_PHASE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> FAMILY_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> GIVEN_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<Float> AFFECTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> EXHAUSTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> FOOD_LEVEL = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> LOVE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> PROGRESS = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> RELAXATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> SATURATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> STRESS = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public HashMap<Class<? extends IStateEnum>, Automaton> states;
    private final Queue<Consumer<AbstractNPCEntity>> nextTickOps;
    private final boolean isAutomatonReady;
    private PlayerEntity playerInteracted;
    private LivingEntity entityStaring;
    private LivingEntity entityToAvoid;
    private BlockState blockToMine;
    private Scene scene;
    private boolean acted;
    private int timeSinceAvoid;
    private int timeSinceInteraction;
    private int timeSinceStare;
    private int timeSinceSleep;
    private int timeUntilHungry;
    private int timeUntilLove;
    private long age;

    protected AbstractNPCEntity(EntityType<? extends AbstractNPCEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.nextTickOps = new LinkedList<>();
        this.stepHeight = 1.0F;
        this.states = new HashMap<>();
        this.registerStates();
        this.isAutomatonReady = true;
    }

    public void registerStates() {
        this.states.put(Animation.class, new Automaton<>(this, Animation.DEFAULT::trigger).setCanRunOnClient().start());
        this.states.put(BloodType.class, new Automaton<>(this, BloodType.O::trigger).setCanUpdate(false).start());
        this.states.put(Dere.class, new Automaton<>(this, Dere.NYANDERE::trigger).setCanUpdate(false).start());
        this.states.put(Emotion.class, new Automaton<>(this, Emotion.NORMAL::trigger).start());
        this.states.put(Gender.class, new Automaton<>(this, Gender.FEMININE::trigger).start());
        this.states.put(HealthState.class, new Automaton<>(this, HealthState.PERFECT::trigger).start());
        this.states.put(HungerState.class, new Automaton<>(this, HungerState.SATISFIED::trigger).start());
        this.states.put(HeldItemState.class, new Automaton<>(this, HeldItemState.DEFAULT::trigger).start());
        this.states.put(LoveState.class, new Automaton<>(this, LoveState.FRIENDLY::trigger).start());
        this.states.put(MoonPhase.class, new Automaton<>(this, MoonPhase.FULL::trigger).start());
        this.states.put(PeriodOfTime.class, new Automaton<>(this, PeriodOfTime.ATTACHED::trigger).start());
        this.states.put(StoryPhase.class, new Automaton<>(this, StoryPhase.INTRODUCTION::trigger).start());
        this.states.put(StressState.class, new Automaton<>(this, StressState.RELAXED::trigger).start());
        this.states.put(TimeOfDay.class, new Automaton<>(this, TimeOfDay.MORNING::trigger).start());
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0x0, new KeepEyeContactGoal(this));
        this.goalSelector.addGoal(0x1, new OpenDoorGoal(this));
        this.goalSelector.addGoal(0x1, new SleepGoal(this));
        this.goalSelector.addGoal(0x1, new SwimGoal(this));
        this.goalSelector.addGoal(0x2, new BasicAttackGoal<>(this));
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
        this.dataManager.register(GLOBAL_UUID, Optional.empty());
        this.dataManager.register(PROTAGONIST, Optional.empty());
        this.dataManager.register(FOLLOWING, false);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(ANIMATION, Animation.DEFAULT.toKey());
        this.dataManager.register(BLOOD_TYPE, BloodType.O.toKey());
        this.dataManager.register(DERE, Dere.NYANDERE.toKey());
        this.dataManager.register(EMOTION, Emotion.NORMAL.toKey());
        this.dataManager.register(STORY_PHASE, StoryPhase.INTRODUCTION.toKey());
        this.dataManager.register(FAMILY_NAME, "Missing");
        this.dataManager.register(GIVEN_NAME, "Name");
        this.dataManager.register(AFFECTION, 0.0F);
        this.dataManager.register(EXHAUSTION, 0.0F);
        this.dataManager.register(FOOD_LEVEL, 20.0F);
        this.dataManager.register(LOVE, 4.0F);
        this.dataManager.register(PROGRESS, 0.0F);
        this.dataManager.register(RELAXATION, 0.0F);
        this.dataManager.register(SATURATION, 6.0F);
        this.dataManager.register(STRESS, 0.0F);
        super.registerData();
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("Protagonist")) { this.setProtagonist(compound.getUniqueId("Protagonist")); }
        if (compound.hasUniqueId("GlobalUUID")) { this.setUUID(compound.getUniqueId("GlobalUUID")); }
        this.setFollowing(compound.getBoolean("IsFollowing"));
        this.setSitting(compound.getBoolean("IsSitting"));
        this.setHomePosition(BlockPos.fromLong(compound.getLong("HomePosition")));
        this.timeSinceAvoid = compound.getInt("TimeSinceAvoid");
        this.timeSinceInteraction = compound.getInt("TimeSinceInteraction");
        this.timeSinceStare = compound.getInt("TimeSinceStare");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLove = compound.getInt("TimeUntilLove");
        this.readCharacter(compound);
    }

    public void readCharacter(CompoundNBT compound) {
        this.states.forEach((state, automaton) -> automaton.fromKey(compound.getString(state.getSimpleName())));
        this.setBloodType(BloodType.get(compound.getString("BloodType")));
        this.setDere(Dere.get(compound.getString("Dere")));
        this.setEmotion(Emotion.get(compound.getString("Emotion")));
        this.setStoryPhase(StoryPhase.get(compound.getString("StoryPhase")));
        this.setFamilyName(compound.getString("FamilyName"));
        this.setAffection(compound.getFloat("Affection"));
        this.setExhaustion(compound.getFloat("Exhaustion"));
        this.setFoodLevel(compound.getFloat("FoodLevel"));
        this.setGivenName(compound.getString("GivenName"));
        this.setHealth(compound.getFloat("Health"));
        this.setLove(compound.getFloat("Love"));
        this.setProgress(compound.getFloat("Progress"));
        this.setRelaxation(compound.getFloat("Relaxation"));
        this.setSaturation(compound.getFloat("Saturation"));
        this.setStress(compound.getFloat("Stress"));
        this.age = compound.getInt("Age");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.hasProtagonist()) { compound.putUniqueId("Protagonist", this.getProtagonistUUID()); }
        compound.putUniqueId("GlobalUUID", this.getUUID());
        compound.putBoolean("IsFollowing", this.isFollowing());
        compound.putBoolean("IsSitting", this.isSitting());
        compound.putLong("HomePosition", this.getHomePosition().toLong());
        compound.putInt("TimeSinceAvoid", this.timeSinceAvoid);
        compound.putInt("TimeSinceInteraction", this.timeSinceInteraction);
        compound.putInt("TimeSinceStare", this.timeSinceStare);
        compound.putInt("TimeSinceSleep", this.timeSinceSleep);
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLove", this.timeUntilLove);
        this.writeCharacter(compound);
    }

    public void writeCharacter(CompoundNBT compound) {
        this.states.forEach((state, automaton) -> compound.putString(state.getSimpleName(), automaton.getKey().toKey()));
        compound.putString("FamilyName", this.getFamilyName());
        compound.putString("GivenName", this.getGivenName());
        compound.putFloat("Health", this.getHealth());
        compound.putFloat("FoodLevel", this.getFoodLevel());
        compound.putFloat("Saturation", this.getSaturation());
        compound.putFloat("Exhaustion", this.getExhaustion());
        compound.putFloat("Love", this.getLove());
        compound.putFloat("Affection", this.getAffection());
        compound.putFloat("Progress", this.getProgress());
        compound.putFloat("Stress", this.getStress());
        compound.putFloat("Relaxation", this.getRelaxation());
        compound.putLong("Age", this.age);
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getGivenName(), this.getHonorific());
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    public String getGivenName() {
        return this.dataManager.get(GIVEN_NAME);
    }

    public void setGivenName(String name) {
        this.dataManager.set(GIVEN_NAME, name);
    }

    public String getFamilyName() {
        return this.dataManager.get(FAMILY_NAME);
    }

    public void setFamilyName(String name) {
        this.dataManager.set(FAMILY_NAME, name);
    }

    public String getFullName() {
        return String.format("%s %s", this.getFamilyName(), this.getGivenName());
    }

    public String getHonorific() {
        return this.getGender() == Gender.FEMININE ? "chan" : "kun";
    }

    public Gender getGender() {
        return Gender.FEMININE;
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.setBloodType(BloodType.weigh(this.rand));
        this.setGivenName(this.getGender().toString());
        this.setUUID(UUID.randomUUID());
        this.resetAnimationState();
        return data;
    }

    @Override
    public void livingTick() {
        this.updateArmSwingProgress();
        super.livingTick();
        this.states.forEach((state, machine) -> machine.update());
        if (this.isFollowing() && this.isPassenger() && !this.getProtagonist().isPassenger()) { this.dismount(); }
        if (this.isLocal()) {
            if (this.timeSinceSleep > 24000) { this.addStress(0.0005F); }
            this.updateConvoState();
            this.updateStareState();
            this.updateHungerState();
            this.updateLoveState();
            ++this.timeSinceSleep;
            ++this.timeSinceInteraction;
            ++this.timeSinceStare;
            ++this.age;
        } else {
            this.updateAITasks();
        }
    }

    @Override
    public void updateAITasks() {
        Consumer<AbstractNPCEntity> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.canFly() && source == DamageSource.FALL;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return !this.canFly() || super.onLivingFall(distance, damageMultiplier);
    }

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) { super.updateFallState(y, onGround, state, pos); }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.setConvo(Interaction.LEFT_CLICK, source.getTrueSource())) { return false; }
        return super.attackEntityFrom(source, amount);
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

    @Override
    public void onDeath(DamageSource cause) {
        this.setCharacter((npc) -> npc.setDead(true));
        super.onDeath(cause);
        if (this.isLocal() && this.isProtagonistOnline()) {
            this.say(this.getProtagonist(), cause.getDeathMessage(this));
        }
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        this.entityDropItem(MoeItems.MOE_DIE.get());
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            this.entityDropItem(this.getItemStackFromSlot(slot));
        }
    }

    @Override
    protected float getDropChance(EquipmentSlotType slot) {
        return 0.0F;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        return;
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
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ActionResultType result = this.onInteract(player, player.getHeldItem(hand), hand);
        if (result.isSuccessOrConsume()) { this.setInteractTarget(player); }
        this.sync();
        return result;
    }

    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem().isIn(MoeTags.Items.ADMIN)) { return ActionResultType.FAIL; }
        if (this.isRemote() || hand != Hand.MAIN_HAND) { return ActionResultType.PASS; }
        if (this.setConvo(Interaction.RIGHT_CLICK, player)) {
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    public boolean canConsume(ItemStack stack) {
        return stack.isFood() && (this.getFoodLevel() < 19 || stack.getItem().getFood().canEatWhenFull());
    }

    public void consume(ItemStack stack) {
        if (this.canConsume(stack)) {
            Food food = stack.getItem().getFood();
            this.addSaturation(food.getSaturation());
            this.addFoodLevel(food.getHealing());
            food.getEffects().forEach(pair -> {
                if (pair.getFirst() != null && this.world.rand.nextFloat() < pair.getSecond()) {
                    this.addPotionEffect(pair.getFirst());
                }
            });
        }
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
    public void jump() {
        this.addExhaustion(this.isSprinting() ? 0.2F : 0.05F);
        super.jump();
    }

    @Override
    public void swingArm(Hand hand) {
        this.addExhaustion(0.1F);
        super.swingArm(hand);
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        super.applyEntityCollision(entity);
        this.setConvo(Interaction.COLLISION, entity);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        this.sync();
    }

    @Override
    public int getTalkInterval() {
        return 150 + this.rand.nextInt(150);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) { return VoiceLines.SLEEPING.get(this); }
        return VoiceLines.NEUTRAL.get(this);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return VoiceLines.DEAD.get(this);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return VoiceLines.HURT.get(this);
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
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

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (stack.getItem().isIn(MoeTags.Items.OFFHAND) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public boolean isWithinHomingDistance() {
        return 16 > this.getHomeDistance() || this.getHomeDistance() > 256;
    }

    public double getHomeDistance() {
        return Math.sqrt(this.getDistanceSq(Vector3d.copyCentered(this.getHomePosition())));
    }

    public void setHomePosition(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int) this.getMaximumHomeDistance());
    }

    public boolean canWander() {
        return this.isWithinHomingDistance() && !this.isOccupied();
    }

    public boolean isOccupied() {
        return this.isFighting() || this.isVengeful() || this.isSleeping() || this.isFollowing() || this.isInConversation() || this.hasPath();
    }

    public boolean canBeTarget(Entity target) {
        return target != null && target.isAlive() && !target.equals(this);
    }

    public LivingEntity getAvoidTarget() {
        return this.entityToAvoid;
    }

    public void setAvoidTarget(LivingEntity target) {
        this.entityToAvoid = target;
        this.timeSinceAvoid = 0;
    }

    public boolean isAvoiding() {
        return this.timeSinceAvoid < 500;
    }

    public boolean isVengeful() {
        return this.ticksExisted - this.getRevengeTimer() < 500;
    }

    public boolean isAttacking() {
        return this.canBeTarget(this.getAttackTarget());
    }

    public int getAttackCooldown() {
        return (int) (this.getAttribute(Attributes.ATTACK_SPEED).getValue() * 4.0F);
    }

    public boolean isAtEase() {
        return !this.isFighting();
    }

    public boolean isTimeToSleep() {
        if (this.canFight() && this.isNightWatch()) { return false; }
        return this.isOccupied() && !this.world.isDaytime();
    }

    public boolean isNightWatch() {
        return this.world.getGameTime() / 24000 % 4 == this.getBloodType().ordinal();
    }

    public boolean isInteracted() {
        return this.timeSinceInteraction < 20;
    }

    public PlayerEntity getInteractTarget() {
        if (this.isInteracted()) { return this.playerInteracted; }
        return null;
    }

    public void setInteractTarget(PlayerEntity player) {
        this.playerInteracted = player;
        this.timeSinceInteraction = 0;
    }

    public int getTimeSinceInteraction() {
        return this.timeSinceInteraction;
    }

    public LivingEntity getStareTarget() {
        if (this.isBeingStaredAt()) { return this.entityStaring; }
        return null;
    }

    public boolean isBeingStaredAt() {
        return this.timeSinceStare < 20;
    }

    public int getTimeSinceSleep() {
        return this.timeSinceSleep;
    }

    public boolean isAutomatonReady() {
        return this.isAutomatonReady;
    }

    public Automaton getAutomaton(IStateEnum state) {
        return this.getAutomaton(state.getClass());
    }

    public Automaton getAutomaton(Class<? extends IStateEnum> state) {
        return this.states.get(state);
    }

    public void addNextTickOp(Consumer<AbstractNPCEntity> op) {
        this.nextTickOps.add(op);
    }

    public void setNextState(Class<? extends IStateEnum> key, IStateEnum state) {
        this.addNextTickOp((entity) -> this.states.get(key).setNextState(state, true));
    }

    public boolean hasItem(ITag<Item> tag) {
        try { return this.getHeldItem(Hand.MAIN_HAND).getItem().isIn(tag); } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean hasItem(Item item) {
        return this.getHeldItem(Hand.MAIN_HAND).getItem() == item;
    }

    public boolean canFight() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item.isIn(MoeTags.Items.WEAPONS);
    }

    public boolean isFighting() {
        return this.canBeTarget(this.getAttackTarget()) || this.canBeTarget(this.getRevengeTarget()) || this.canBeTarget(this.getAvoidTarget());
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
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

    public boolean hasState(IStateEnum state) {
        return this.getState(state) == state;
    }

    public IStateEnum getState(IStateEnum state) {
        return this.getState(state.getClass());
    }

    public IStateEnum getState(Class<? extends IStateEnum> state) {
        return this.getAutomaton(state).getKey();
    }

    public boolean is(IStateEnum state) {
        return this.isAutomatonReady() && this.getState(state) == state;
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        if (!this.canBeTarget(entity) || this.getDistance(entity) > 5.0F) { return false; }
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public UUID getProtagonistUUID() {
        return this.dataManager.get(PROTAGONIST).orElse(null);
    }

    public PlayerEntity getProtagonist() {
        if (this.getProtagonistUUID() == null) { return null; }
        if (this.isLocal()) { return this.world.getServer().getPlayerList().getPlayerByUUID(this.getProtagonistUUID()); }
        return this.world.getPlayerByUuid(this.getProtagonistUUID());
    }

    public void setProtagonist(PlayerEntity player) {
        this.setCharacter((npc) -> npc.setEstranged(true));
        this.setProtagonist(player != null ? player.getUniqueID() : null);
        this.sync();
    }

    public void setProtagonist(UUID uuid) {
        this.dataManager.set(PROTAGONIST, Optional.of(uuid));
    }

    public boolean isProtagonistBusy() {
        return this.isProtagonistOnline() && this.getProtagonist().openContainer != this.getProtagonist().container;
    }

    public boolean isProtagonistOnline() {
        return this.hasProtagonist() && this.getProtagonist() != null;
    }

    public boolean hasProtagonist() {
        if (this.getUUID() == null) { return false; }
        return this.getProtagonistUUID() != null;
    }

    public boolean isProtagonist(Entity entity) {
        if (!this.hasProtagonist() || entity == null) { return false; }
        return this.getProtagonistUUID().equals(entity.getUniqueID());
    }

    public void say(PlayerEntity player, String line, Response... responses) {
        if (line.length() > 128) { throw new IllegalArgumentException("Lines can't be over 128 characters long."); }
        if (responses.length == 0) { responses = new Response[]{Response.CLOSE}; }
        MoeMessages.send(player, new SOpenDialogue(new Dialogue(this.getCharacter(), line, responses)));
    }

    public void say(String key, Object... params) {
        this.world.getPlayers().forEach((player) -> {
            if (player.getDistance(this) < 8.0D) { this.say(player, key, params); }
        });
    }

    public void say(PlayerEntity player, String key, Object... params) {
        this.say(player, new TranslationTextComponent(key, params));
    }

    public void say(PlayerEntity player, ITextComponent component) {
        player.sendMessage(component, this.getUUID());
    }

    public AbstractNPCEntity teleport(ServerWorld world, ITeleporter teleporter) {
        AbstractNPCEntity npc = (AbstractNPCEntity) this.changeDimension(world, teleporter);
        ChunkScheduler.get(npc.getUUID()).despawn();
        return npc.afterTeleport().sync();
    }

    public AbstractNPCEntity afterTeleport() {
        this.setAnimation(Animation.SUMMONED);
        this.onTeleport();
        return this;
    }

    protected abstract void onTeleport();

    public void updateConvoState() {
        if (this.isInConversation() && !this.acted) {
            this.scene.act(this.getProtagonist(), this);
            this.acted = true;
        } else if (!this.isInConversation()) {
            this.scene = null;
        }
    }

    public void updateHungerState() {
        if (--this.timeUntilHungry < 0) {
            if (this.getExhaustion() > 4.0F) {
                this.addExhaustion(-4.0F);
                this.addSaturation(-1.0F);
                if (this.getSaturation() < 0.0F) {
                    this.addFoodLevel(-1.0F);
                    this.setSaturation(0.0F);
                }
            }
            if (this.getSaturation() > 0.0F) {
                this.timeUntilHungry = 80;
                this.addExhaustion(6.0F);
                this.heal(1.0F);
            }
        }
    }

    public void updateLoveState() {
        if (--this.timeUntilLove < 0) {
            this.addAffection(-this.getStress());
            this.addLove(this.getAffection());
            this.timeUntilLove = 1000;
            if (this.getAffection() < 0) {
                this.setAffection(0);
            }
        }
    }

    public void updateStareState() {
        if (this.isBeingStaredAt() || this.timeSinceStare % 200 != 0) { return; }
        if (this.isBeingWatchedBy(this.getProtagonist())) {
            this.entityStaring = this.getProtagonist();
            this.setConvo(Interaction.STARE, this.entityStaring);
            this.timeSinceStare = 0;
        }
    }

    public boolean canConverseWith(Entity entity) {
        if (this.isRemote() || this.isProtagonistBusy() || this.isInConversation()) { return false; }
        return this.isProtagonist(entity);
    }

    public boolean canSee(Entity entity) {
        if (this.canBeTarget(entity) && this.getEntitySenses().canSee(entity)) {
            this.getLookController().setLookPositionWithEntity(entity, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
            return true;
        }
        return false;
    }

    public boolean isInConversation() {
        return this.scene != null && this.getDistance(this.getProtagonist()) < 5.0F;
    }

    public boolean setConvo(Interaction interaction) {
        this.setScene(MoeConvos.find(interaction, this));
        return this.isInConversation();
    }

    public boolean setConvo(Interaction interaction, Entity entity) {
        return this.canConverseWith(entity) && this.setConvo(interaction);
    }

    public float getScale() {
        return 1.0F;
    }

    public Scene getScene() {
        return this.scene;
    }

    public void setScene(Scene scene) {
        if (scene == null) { MoeMessages.send(this.getProtagonist(), new SCloseDialogue()); }
        this.scene = scene;
        this.acted = false;
    }

    public void setScene(Response response) {
        if (this.isInConversation()) { this.setScene(this.scene.next(response)); }
    }

    public UUID getUUID() {
        return this.dataManager.get(GLOBAL_UUID).orElse(null);
    }

    public void setUUID(UUID uuid) {
        this.dataManager.set(GLOBAL_UUID, Optional.of(uuid));
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING) || this.isPassenger() && (this.getRidingEntity() != null && this.getRidingEntity().shouldRiderSit());
    }

    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
    }

    public boolean isFollowing() {
        if (!this.isProtagonistOnline()) { return false; }
        return this.dataManager.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.dataManager.set(FOLLOWING, following);
    }

    public boolean isCompatible(AbstractNPCEntity entity) {
        return BloodType.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public BloodType getBloodType() {
        return BloodType.get(this.dataManager.get(BLOOD_TYPE));
    }

    public void setBloodType(BloodType bloodType) {
        this.dataManager.set(BLOOD_TYPE, bloodType.toKey());
    }

    public Dere getDere() {
        return Dere.get(this.dataManager.get(DERE));
    }

    public void setDere(Dere dere) {
        this.dataManager.set(DERE, dere.toKey());
    }

    public Emotion getEmotion() {
        return Emotion.get(this.dataManager.get(EMOTION));
    }

    public void setEmotion(Emotion emotion) {
        this.dataManager.set(EMOTION, emotion.toKey());
    }

    public StoryPhase getStoryPhase() {
        return StoryPhase.get(this.dataManager.get(STORY_PHASE));
    }

    public void setStoryPhase(StoryPhase phase) {
        this.dataManager.set(STORY_PHASE, phase.toKey());
        this.setProgress(0.0F);
    }

    public float getFoodLevel() {
        return this.dataManager.get(FOOD_LEVEL);
    }

    public void setFoodLevel(float foodLevel) {
        this.dataManager.set(FOOD_LEVEL, Math.max(0.0F, Math.min(foodLevel, 20.0F)));
        this.sync();
    }

    public void addFoodLevel(float foodLevel) {
        this.setFoodLevel(this.getFoodLevel() + foodLevel);
    }

    public float getLove() {
        return this.dataManager.get(LOVE);
    }

    public void setLove(float love) {
        this.dataManager.set(LOVE, Math.max(0.0F, Math.min(love, 20.0F)));
        this.sync();
    }

    public void addLove(float love) {
        this.setLove(this.getLove() + love);
    }

    public void resetAnimationState() {
        this.setAnimation(Animation.DEFAULT);
    }

    public Animation getAnimation() {
        return Animation.get(this.dataManager.get(ANIMATION));
    }

    public void setAnimation(Animation animation) {
        this.dataManager.set(ANIMATION, animation.toKey());
    }

    public void addExhaustion(float exhaustion) {
        this.setExhaustion(this.getExhaustion() + exhaustion);
    }

    public float getExhaustion() {
        return this.dataManager.get(EXHAUSTION);
    }

    public void setExhaustion(float exhaustion) {
        this.dataManager.set(EXHAUSTION, Math.max(0.0F, Math.min(exhaustion, 20.0F)));
    }

    public float getSaturation() {
        return this.dataManager.get(SATURATION);
    }

    public void setSaturation(float saturation) {
        this.dataManager.set(SATURATION, Math.max(0.0F, Math.min(saturation, 20.0F)));
    }

    public void addSaturation(float saturation) {
        this.setSaturation(this.getSaturation() + saturation);
    }

    public float getAffection() {
        return this.dataManager.get(AFFECTION);
    }

    public void setAffection(float affection) {
        this.dataManager.set(AFFECTION, Math.max(0.0F, Math.min(affection, 20.0F)));
    }

    public void addAffection(float love) {
        this.setAffection(this.getAffection() + love);
    }

    public float getRelaxation() {
        return this.dataManager.get(RELAXATION);
    }

    public void setRelaxation(float relaxation) {
        this.dataManager.set(RELAXATION, Math.max(0.0F, Math.min(relaxation, 20.0F)));
    }

    public float getProgress() {
        return this.dataManager.get(PROGRESS);
    }

    public void setProgress(float progress) {
        this.dataManager.set(PROGRESS, progress);
        this.sync();
    }

    public float getStress() {
        return this.dataManager.get(STRESS);
    }

    public void setStress(float stress) {
        this.dataManager.set(STRESS, Math.max(Math.min(stress, 20.0F), 0.0F));
        this.sync();
    }

    public void addStress(float stress) {
        this.setStress(this.getStress() + stress);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.setNextState(Animation.class, this.getAnimation()); }
        if (BLOOD_TYPE.equals(key)) { this.setNextState(BloodType.class, this.getBloodType()); }
        if (DERE.equals(key)) { this.setNextState(Dere.class, this.getDere()); }
        if (EMOTION.equals(key)) { this.setNextState(Emotion.class, this.getEmotion()); }
        if (STORY_PHASE.equals(key)) { this.setNextState(StoryPhase.class, this.getStoryPhase()); }
        super.notifyDataManagerChange(key);
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

    public boolean asMoe(Predicate<MoeEntity> function) {
        return this.isMoe() && function.test(this.asMoe());
    }

    public boolean isMoe() {
        return this instanceof MoeEntity;
    }

    public MoeEntity asMoe() {
        return this.isMoe() ? (MoeEntity) this : null;
    }

    public CacheNPC getCharacter() {
        if (this.isAutomatonReady() && this.isLocal() && this.hasProtagonist()) {
            return this.getDatingSim().getNPC(this.getUUID(), this);
        }
        return null;
    }

    public void setCharacter(Consumer<CacheNPC> transaction) {
        CacheNPC character = this.getCharacter();
        if (character != null) {
            character.set(DatingData.get(this.world), transaction);
        }
    }

    public AbstractNPCEntity sync() {
        this.setCharacter((npc) -> npc.sync(this));
        return this;
    }

    public DatingSim getDatingSim() {
        return DatingData.get(this.world, this.getProtagonistUUID());
    }

    public boolean canFly() {
        return this.moveController instanceof FlyingMovementController;
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
            GroundPathNavigator ground = (GroundPathNavigator) this.navigator;
            ground.setBreakDoors(true);
        }
    }

    public void setPositionAndUpdate(BlockPos pos) {
        this.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int) (this.world.getGameTime() - this.age) / 24000 / 366;
    }

    public abstract int getBaseAge();

    public float getBlockStrikingDistance() {
        return this.getStrikingDistance(1.0F);
    }

    public BlockState getBlockToMine() {
        return this.blockToMine;
    }

    public void setBlockToMine(BlockState state) {
        this.blockToMine = state;
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

    public double getGaussian(double factor) {
        return this.rand.nextGaussian() * factor;
    }

    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.world.getDimensionKey(), this.getPosition());
    }

    public ChunkPos getChunkPosition() {
        return new ChunkPos(this.getPosition());
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

    @Override
    public void copyDataFromOld(Entity entity) {
        super.copyDataFromOld(entity);
        this.setUniqueId(UUID.randomUUID());
    }

    @Override
    public void dismount() {
        if (this.isFollowing()) { this.setPositionAndUpdate(this.getProtagonist().getPosition()); }
        super.dismount();
    }

    @Override
    public void setRawPosition(double x, double y, double z) {
        super.setRawPosition(x, y, z);
        this.setCharacter((npc) -> npc.setPosition(this.getDimBlockPos()));
    }

    public BlockState getBlockData() {
        return Blocks.AIR.getDefaultState();
    }

    public CompoundNBT getExtraBlockData() {
        return new CompoundNBT();
    }
}
