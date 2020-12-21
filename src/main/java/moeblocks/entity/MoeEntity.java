package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.state.BlockStates;
import moeblocks.automata.state.Deres;
import moeblocks.automata.state.Genders;
import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeEntities;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
import java.util.UUID;

public class MoeEntity extends AbstractNPCEntity {
    public static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.FLOAT);
    protected CompoundNBT extraBlockData = new CompoundNBT();
    protected Inventory brassiere;

    public MoeEntity(EntityType<? extends MoeEntity> type, World world) {
        super(type, world);
        this.setBrassiere(new CompoundNBT());
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return this.getCupSize().getContainer(id, inventory, this.getBrassiere());
    }

    public Inventory getBrassiere() {
        return this.brassiere;
    }

    protected void setBrassiere(CompoundNBT compound) {
        CupSizes cup = compound.contains("CupSizes") ? CupSizes.valueOf(compound.getString("CupSizes")) : CupSizes.B;
        this.brassiere = new Inventory(cup.getSize());
        this.brassiere.read(compound.getList("Brassiere", 10));
    }

    public CupSizes getCupSize() {
        return CupSizes.get(this.brassiere.getSizeInventory());
    }

    public void setCupSize(CupSizes cup) {
        ListNBT items = this.brassiere.write();
        this.brassiere = new Inventory(cup.getSize());
        this.brassiere.read(items);
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

    public float getScale() {
        return this.dataManager.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataManager.set(SCALE, scale);
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

    @Override
    public BlockState getBlockData() {
        return this.dataManager.get(BLOCK_STATE).orElseGet(() -> super.getBlockData());
    }

    public void setBlockData(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
    }

    public Genders getGender() {
        return this.isTagSafe(MoeTags.MALE) ? Genders.MASCULINE : Genders.FEMININE;
    }

    @Override
    public String getFamilyName() {
        return Trans.lator(String.format("entity.moeblocks.%s", this.getBlockName()), String.format("block.%s", this.getBlockName()));
    }

    @Override
    public void registerStates() {
        this.states.put(BlockStates.class, new Automaton(this, BlockStates.DEFAULT));
        super.registerStates();
    }

    @Override
    public void registerData() {
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(SCALE, 1.0F);
        super.registerData();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putFloat("Scale", this.getScale());
        compound.putString("CupSizes", this.getCupSize().name());
        compound.put("Brassiere", this.brassiere.write());
        super.writeAdditional(compound);
    }

    public void writeCharacter(CompoundNBT compound) {
        compound.putInt("BlockData", Block.getStateId(this.getBlockData()));
        super.writeCharacter(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setExtraBlockData((CompoundNBT) compound.get("ExtraBlockData"));
        this.setScale(compound.getFloat("Scale"));
        this.setBrassiere(compound);
        super.readAdditional(compound);
    }

    public void readCharacter(CompoundNBT compound) {
        this.setBlockData(Block.getStateById(compound.getInt("BlockData")));
        super.readCharacter(compound);
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        super.dropLoot(cause, player);
        Block.spawnDrops(this.getBlockData(), this.world, this.getPosition(), this.getTileEntity(), cause.getTrueSource(), ItemStack.EMPTY);
        for (int i = 0; i < this.getBrassiere().getSizeInventory(); ++i) {
            ItemStack stack = this.getBrassiere().getStackInSlot(i);
            if (!stack.isEmpty()) { this.entityDropItem(stack); }
        }
    }

    public TileEntity getTileEntity() {
        return this.getBlockData().hasTileEntity() ? TileEntity.readTileEntity(this.getBlockData(), this.getExtraBlockData()) : null;
    }

    @Override
    public String getGivenName() {
        return Trans.lator(String.format("entity.moeblocks.%s.name", this.getBlockName()), super.getGivenName());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (BLOCK_STATE.equals(key)) { this.setNextState(BlockStates.class, BlockStates.get(this.getBlockData())); }
        if (SCALE.equals(key)) { this.recalculateSize(); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }

    public CompoundNBT getExtraBlockData() {
        return this.extraBlockData;
    }

    public void setExtraBlockData(CompoundNBT compound) {
        this.extraBlockData = compound == null ? new CompoundNBT() : compound;
    }

    public String getBlockName() {
        ResourceLocation block = MoeBlocks.get(this.getBlockData().getBlock()).getRegistryName();
        return String.format("%s.%s", block.getNamespace(), block.getPath());
    }

    @Override
    public void onInventoryChanged(IInventory inventory) {

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
        return new float[]{r, g, b};
    }

    private int[] getAuraColor() {
        MaterialColor block = this.getBlockData().getMaterial().getColor();
        return new int[]{block.colorValue, this.getDere().getColor()};
    }

    public boolean isBlockGlowing() {
        return this.isTagSafe(MoeTags.GLOWING);
    }

    public boolean isTagSafe(ITag<Block> tag) {
        try {
            return this.getBlockData().isIn(tag);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static boolean spawn(World world, BlockPos block, BlockPos spawn, float yaw, float pitch, Deres dere, PlayerEntity player) {
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
