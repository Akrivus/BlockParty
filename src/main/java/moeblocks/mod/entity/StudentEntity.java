package moeblocks.mod.entity;

import moeblocks.mod.client.Animations;
import moeblocks.mod.client.animation.Animation;
import moeblocks.mod.entity.ai.AbstractState;
import moeblocks.mod.entity.ai.dere.AbstractDere;
import moeblocks.mod.entity.ai.dere.Himedere;
import moeblocks.mod.entity.ai.emotion.AbstractEmotion;
import moeblocks.mod.entity.ai.emotion.NormalEmotion;
import moeblocks.mod.entity.ai.goal.*;
import moeblocks.mod.entity.ai.goal.engage.ShareGoals;
import moeblocks.mod.entity.ai.goal.engage.SocializeGoal;
import moeblocks.mod.entity.ai.goal.target.*;
import moeblocks.mod.entity.util.*;
import moeblocks.mod.entity.util.data.FoodStats;
import moeblocks.mod.entity.util.data.Relationships;
import moeblocks.mod.entity.util.data.StressStats;
import moeblocks.mod.init.MoeItems;
import moeblocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Predicate;

public class StudentEntity extends CreatureEntity {
    public static final DataParameter<Integer> ANIMATION = EntityDataManager.createKey(StudentEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> BLOOD_TYPE = EntityDataManager.createKey(StudentEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> DERE = EntityDataManager.createKey(StudentEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> EMOTION = EntityDataManager.createKey(StudentEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Boolean> EYEPATCH = EntityDataManager.createKey(StudentEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(StudentEntity.class, DataSerializers.BOOLEAN);
    public AttackGoals.Melee attackGoal;
    protected Animation animation = new Animation();
    protected FoodStats foodStats = new FoodStats();
    protected Relationships relationships = new Relationships();
    protected StressStats stressStats = new StressStats();
    protected AbstractDere dere = new Himedere();
    protected AbstractEmotion emotion = new NormalEmotion();
    private long timeBorn;
    private int timeUntilEmotional;
    private UUID followTargetUUID;
    private LivingEntity avoidTarget;
    private int avoidTimer;

    protected StudentEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.foodStats.start(this);
        this.relationships.start(this);
        this.stressStats.start(this);
        this.dere.setStateEntity(this);
        this.emotion.setStateEntity(this);
    }

    public static AttributeModifierMap setCustomAttributes() {
        AttributeModifierMap.MutableAttribute attributes = MobEntity.func_233666_p_();
        attributes = attributes.createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0F);
        attributes = attributes.createMutableAttribute(Attributes.ATTACK_SPEED, 4.0F);
        attributes = attributes.createMutableAttribute(Attributes.FLYING_SPEED, 1.5F);
        attributes = attributes.createMutableAttribute(Attributes.FOLLOW_RANGE, 256.0D);
        attributes = attributes.createMutableAttribute(Attributes.MAX_HEALTH, 20.0F);
        attributes = attributes.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3F);
        return attributes.create();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new OpenDoorGoal(this));
        this.goalSelector.addGoal(2, this.attackGoal = new AttackGoals.Melee(this));
        this.goalSelector.addGoal(2, new AttackGoals.Ranged(this));
        this.goalSelector.addGoal(3, new FollowGoal(this));
        this.goalSelector.addGoal(4, new AvoidGoal(this));
        this.goalSelector.addGoal(5, new GrabGoal(this));
        this.goalSelector.addGoal(6, new ShareGoals.Moe(this));
        this.goalSelector.addGoal(6, new ShareGoals.Player(this));
        this.goalSelector.addGoal(6, new SocializeGoal(this));
        this.goalSelector.addGoal(7, new WaitGoal(this));
        this.registerTargets();
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register(ANIMATION, Animations.DEFAULT.ordinal());
        this.dataManager.register(BLOOD_TYPE, BloodTypes.O.ordinal());
        this.dataManager.register(DERE, Deres.HIMEDERE.ordinal());
        this.dataManager.register(EMOTION, Emotions.NORMAL.ordinal());
        this.dataManager.register(EYEPATCH, false);
        this.dataManager.register(SITTING, false);
        this.registerStates();
    }

    protected void registerStates() {

    }

    @Override
    public int getTalkInterval() {
        return 900 + this.rand.nextInt(600);
    }

    @Override
    public void tick() {
        this.updateArmSwingProgress();
        super.tick();
        if (this.isLocal()) {
            if (--this.timeUntilEmotional < 0) {
                Triggers.REGISTRY.forEach(trigger -> trigger.fire(this));
            }
        } else {
            this.getAnimation().tick(this);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getEmotion().getLivingSound();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Animation", this.getAnimation().toString());
        compound.putString("BloodType", this.getBloodType().toString());
        compound.putString("Dere", this.getDere().toString());
        compound.putString("Emotion", this.getEmotion().toString());
        compound.putBoolean("HasEyepatch", this.hasEyepatch());
        compound.putInt("TimeUntilEmotional", this.getEmotionalTimeout());
        compound.putLong("TimeBorn", this.timeBorn);
        this.runStates(state -> {
            state.write(compound);
            return true;
        });
        if (this.getFollowTarget() != null) {
            compound.putUniqueId("FollowTarget", this.getFollowTarget().getUniqueID());
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setAnimation(Animations.valueOf(compound.getString("Animation")));
        this.setBloodType(BloodTypes.valueOf(compound.getString("BloodType")));
        this.setDere(Deres.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotions.valueOf(compound.getString("Emotion")));
        this.setEmotionalTimeout(compound.getInt("TimeUntilEmotional"));
        this.setHasEyepatch(compound.getBoolean("HasEyepatch"));
        this.timeBorn = compound.getLong("TimeBorn");
        this.runStates(state -> {
            state.read(compound);
            return true;
        });
        if (compound.hasUniqueId("FollowTarget")) {
            this.setFollowTarget(compound.getUniqueId("FollowTarget"));
        }
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        this.entityDropItem(MoeItems.MOE_DIE.get());
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (this.getDere().getGiftValue(stack) == 0.0F || stack.isFood()) {
                this.entityDropItem(stack);
            }
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.runStates(state -> {
            state.tick();
            return true;
        });
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        EquipmentSlotType slot = this.getSlotForStack(candidate);
        if (EnchantmentHelper.hasBindingCurse(existing)) {
            return false;
        } else if (slot == EquipmentSlotType.OFFHAND && this.foodStats.canConsume(candidate)) {
            return true;
        } else if (existing.getItem() instanceof TieredItem && candidate.getItem() instanceof TieredItem) {
            IItemTier inbound = ((TieredItem) candidate.getItem()).getTier();
            IItemTier current = ((TieredItem) existing.getItem()).getTier();
            return inbound.getHarvestLevel() > current.getHarvestLevel() || inbound.getAttackDamage() > current.getAttackDamage() || inbound.getMaxUses() > current.getMaxUses();
        } else if (existing.getItem() instanceof ArmorItem) {
            ArmorItem inbound = (ArmorItem) candidate.getItem();
            ArmorItem current = (ArmorItem) existing.getItem();
            return inbound.getDamageReduceAmount() > current.getDamageReduceAmount() || inbound.func_234657_f_() > current.func_234657_f_();
        } else if (this.isWieldingBow()) {
            return slot == EquipmentSlotType.OFFHAND ? candidate.getItem() instanceof ArrowItem : !this.hasAmmo();
        } else {
            return slot != EquipmentSlotType.OFFHAND && existing.isEmpty();
        }
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean hit) {

    }

    protected float getDropChance(EquipmentSlotType slot) {
        return 0.0F;
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {

    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setBloodType(BloodTypes.weigh(this.world.rand));
        this.timeBorn = world.getWorld().getGameTime();
        this.resetAnimationState();
        this.runStates(state -> {
            state.onSpawn(world);
            return true;
        });
        return data;
    }

    @Override
    public boolean canPickUpItem(ItemStack stack) {
        if (stack.getItem().isIn(MoeTags.EQUIPPABLES) || this.foodStats.canConsume(stack)) {
            EquipmentSlotType slot = this.getSlotForStack(stack);
            ItemStack shift = this.getItemStackFromSlot(slot);
            if (ItemStack.areItemsEqual(shift, stack) && ItemStack.areItemStackTagsEqual(shift, stack)) {
                return shift.getCount() < shift.getMaxStackSize();
            } else {
                return this.shouldExchangeEquipment(stack, shift);
            }
        }
        return false;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if (player.getHeldItem(hand).getItem() != MoeItems.YEARBOOK.get()) {
            return this.runStates(state -> state.onInteract(player, player.getHeldItem(hand), hand)) ? ActionResultType.func_233537_a_(this.isRemote()) : super.func_230254_b_(player, hand);
        }
        return super.func_230254_b_(player, hand);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target) && this.runStates(state -> state.canAttack(target));
    }

    public boolean canBeTarget(Entity target) {
        return target != null && target.isAlive() && !target.equals(this);
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public void resetAnimationState() {
        this.setAnimation(this.getFollowTarget() != null ? Animations.DEFAULT : Animations.WAITING);
    }

    public void setHasEyepatch(boolean hasEyepatch) {
        this.dataManager.set(EYEPATCH, hasEyepatch);
    }

    public LivingEntity getFollowTarget() {
        if (this.getLeashHolder() instanceof LivingEntity) {
            return (LivingEntity) this.getLeashHolder();
        } else {
            return this.getEntityFromUUID(this.followTargetUUID);
        }
    }

    public void setFollowTarget(LivingEntity leader) {
        this.setFollowTarget(leader == null ? null : leader.getUniqueID());
        this.setHomePosAndDistance(this.getOnPosition(), 32);
        this.resetAnimationState();
    }

    protected void setFollowTarget(UUID uuid) {
        this.followTargetUUID = uuid;
    }

    public LivingEntity getEntityFromUUID(UUID uuid) {
        if (this.world instanceof ServerWorld) {
            Entity entity = ((ServerWorld) this.world).getEntityByUuid(uuid);
            if (entity instanceof LivingEntity) {
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    public boolean runStates(Predicate<AbstractState> function) {
        boolean result = false;
        Iterator<AbstractState> it = this.getStates();
        while (it.hasNext()) {
            result |= function.test(it.next());
        }
        return result;
    }

    public Iterator<AbstractState> getStates() {
        ArrayList<AbstractState> states = new ArrayList<>();
        states.add(this.getRelationships());
        states.add(this.getFoodStats());
        states.add(this.getStressStats());
        return states.iterator();
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public StressStats getStressStats() {
        return this.stressStats;
    }

    public Relationships getRelationships() {
        return this.relationships;
    }

    public AbstractDere getDere() {
        return this.dere;
    }

    public void setDere(Deres dere) {
        this.dataManager.set(DERE, dere.ordinal());
    }

    public int getEmotionalTimeout() {
        return this.timeUntilEmotional;
    }

    public void setEmotionalTimeout(int timeout) {
        this.timeUntilEmotional = timeout;
    }

    public BloodTypes getBloodType() {
        return BloodTypes.values()[this.dataManager.get(BLOOD_TYPE)];
    }

    public void setBloodType(BloodTypes bloodType) {
        this.dataManager.set(BLOOD_TYPE, bloodType.ordinal());
    }

    public boolean hasEyepatch() {
        return this.dataManager.get(EYEPATCH);
    }

    public AbstractEmotion getEmotion() {
        return this.emotion;
    }

    public void setEmotion(Emotions emotion) {
        this.dataManager.set(EMOTION, emotion.ordinal());
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setAnimation(Animations animation) {
        this.dataManager.set(ANIMATION, animation.ordinal());
    }

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    protected void registerTargets() {
        this.targetSelector.addGoal(1, new AvengeSelfGoal(this));
        this.targetSelector.addGoal(2, new AvengeLeaderGoal(this));
        this.targetSelector.addGoal(3, new JoinLeaderGoal(this));
        this.targetSelector.addGoal(4, new DefendSelfGoal(this));
        this.targetSelector.addGoal(5, new DefendLeaderGoal(this));
        this.targetSelector.addGoal(6, new TargetMobsGoal(this));
    }

    public void toggleFollowTarget(LivingEntity leader) {
        LivingEntity target = (leader == null || leader.equals(this.getFollowTarget())) ? null : leader;
        this.setFollowTarget(target);
        if (leader instanceof PlayerEntity) {
            String decision = this.getFollowTarget() == null ? "no" : "yes";
            this.say((PlayerEntity) leader, String.format("command.moeblocks.moe.following.%s", decision), this.getPlainName());
        }
    }

    public String getPlainName() {
        return this.getName().getString();
    }

    public boolean isStandingOn(BlockState state) {
        BlockState stand = this.world.getBlockState(this.getPosition());
        return state.getBlock() == stand.getBlock();
    }

    public void setEmotion(Emotions emotion, int timeout) {
        this.setEmotionalTimeout(timeout);
        if (timeout > 0) {
            this.setEmotion(emotion);
        }
    }

    public boolean tryEquipItem(ItemStack stack) {
        EquipmentSlotType slot = this.getSlotForStack(stack);
        ItemStack shift = this.getItemStackFromSlot(slot);
        int max = shift.getMaxStackSize();
        if (this.canPickUpItem(stack)) {
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
                if (this.getDere().getGiftValue(stack) == 0.0F || stack.isFood()) {
                    this.entityDropItem(shift);
                }
                this.setItemStackToSlot(slot, stack.split(1));
                return stack.isEmpty();
            }
        }
        return false;
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (this.isAmmo(stack.getItem()) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public boolean isSuperiorTo(LivingEntity foe) {
        if (foe == null) {
            return false;
        }
        if (this.isMeleeFighter()) {
            double moeHOD = foe.getHealth() / this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            double foeHOD = this.getHealth() + this.getTotalArmorValue() / 0.4F;
            if (foe.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                foeHOD /= foe.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            }
            return foeHOD >= moeHOD;
        } else {
            return true;
        }
    }

    public boolean isMeleeFighter() {
        return this.isWieldingWeapons() && !this.isRangedFighter();
    }

    public boolean isRangedFighter() {
        return (this.isWieldingBow() && this.hasAmmo()) || this.getThrowableFromStack(this.getHeldItemMainhand()) != null;
    }

    public ProjectileEntity getThrowableFromStack(ItemStack stack) {
        Direction face = this.getAdjustedHorizontalFacing();
        Item item = stack.getItem();
        if (item == Items.FIREWORK_ROCKET) {
            return new FireworkRocketEntity(this.world, this.getPosX() + face.getXOffset() * 0.15D, this.getPosY() + face.getYOffset() * 0.15D, this.getPosZ() + face.getZOffset() * 0.15D, stack);
        } else if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
            PotionEntity potion = new PotionEntity(this.world, this);
            potion.setItem(stack);
            potion.rotationPitch += 20.0F;
            return potion;
        } else if (item == Items.SNOWBALL) {
            return new SnowballEntity(this.world, this);
        } else if (item == Items.EGG) {
            return new EggEntity(this.world, this);
        }
        return null;
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
    }

    public boolean hasAmmo() {
        return this.isAmmo(this.getHeldItem(Hand.OFF_HAND).getItem());
    }

    public boolean isAmmo(Item item) {
        return item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;
    }

    public boolean isWieldingWeapons() {
        return this.getHeldItem(Hand.MAIN_HAND).getItem().isIn(MoeTags.WEAPONS);
    }

    public boolean isCompatible(StudentEntity entity) {
        return BloodTypes.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public float getFollowSpeed(Entity entity, float factor) {
        float speed = 1.0F + this.getDistance(entity) / factor;
        this.setSprinting(speed > 1.5F);
        return Math.min(speed, 8.0F);
    }

    public void attackEntityFromRange(LivingEntity victim, double factor) {
        ItemStack stack = this.getHeldItem(Hand.MAIN_HAND);
        double dX = victim.getPosX() - this.getPosX();
        double dY = victim.getPosY() - this.getPosY();
        double dZ = victim.getPosZ() - this.getPosZ();
        double d = MathHelper.sqrt(dX * dX + dZ * dZ);
        if (stack.getItem() == Items.BOW || stack.getItem() == Items.CROSSBOW) {
            stack = this.getHeldItem(Hand.OFF_HAND);
            AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, stack, (float) factor);
            arrow.shoot(dX, dY + d * 0.2, dZ, 1.6F, 1.0F);
            arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT);
            this.world.addEntity(arrow);
        } else {
            ProjectileEntity model = this.getThrowableFromStack(stack);
            model.shoot(dX, dY + d * 0.2, dZ, 0.75F, 1.0F);
            this.playSound(SoundEvents.ENTITY_SNOWBALL_THROW);
            this.world.addEntity(model);
            this.swingArm(Hand.MAIN_HAND);
        }
        stack.shrink(1);
    }

    @Override
    public void swingArm(Hand hand) {
        this.foodStats.addExhaustion(0.1F);
        super.swingArm(hand);
    }

    @Override
    public void jump() {
        this.foodStats.addExhaustion(this.isSprinting() ? 0.2F : 0.05F);
        super.jump();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (ANIMATION.equals(key)) {
            this.animation = Animations.values()[this.dataManager.get(ANIMATION)].get();
        } else if (DERE.equals(key)) {
            AbstractDere dere = Deres.values()[this.dataManager.get(DERE)].get();
            this.dere.stop(this.dere = dere);
        } else if (EMOTION.equals(key)) {
            AbstractEmotion emotion = Emotions.values()[this.dataManager.get(EMOTION)].get();
            this.emotion.stop(this.emotion = emotion);
        }
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    public int getAttackCooldown() {
        return (int) (this.getAttribute(Attributes.ATTACK_SPEED).getValue() * 4.0F);
    }

    public boolean isWaiting() {
        return !this.canBeTarget(this.getFollowTarget()) || this.isCalm();
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING) || this.isPassenger() && (this.getRidingEntity() != null && this.getRidingEntity().shouldRiderSit());
    }

    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
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

    public LivingEntity getAvoidTarget() {
        return this.avoidTarget;
    }

    public void setAvoidTarget(LivingEntity target) {
        this.avoidTarget = target;
        this.avoidTimer = this.ticksExisted;
    }

    public int getAvoidTimer() {
        return this.avoidTimer;
    }

    public void setCanFly(boolean fly) {
        if (fly) {
            this.setMoveController(new FlyingMovementController(this, 10, false));
            this.setNavigator(new FlyingPathNavigator(this, this.world));
        } else {
            this.setMoveController(new MovementController(this));
            this.setNavigator(new GroundPathNavigator(this, this.world));
        }
    }

    public void setMoveController(MovementController moveController) {
        this.moveController = moveController;
    }

    public void setNavigator(PathNavigator navigator) {
        this.navigator = navigator;
    }

    public boolean canSee(Entity entity) {
        if (this.canBeTarget(entity)) {
            this.getLookController().setLookPositionWithEntity(entity, 30.0F, this.getVerticalFaceSpeed());
            return this.getEntitySenses().canSee(entity);
        }
        return false;
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

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) {
            super.updateFallState(y, onGround, state, pos);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.runStates(state -> state.onDamage(source, amount))) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.runStates(state -> {
            state.onDeath(cause);
            return true;
        });
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

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int)(this.world.getGameTime() - this.timeBorn) / 24000 / 366;
    }

    public int getBaseAge() {
        return 14;
    }

    public boolean isFighting() {
        return this.canBeTarget(this.getAttackTarget()) || this.canBeTarget(this.getRevengeTarget()) || this.canBeTarget(this.getAvoidTarget());
    }

    public boolean isCalm() {
        return !this.isFighting();
    }
}
