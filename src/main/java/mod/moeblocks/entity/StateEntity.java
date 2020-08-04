package mod.moeblocks.entity;

import mod.moeblocks.register.ItemsMoe;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StateEntity extends CreatureEntity {
    private LivingEntity followTarget;
    private LivingEntity avoidTarget;
    private int avoidTimer;

    protected StateEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.registerTargets();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.registerStats();
    }

    @Override
    public void registerData() {
        super.registerData();
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
        this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox()).forEach(item -> {
            if (!item.getItem().isEmpty() && !item.cannotPickup()) {
                this.updateEquipmentIfNeeded(item);
            }
        });
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        if (this.tryEquipItem(stack)) {
            this.onItemPickup(entity, stack.getCount());
            entity.remove();
        }
    }

    public boolean tryEquipItem(ItemStack stack) {
        if (this.canPickUpItem(stack)) {
            EquipmentSlotType slot = this.getSlotForStack(stack);
            ItemStack drop = this.getItemStackFromSlot(slot);
            if (this.shouldExchangeEquipment(stack, drop, slot)) {
                this.entityDropItem(drop);
                this.setItemStackToSlot(slot, stack);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing, EquipmentSlotType slot) {
        if (EnchantmentHelper.hasBindingCurse(existing)) {
            return false;
        } else if (existing.getItem() instanceof TieredItem && candidate.getItem() instanceof TieredItem) {
            TieredItem inbound = (TieredItem) candidate.getItem();
            TieredItem current = (TieredItem) existing.getItem();
            if (current instanceof SwordItem || current instanceof AxeItem) {
                return inbound.getTier().getAttackDamage() > current.getTier().getAttackDamage();
            } else if (current instanceof PickaxeItem) {
                return inbound.getTier().getHarvestLevel() > current.getTier().getHarvestLevel();
            }
            return inbound.getTier().getMaxUses() > current.getTier().getMaxUses();
        } else if (existing.getItem() instanceof ArmorItem) {
            ArmorItem inbound = (ArmorItem) candidate.getItem();
            ArmorItem current = (ArmorItem) existing.getItem();
            return inbound.getDamageReduceAmount() > current.getDamageReduceAmount();
        }
        return existing.isEmpty();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canPickUpItem(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target);
    }

    public boolean canBeTarget(LivingEntity target) {
        return target != null && target.isAlive() && !target.equals(this);
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (this.isAmmo(stack.getItem()) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public boolean isAmmo(Item item) {
        return item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;
    }

    protected void registerStats() {

    }

    protected void registerTargets() {

    }

    public boolean isSuperiorTo(LivingEntity foe) {
        double moeHOD = foe.getHealth() / this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        double foeHOD = this.getHealth();
        if (foe.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            foeHOD /= foe.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        }
        return foeHOD >= moeHOD;
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        Vec3d look = entity.getLook(1.0F).normalize();
        Vec3d cast = new Vec3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public float getFollowSpeed(Entity entity, float factor) {
        float speed = 1.0F + this.getDistance(entity) / factor;
        this.setSprinting(speed > 1.5F);
        return Math.min(speed, 2.0F);
    }

    public void attackEntityFromRange(LivingEntity victim, double factor) {
        ItemStack stack = this.getHeldItem(Hand.MAIN_HAND);
        double dX = victim.getPosX() - this.getPosX();
        double dY = victim.getPosY() - this.getPosY();
        double dZ = victim.getPosZ() - this.getPosZ();
        double d = MathHelper.sqrt(dX * dX + dZ * dZ);
        if (stack.getItem() == Items.BOW || stack.getItem() == Items.CROSSBOW) {
            stack = this.getHeldItem(Hand.OFF_HAND);
            AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, stack.split(1), (float) factor);
            arrow.shoot(dX, dY + d * 0.2, dZ, 1.6F, 0.0F);
            arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT);
            this.world.addEntity(arrow);
        } else {
            IProjectile model = this.getThrowableFromStack(stack.split(1));
            model.shoot(dX, dY + d * 0.2, dZ, 0.75F, 0.0F);
            this.playSound(SoundEvents.ENTITY_SNOWBALL_THROW);
            this.world.addEntity((Entity) model);
        }
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    public IProjectile getThrowableFromStack(ItemStack stack) {
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

    public boolean isMeleeFighter() {
        return this.isWieldingWeapons() && !this.isRangedFighter();
    }

    public boolean isRangedFighter() {
        return this.isWieldingBow() && this.hasAmmo() || this.getThrowableFromStack(this.getHeldItemMainhand()) != null;
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
    }

    public boolean hasAmmo() {
        return this.isAmmo(this.getHeldItem(Hand.OFF_HAND).getItem());
    }

    public boolean isWieldingWeapons() {
        return this.getHeldItem(Hand.MAIN_HAND).getItem().isIn(ItemsMoe.Tags.WEAPONS);
    }

    public int getAttackCooldown() {
        return (int) (1.0 / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() / 5.0);
    }

    public LivingEntity getFollowTarget() {
        return this.followTarget;
    }

    public void setFollowTarget(LivingEntity leader) {
        this.setHomePosAndDistance(this.getPosition(), leader == null ? 16 : -1);
        this.followTarget = leader;
    }

    public boolean isWaiting() {
        return this.followTarget == null;
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || (this.canFly() && source == DamageSource.FALL);
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
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return !this.canFly() || super.onLivingFall(distance, damageMultiplier);
    }
}
