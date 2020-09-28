package moe.blocks.mod.entity.partial;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.entity.ai.automata.State;
import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.ai.automata.state.ItemStates;
import moe.blocks.mod.entity.ai.goal.*;
import moe.blocks.mod.entity.ai.goal.attack.BasicAttackGoal;
import moe.blocks.mod.entity.ai.goal.items.ConsumeGoal;
import moe.blocks.mod.entity.ai.goal.target.RevengeTarget;
import moe.blocks.mod.entity.ai.trigger.Trigger;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
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
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.function.Consumer;

public abstract class NPCEntity extends CreatureEntity {
    public static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(NPCEntity.class, DataSerializers.BOOLEAN);
    protected HashMap<States, State> states;
    protected Queue<Consumer<NPCEntity>> nextTickOps = new LinkedList<>();
    protected int timeUntilTriggered;
    protected long age;
    private float hunger = 20.0F;
    private float saturation = 5.0F;
    private float exhaustion;
    private float stress;
    private float relaxation;
    private int timeSinceSleep;
    private LivingEntity avoidTarget;
    private int avoidTimer;
    private boolean updateItemState = true;

    protected NPCEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0x1, new RevengeTarget(this));
        this.goalSelector.addGoal(0x0, new OpenDoorGoal(this));
        this.goalSelector.addGoal(0x0, new SleepGoal(this));
        this.goalSelector.addGoal(0x0, new SwimGoal(this));
        this.goalSelector.addGoal(0x2, new BasicAttackGoal(this));
        this.goalSelector.addGoal(0x3, new ConsumeGoal(this));
        this.goalSelector.addGoal(0x4, new AvoidTargetGoal(this));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this, (stack) -> stack.isFood()));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this));
        this.goalSelector.addGoal(0x8, new FindBedGoal(this));
        this.registerStates(this.states = new HashMap<>());
    }

    @Override
    public void registerData() {
        this.dataManager.register(SITTING, false);
        super.registerData();
    }

    @Override
    public int getTalkInterval() {
        return 900 + this.rand.nextInt(600);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putLong("HomePosition", this.getHomePosition().toLong());
        compound.putFloat("Hunger", this.hunger);
        compound.putFloat("Saturation", this.saturation);
        compound.putFloat("Exhaustion", this.exhaustion);
        compound.putFloat("Stress", this.stress);
        compound.putFloat("Relaxation", this.relaxation);
        compound.putLong("Age", this.age);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setHomePosition(BlockPos.fromLong(compound.getLong("HomePosition")));
        this.hunger = compound.getFloat("Hunger");
        this.saturation = compound.getFloat("Saturation");
        this.exhaustion = compound.getFloat("Exhaustion");
        this.stress = compound.getFloat("Stress");
        this.relaxation = compound.getFloat("Relaxation");
        this.age = compound.getInt("Age");
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
        super.livingTick();
        if (this.isLocal()) {
            if (++this.timeSinceSleep > 24000) { this.addStress(0.0005F); }
            Consumer<NPCEntity> op = this.nextTickOps.poll();
            if (op != null) { op.accept(this); }
            ++this.age;
        }
    }

    public void addStress(float stress) {
        this.stress = Math.min(this.stress + stress, 20.0F);
    }

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    protected void registerStates(HashMap<States, State> states) {
        states.put(States.HELD_ITEM, null);
    }

    public void addNextTickOp(Consumer<NPCEntity> op) {
        this.nextTickOps.add(op);
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

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
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

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) {
            super.updateFallState(y, onGround, state, pos);
        }
    }

    public boolean canFly() {
        return this.moveController instanceof FlyingMovementController;
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
    public void jump() {
        this.addExhaustion(this.isSprinting() ? 0.2F : 0.05F);
        super.jump();
    }

    @Override
    public void onItemUseFinish() {
        this.consume(this.activeItemStack);
        super.onItemUseFinish();
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

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.canFly() && source == DamageSource.FALL;
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
        this.avoidTimer = this.ticksExisted;
    }

    public boolean isAvoiding() {
        return this.ticksExisted - this.avoidTimer < 500;
    }

    public int getAttackCooldown() {
        return (int) (this.getAttribute(Attributes.ATTACK_SPEED).getValue() * 4.0F);
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public float getStress() {
        return this.stress;
    }

    public float getHunger() {
        return this.hunger;
    }

    public boolean isFull() {
        return this.hunger > 19.0F;
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
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target);
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

    public boolean isFavoriteItem(ItemStack stack) {
        return false;
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void updateAITasks() {
        if (this.updateItemState) { this.updateItemState(); }
        super.updateAITasks();
    }

    public void updateItemState() {
        this.setNextState(States.HELD_ITEM, ItemStates.get(this.getHeldItem(Hand.MAIN_HAND)).state);
        this.updateItemState = false;
    }

    public void setNextState(States key, State state) {
        this.addNextTickOp((entity) -> {
            if (this.states != null) {
                if (this.states.get(key) != null) { this.states.get(key).clean(this); }
                this.states.put(key, state.start(this));
            }
        });
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (stack.getItem().isIn(MoeTags.OFFHAND) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public boolean hasAmmo() {
        return this.isAmmo(this.getHeldItem(Hand.OFF_HAND).getItem());
    }

    public boolean isAmmo(Item item) {
        return item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;
    }

    public float getStrikingDistance(Entity target) {
        return this.getStrikingDistance(target.getWidth());
    }

    public float getStrikingDistance(float distance) {
        return (float) (Math.pow(this.getWidth() * 2.0F, 2) + distance);
    }

    public float getBlockStrikingDistance() {
        return this.getStrikingDistance(1.0F);
    }

    public LivingEntity getEntityFromUUID(UUID uuid) {
        return getEntityFromUUID(LivingEntity.class, this.world, uuid);
    }

    public static <T extends LivingEntity> T getEntityFromUUID(Class<T> type, World world, UUID uuid) {
        if (uuid != null && world instanceof ServerWorld) {
            Chunk chunk = world.getChunkAt(Yearbooks.getInstance(world).get(uuid));
            ChunkPos pos = chunk.getPos();
            List<T> entities = new ArrayList<>();
            chunk.getEntitiesOfTypeWithinAABB(type, new AxisAlignedBB(pos.getXStart(), 0, pos.getZStart(), pos.getXEnd(), 255, pos.getZEnd()), entities, (entity) -> entity.getUniqueID().equals(uuid));
            if (entities.size() > 0) { return entities.get(0); }
        }
        return null;
    }

    public boolean isVengeful() {
        return this.ticksExisted - this.getRevengeTimer() < 500;
    }

    public boolean isAttacking() {
        return this.canBeTarget(this.getAttackTarget());
    }

    public ChunkPos getChunkPosition() {
        return new ChunkPos(this.getPosition());
    }

    public void setHomePosition(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int) this.getMaximumHomeDistance());
    }

    public double getHomeDistance() {
        return Math.sqrt(this.getDistanceSq(Vector3d.copyCentered(this.getHomePosition())));
    }

    public boolean isTimeTo() {
        return !this.world.isDaytime();
    }

    public abstract void setEmotion(Emotions emotion, int timeout, PlayerEntity entity);

    public void setEmotion(Emotions emotion, int timeout) {
        this.setEmotion(emotion, timeout, null);
    }

    public boolean canWander() {
        if (this.isAttacking() || this.isVengeful() || this.isSleeping()) { return false; }
        return 16 > this.getHomeDistance() || this.getHomeDistance() > 256;
    }
}
