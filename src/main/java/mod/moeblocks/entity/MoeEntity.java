package mod.moeblocks.entity;

import mod.moeblocks.MoeMod;
import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.behavior.AbstractBehavior;
import mod.moeblocks.entity.ai.behavior.BasicBehavior;
import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.register.EntityTypesMoe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class MoeEntity extends StateEntity {
    public static final DataParameter<Integer> BEHAVIOR = EntityDataManager.createKey(MoeEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.FLOAT);
    protected CompoundNBT extraBlockData = new CompoundNBT();
    protected AbstractBehavior behavior = new BasicBehavior();

    public MoeEntity(EntityType<MoeEntity> type, World world) {
        super(type, world);
        this.behavior.setMoe(this);
    }

    @Override
    protected void registerStates() {
        this.dataManager.register(BEHAVIOR, Behaviors.MISSING.ordinal());
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(SCALE, 1.0F);
    }

    @Override
    public void func_241841_a(ServerWorld world, LightningBoltEntity lightning) {
        CompoundNBT compound = new CompoundNBT();
        this.writeAdditional(compound);
        compound.remove("Attributes");
        SenpaiEntity senpai = EntityTypesMoe.SENPAI.get().create(world);
        senpai.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), -this.rotationYaw, -this.rotationPitch);
        senpai.read(compound);
        if (this.world.addEntity(senpai)) {
            senpai.onInitialSpawn(world, world.getDifficultyForLocation(this.getPosition()), SpawnReason.TRIGGERED, null, null);
            this.remove();
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("BlockData", Block.getStateId(this.getBlockData()));
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putString("Behavior", this.getBehavior().toString());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setBlockData(Block.getStateById(compound.getInt("BlockData")));
        this.setExtraBlockData((CompoundNBT) compound.get("ExtraBlockData"));
        this.setBehavior(Behaviors.valueOf(compound.getString("Behavior")));
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        Block.spawnDrops(this.getBlockData(), this.world, this.getPosition(), this.getTileEntity(), cause.getTrueSource(), ItemStack.EMPTY);
        super.dropLoot(cause, player);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.isBurning() && this.isReallyImmuneToFire()) {
            this.extinguish();
        }
        if (this.isStandingOn(this.getBlockData())) {
            this.getStressStats().addStressSilently(-0.0001F);
        }
    }

    @Override
    public Iterator<AbstractState> getStates() {
        ArrayList<AbstractState> states = new ArrayList<>();
        states.add(this.getRelationships());
        states.add(this.getFoodStats());
        states.add(this.getBehavior());
        states.add(this.getDere());
        states.add(this.getEmotion());
        states.add(this.getStressStats());
        return states.iterator();
    }

    @Override
    public boolean isMeleeFighter() {
        return (super.isMeleeFighter() && this.dere.isArmed()) || this.behavior.isArmed();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (BEHAVIOR.equals(key)) {
            this.behavior.stop(this.behavior = Behaviors.values()[this.dataManager.get(BEHAVIOR)].get());
        } else if (BLOCK_STATE.equals(key)) {
            this.setBehavior(Behaviors.from(this.dataManager.get(BLOCK_STATE).get()));
        } else if (SCALE.equals(key)) {
            this.recalculateSize();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || this.isReallyImmuneToFire() && source.isFireDamage() || source == DamageSource.DROWN;
    }

    public boolean isReallyImmuneToFire() {
        return !this.getBlockData().getMaterial().isFlammable();
    }

    public TileEntity getTileEntity() {
        return this.getBlockData().hasTileEntity() ? TileEntity.readTileEntity(this.getBlockData(), this.getExtraBlockData()) : null;
    }

    public AbstractBehavior getBehavior() {
        return this.behavior;
    }

    public void setBehavior(Behaviors behavior) {
        this.dataManager.set(BEHAVIOR, behavior.ordinal());
    }

    public CompoundNBT getExtraBlockData() {
        return this.extraBlockData;
    }

    public void setExtraBlockData(CompoundNBT compound) {
        this.extraBlockData = compound;
    }

    public BlockState getBlockData() {
        return this.dataManager.get(BLOCK_STATE).get();
    }

    public void setBlockData(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
    }

    @Override
    public ITextComponent getCustomName() {
        ResourceLocation block = this.getBlockData().getBlock().getRegistryName();
        String translation = String.format("entity.moeblocks.%s.%s", block.getNamespace(), block.getPath());
        TranslationTextComponent component = new TranslationTextComponent(translation);
        if (component.getString().startsWith("entity.moeblocks")) {
            return new TranslationTextComponent("entity.moeblocks.generic", new ItemStack(this.getBlockData().getBlock()).getDisplayName().getString());
        }
        return component;
    }

    @Override
    protected float getSoundPitch() {
        return this.getBehavior().getPitch();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(this.getBehavior().getStepSound(), 0.15F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
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

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return 0.908203125F * this.getScale();
    }
}
