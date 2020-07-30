package mod.moeblocks.entity;

import com.mojang.datafixers.Dynamic;
import mod.moeblocks.MoeMod;
import mod.moeblocks.client.Animations;
import mod.moeblocks.client.animation.Animation;
import mod.moeblocks.entity.ai.behavior.AbstractBehavior;
import mod.moeblocks.entity.ai.behavior.BasicBehavior;
import mod.moeblocks.entity.ai.dere.AbstractDere;
import mod.moeblocks.entity.ai.dere.HimeDere;
import mod.moeblocks.entity.ai.emotion.AbstractEmotion;
import mod.moeblocks.entity.ai.emotion.NormalEmotion;
import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.FoodStats;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

public class MoeEntity extends StateEntity {
    public static final DataParameter<Integer> ANIMATION = EntityDataManager.createKey(MoeEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> BEHAVIOR = EntityDataManager.createKey(MoeEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public static final DataParameter<Integer> DERE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> EMOTION = EntityDataManager.createKey(MoeEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.FLOAT);

    protected Animation animation = new Animation();
    protected AbstractBehavior behavior = new BasicBehavior();
    protected AbstractDere dere = new HimeDere();
    protected AbstractEmotion emotion = new NormalEmotion();
    protected CompoundNBT extraBlockData = new CompoundNBT();
    protected FoodStats foodStats = new FoodStats();

    public MoeEntity(EntityType<MoeEntity> type, World world) {
        super(type, world);
        this.foodStats.start(this);
        this.behavior.setMoe(this);
        this.dere.setMoe(this);
        this.emotion.setMoe(this);
    }

    @Override
    protected void registerStates() {
        this.dataManager.register(ANIMATION, Animations.DEFAULT.ordinal());
        this.dataManager.register(BEHAVIOR, Behaviors.MISSING.ordinal());
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(DERE, Deres.HIMEDERE.ordinal());
        this.dataManager.register(EMOTION, Emotions.NORMAL.ordinal());
        this.dataManager.register(SCALE, 1.0F);
    }

    @Override
    protected void registerStats() {
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25F);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.5F);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
    }

    @Override
    public void tick() {
        this.getAnimation().tick(this);
        this.foodStats.addExhaustion(0.0001F);
        super.tick();
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        if (this.canConsume(stack)) {
            this.consume(stack.split(1));
            if (stack.isEmpty()) {
                entity.remove();
            }
        } else {
            super.updateEquipmentIfNeeded(entity);
        }
    }

    @Override
    public boolean canPickUpItem(ItemStack stack) {
        return super.canPickUpItem(stack) || stack.isFood();
    }

    public boolean canConsume(ItemStack stack) {
        if (stack.isFood()) {
            return this.foodStats.isHungry() || stack.getItem().getFood().canEatWhenFull();
        }
        return false;
    }

    public void consume(ItemStack stack) {
        this.foodStats.consume(stack);
        stack.shrink(1);
        Food food = stack.getItem().getFood();
        food.getEffects().forEach(pair -> {
            if (pair.getLeft() != null && this.rand.nextFloat() < pair.getRight()) {
                this.addPotionEffect(pair.getLeft());
            }
        });
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.getEmotion().getLivingSound();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Animation", this.getAnimation().toString());
        compound.put("BlockData", BlockState.serialize(NBTDynamicOps.INSTANCE, this.getBlockData()).getValue());
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putString("Behavior", this.getBehavior().toString());
        compound.putString("Dere", this.getDere().toString());
        compound.putString("Emotion", this.getEmotion().toString());
        this.getBehavior().write(compound);
        this.getDere().write(compound);
        this.getEmotion().write(compound);
        this.foodStats.write(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setAnimation(Animations.valueOf(compound.getString("Animation")));
        this.setBlockData(BlockState.deserialize(new Dynamic(NBTDynamicOps.INSTANCE, compound.get("BlockData"))));
        this.setExtraBlockData((CompoundNBT) compound.get("ExtraBlockData"));
        this.setBehavior(Behaviors.valueOf(compound.getString("Behavior")));
        this.setDere(Deres.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotions.valueOf(compound.getString("Emotion")));
        this.getBehavior().read(compound);
        this.getDere().read(compound);
        this.getEmotion().read(compound);
        this.foodStats.read(compound);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.getBehavior().tick();
        this.getDere().tick();
        this.getEmotion().tick();
        this.foodStats.tick();
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public void setAnimation(Animations animation) {
        this.dataManager.set(ANIMATION, animation.ordinal());
    }

    public AbstractBehavior getBehavior() {
        return this.behavior;
    }

    public void setBehavior(Behaviors behavior) {
        this.dataManager.set(BEHAVIOR, behavior.ordinal());
    }

    public BlockState getBlockData() {
        return this.dataManager.get(BLOCK_STATE).get();
    }

    public void setBlockData(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
    }

    public AbstractDere getDere() {
        return this.dere;
    }

    public void setDere(Deres dere) {
        this.dataManager.set(DERE, dere.ordinal());
    }

    public CompoundNBT getExtraBlockData() {
        return this.extraBlockData;
    }

    public void setExtraBlockData(CompoundNBT compound) {
        this.extraBlockData = compound;
    }

    public AbstractEmotion getEmotion() {
        return this.emotion;
    }

    public void setEmotion(Emotions emotion) {
        this.dataManager.set(EMOTION, emotion.ordinal());
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public boolean isReallyImmuneToFire() {
        return !this.getBlockData().getMaterial().isFlammable();
    }

    @Override
    public ITextComponent getCustomName() {
        ResourceLocation block = this.getBlockData().getBlock().getRegistryName();
        String translation = String.format("entity.moeblocks.%s.%s", block.getNamespace(), block.getPath());
        TranslationTextComponent component = new TranslationTextComponent(translation);
        if (component.getFormattedText().startsWith("entity.moeblocks")) {
            return new ItemStack(this.getBlockData().getBlock()).getDisplayName().appendText("-chan");
        }
        return component;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEventsMoe.HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEventsMoe.DEAD.get();
    }

    @Override
    public void swingArm(Hand hand) {
        this.foodStats.addExhaustion(0.1F);
        super.swingArm(hand);
    }

    @Override
    protected float getSoundPitch() {
        return this.getBehavior().getPitch();
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
        } else if (BEHAVIOR.equals(key)) {
            this.behavior.stop(this.behavior = Behaviors.values()[this.dataManager.get(BEHAVIOR)].get());
        } else if (BLOCK_STATE.equals(key)) {
            this.setBehavior(Behaviors.from(this.dataManager.get(BLOCK_STATE).get()));
        } else if (DERE.equals(key)) {
            this.dere.stop(this.dere = Deres.values()[this.dataManager.get(DERE)].get());
        } else if (EMOTION.equals(key)) {
            this.emotion.stop(this.emotion = Emotions.values()[this.dataManager.get(EMOTION)].get());
        } else if (SCALE.equals(key)) {
            this.recalculateSize();
        }
    }

    @Override
    public EntitySize getSize(Pose pose) {
        return super.getSize(pose).scale(this.getScale());
    }

    public float getScale() {
        return this.dataManager.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataManager.set(SCALE, scale);
    }

    @Mod.EventBusSubscriber(modid = MoeMod.ID)
    public static class EyeHeight {
        @SubscribeEvent
        public static void setEyeHeight(EntityEvent.EyeHeight e) {
            if (e.getEntity() instanceof MoeEntity) {
                e.setNewHeight(0.908203125F * ((MoeEntity) e.getEntity()).getScale());
            }
        }
    }
}
