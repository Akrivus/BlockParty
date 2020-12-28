package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.state.enums.BlockDataState;
import moeblocks.automata.state.enums.CupSize;
import moeblocks.automata.state.enums.Dere;
import moeblocks.automata.state.enums.Gender;
import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeSounds;
import moeblocks.init.MoeTags;
import moeblocks.util.Trans;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class MoeEntity extends AbstractNPCEntity {
    public static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public static final DataParameter<String> CUP_SIZE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.STRING);
    public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.FLOAT);
    private CompoundNBT extraBlockData = new CompoundNBT();
    private Inventory inventory;
    
    public MoeEntity(EntityType<? extends MoeEntity> type, World world) {
        super(type, world);
        this.setInventory(new CompoundNBT());
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    protected void setInventory(CompoundNBT compound) {
        CupSize cup = compound.contains("CupSizes") ? CupSize.valueOf(compound.getString("CupSizes")) : CupSize.B;
        this.inventory = new Inventory(cup.getSize());
        this.inventory.read(compound.getList("Inventory", 10));
    }
    
    public CupSize getCupSize() {
        return CupSize.get(this.dataManager.get(CUP_SIZE));
    }
    
    public void setCupSize(CupSize cup) {
        this.dataManager.set(CUP_SIZE, cup.toKey());
    }
    
    @Override
    public float getSoundPitch() {
        float hardness = (1.0F - (float) (this.getAttributeValue(Attributes.ARMOR) + 1.0F) / 31.0F) * 0.25F;
        return super.getSoundPitch() + hardness + (1.0F - this.getScale());
    }
    
    @Override
    public EntitySize getSize(Pose pose) {
        return super.getSize(pose).scale(this.getScale());
    }
    
    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return 0.908203125F * this.getScale();
    }
    
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }
    
    @Override
    public String getHonorific() {
        if (this.isTagSafe(MoeTags.FULLSIZED)) { return super.getHonorific(); }
        if (this.isTagSafe(MoeTags.BABY)) { return "tan"; }
        return this.getScale() < 1.0F ? "tan" : super.getHonorific();
    }
    
    public Gender getGender() {
        return this.isTagSafe(MoeTags.MALE) ? Gender.MASCULINE : Gender.FEMININE;
    }
    
    @Override
    public String getGivenName() {
        return Trans.late(String.format("entity.moeblocks.%s.name", this.getBlockName()), super.getGivenName());
    }
    
    @Override
    public void registerStates() {
        this.states.put(BlockDataState.class, new Automaton(this, BlockDataState.DEFAULT).start());
        this.states.put(CupSize.class, new Automaton(this, CupSize.A).start());
        super.registerStates();
    }
    
    @Override
    public void registerData() {
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(SCALE, 1.0F);
        this.dataManager.register(CUP_SIZE, CupSize.A.toKey());
        super.registerData();
    }
    
    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putFloat("Scale", this.getScale());
        compound.put("Inventory", this.inventory.write());
        super.writeAdditional(compound);
    }
    
    public void writeCharacter(CompoundNBT compound) {
        compound.putInt("BlockData", Block.getStateId(this.getBlockData()));
        super.writeCharacter(compound);
    }
    
    @Override
    public String getFamilyName() {
        return Trans.late(String.format("entity.moeblocks.%s", this.getBlockName()), String.format("block.%s", this.getBlockName()));
    }
    
    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setExtraBlockData((CompoundNBT) compound.get("ExtraBlockData"));
        this.setScale(compound.getFloat("Scale"));
        this.setInventory(compound);
        super.readAdditional(compound);
    }
    
    public void readCharacter(CompoundNBT compound) {
        this.setBlockData(Block.getStateById(compound.getInt("BlockData")));
        this.setCupSize(CupSize.get(compound.getString("CupSize")));
        super.readCharacter(compound);
    }
    
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (CUP_SIZE.equals(key)) { this.setNextState(CupSize.class, this.getCupSize()); }
        if (SCALE.equals(key)) { this.recalculateSize(); }
        super.notifyDataManagerChange(key);
    }
    
    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        super.dropLoot(cause, player);
        Block.spawnDrops(this.getBlockData(), this.world, this.getPosition(), this.getTileEntity(), cause.getTrueSource(), ItemStack.EMPTY);
        for (int i = 0; i < this.getInventory().getSizeInventory(); ++i) {
            ItemStack stack = this.getInventory().getStackInSlot(i);
            if (!stack.isEmpty()) { this.entityDropItem(stack); }
        }
    }
    
    @Override
    protected void onTeleport() {
        this.playSound(MoeSounds.MOE_ENTITY_FOLLOW.get());
        this.setFollowing(true);
    }
    
    private TileEntity getTileEntity() {
        return this.getBlockData().hasTileEntity() ? TileEntity.readTileEntity(this.getBlockData(), this.getExtraBlockData()) : null;
    }
    
    @Override
    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }
    
    @Override
    public BlockState getBlockData() {
        return this.dataManager.get(BLOCK_STATE).orElseGet(() -> super.getBlockData());
    }
    
    public CompoundNBT getExtraBlockData() {
        return this.extraBlockData;
    }
    
    public float getScale() {
        return this.dataManager.get(SCALE);
    }
    
    public void setScale(float scale) {
        this.dataManager.set(SCALE, scale);
    }
    
    public void setExtraBlockData(CompoundNBT compound) {
        this.extraBlockData = compound == null ? new CompoundNBT() : compound;
    }
    
    public void setBlockData(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
    }
    
    public String getBlockName() {
        ResourceLocation block = MoeBlocks.get(this.getBlockData().getBlock()).getRegistryName();
        return String.format("%s.%s", block.getNamespace(), block.getPath());
    }
    
    private boolean isTagSafe(ITag<Block> tag) {
        try {
            return this.getBlockData().isIn(tag);
        } catch (IllegalStateException e) {
            return false;
        }
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
    
    private float[] getRGB(int hex) {
        float r = ((hex & 0xff0000) >> 16) / 255.0F;
        float g = ((hex & 0xff00) >> 8) / 255.0F;
        float b = ((hex & 0xff) >> 1) / 255.0F;
        return new float[] { r, g, b };
    }
    
    private int[] getAuraColor() {
        MaterialColor block = this.getBlockData().getMaterial().getColor();
        return new int[] { block.colorValue, this.getDere().getColor() };
    }
    
    public boolean isBlockGlowing() {
        return this.isTagSafe(MoeTags.GLOWING);
    }
    
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(MoeBlocks.getStepSound(this.getBlockData()), 0.15F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }
    
    @Override
    public boolean isImmuneToFire() {
        return this.getBlockData().isFlammable(this.world, this.getPosition(), this.getHorizontalFacing());
    }
    
    public float getTiddyAngle() {
        return this.getCupSize().getAngle();
    }
    
    public static boolean spawn(World world, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, PlayerEntity player) {
        BlockState state = world.getBlockState(block);
        if (!state.getBlock().isIn(MoeTags.MOEABLES)) { return false; }
        TileEntity extra = world.getTileEntity(block);
        MoeEntity moe = MoeEntities.MOE.get().create(world);
        moe.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        moe.setBlockData(state);
        moe.setExtraBlockData(extra != null ? extra.getTileData() : new CompoundNBT());
        moe.setDere(dere);
        if (world.addEntity(moe)) {
            moe.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(spawn), SpawnReason.TRIGGERED, null, null);
            if (player != null) { moe.setProtagonist(player); }
            return world.destroyBlock(block, false);
        }
        return false;
    }
}
