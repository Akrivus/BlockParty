package block_party.entities.abstraction;

import block_party.entities.BlockPartyNPC;
import block_party.entities.goals.HideUntil;
import block_party.registry.CustomTags;
import block_party.registry.resources.BlockAliases;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction layer 2: block data and catch-all block behaviors.
 */
public abstract class Layer2 extends Layer1 {
    public static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(Layer2.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(Layer2.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> CORPOREAL = SynchedEntityData.defineId(Layer2.class, EntityDataSerializers.BOOLEAN);
    private BlockState actualBlockState = Blocks.AIR.defaultBlockState();
    private CompoundTag tileEntityData = new CompoundTag();

    protected Layer2(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Blocks.AIR.defaultBlockState());
        this.entityData.define(SCALE, 1.0F);
        this.entityData.define(CORPOREAL, true);
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("BlockState", Block.getId(this.getActualBlockState()));
        compound.putFloat("Scale", this.getScale());
        compound.putBoolean("IsCorporeal", this.isCorporeal());
        compound.put("TileEntity", this.getTileEntityData());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setBlockState(Block.stateById(compound.getInt("BlockState")));
        this.setScale(compound.getFloat("Scale"));
        this.setIsCorporeal(compound.getBoolean("IsCorporeal"));
        this.setTileEntityData(compound.getCompound("TileEntity"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.isCorporeal())
            this.hide(HideUntil.EXPOSED);
    }

    @Override
    public boolean fireImmune() {
        return !this.getActualBlockState().isFlammable(this.level, this.blockPosition(), this.getDirection());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SCALE.equals(key)) { this.refreshDimensions(); }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.908203125F * this.getScale();
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float scale) {
        this.entityData.set(SCALE, scale);
    }

    public CompoundTag getTileEntityData() {
        return this.tileEntityData;
    }

    public void setTileEntityData(CompoundTag compound) {
        this.tileEntityData = compound == null ? new CompoundTag() : compound;
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, BlockAliases.get(state));
        this.actualBlockState = state;
        if (this.isLocal()) {
            this.setAdditionalBlockStateData(state);
            this.setScale(state.is(CustomTags.IGNORES_VOLUME) ? 1.0F : this.getBlockVolume(state));
            this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            boolean flying = this.is(CustomTags.HAS_WINGS);
            this.moveControl = flying ? new FlyingMoveControl(this, 10, false) : new MoveControl(this);
            this.navigation = flying ? new FlyingPathNavigation(this, this.level) : new GroundPathNavigation(this, this.level);
            if (this.navigation instanceof GroundPathNavigation) {
                GroundPathNavigation ground = (GroundPathNavigation) this.navigation;
                ground.setCanOpenDoors(true);
            }
        }
    }

    public void setAdditionalBlockStateData(BlockState state) {

    }

    public BlockState getActualBlockState() {
        return this.actualBlockState;
    }

    public BlockState getVisibleBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public Block getBlock() {
        return this.getVisibleBlockState().getBlock();
    }

    public boolean is(TagKey<Block> tag) {
        return this.getVisibleBlockState().is(tag);
    }

    public boolean isBlockGlowing() {
        return this.is(CustomTags.HAS_GLOW);
    }

    protected float getBlockVolume(BlockState state) {
        double volume = 0.0F;
        List<AABB> aabbs = state.getOcclusionShape(this.level, this.blockPosition()).toAabbs();
        for (AABB aabb : aabbs)
            volume += (aabb.maxX - aabb.minX) * (aabb.maxY - aabb.minY) * (aabb.maxZ - aabb.minZ);
        volume = Math.cbrt(volume);
        if (!Double.isFinite(volume) || volume < 0.25F)
            volume = 1.0F;
        return (float) volume * 0.9375F;
    }

    protected float getBlockBuffer() {
        return 0.5F / (this.getActualBlockState().getDestroySpeed(this.level, this.blockPosition()) + 1);
    }

    @Override
    public float getVoicePitch() {
        float pitch = (super.getVoicePitch() + this.getBlockBuffer() + 0.6F) / 2;
        return pitch + (1.0F - this.getScale());
    }

    public float[] getEyeColor() {
        int[] colors = this.getAuraColor();
        float[] b = this.getRGB(colors[0]);
        float[] a = this.getRGB(colors[1]);
        float[] rgb = new float[3];
        for (int i = 0; i < rgb.length; ++i) {
            rgb[i] = (b[i] + a[i]) / 2.0F;
        }
        return rgb;
    }

    public int[] getAuraColor() {
        MaterialColor block = this.getActualBlockState().getMaterial().getColor();
        return new int[] { block.col, 0xffffff };
    }

    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 14;
    }

    public void hide(HideUntil until) {
        this.level.setBlock(this.blockPosition(), this.getActualBlockState(), 3);
        this.remove(RemovalReason.DISCARDED);
    }

    public boolean isCorporeal() {
        return this.entityData.get(CORPOREAL);
    }

    public boolean isEthereal() {
        return !this.isCorporeal();
    }

    public void setIsCorporeal(boolean corporeal) {
        this.entityData.set(CORPOREAL, corporeal);
    }

    public void setIsEthereal(boolean corporeal) {
        this.setIsCorporeal(!corporeal);
    }
}
