package moe.blocks.mod.entity;

import moe.blocks.mod.entity.ai.automata.States;
import moe.blocks.mod.entity.ai.automata.state.BlockStates;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.init.MoeBlocks;
import moe.blocks.mod.init.MoeSounds;
import moe.blocks.mod.init.MoeTags;
import moe.blocks.mod.util.Trans;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void registerData() {
        super.registerData();
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(SCALE, 1.0F);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("BlockData", Block.getStateId(this.getBlockData()));
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putFloat("Scale", this.getScale());
        compound.putString("CupSizes", this.getCupSize().name());
        compound.put("Brassiere", this.brassiere.write());
        super.writeAdditional(compound);
    }    @Override
    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setBlockData(Block.getStateById(compound.getInt("BlockData")));
        this.setExtraBlockData((CompoundNBT) compound.get("ExtraBlockData"));
        this.setScale(compound.getFloat("Scale"));
        this.setBrassiere(compound);
        super.readAdditional(compound);
    }    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (BLOCK_STATE.equals(key)) { this.setNextState(States.BLOCK_STATE, BlockStates.get(this.getBlockData()).state); }
        if (SCALE.equals(key)) { this.recalculateSize(); }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        super.dropLoot(cause, player);
        Block.spawnDrops(this.getBlockData(), this.world, this.getPosition(), this.getTileEntity(), cause.getTrueSource(), ItemStack.EMPTY);
        for (int i = 0; i < this.getBrassiere().getSizeInventory(); ++i) {
            ItemStack stack = this.getBrassiere().getStackInSlot(i);
            if (!stack.isEmpty()) { this.entityDropItem(stack); }
        }
    }    @Override
    public BlockState getBlockData() {
        return this.dataManager.get(BLOCK_STATE).orElseGet(() -> super.getBlockData());
    }

    @Override
    public String getGivenName() {
        return Trans.lator(String.format("entity.moeblocks.%s.name", this.getBlockName()), super.getGivenName());
    }    public void setBlockData(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
    }

    public Genders getGender() {
        return this.getBlockData().isIn(MoeTags.MALE) ? Genders.MASCULINE : Genders.FEMININE;
    }    public float getScale() {
        return this.dataManager.get(SCALE);
    }

    @Override
    public void setYearbookPage(CompoundNBT compound, UUID uuid) {
        super.setYearbookPage(compound, uuid);
        compound.putInt("BlockData", Block.getStateId(this.getBlockData()));
        compound.put("ExtraBlockData", this.getExtraBlockData());
        compound.putFloat("Scale", 1.0F);
    }    public void setScale(float scale) {
        this.dataManager.set(SCALE, scale);
    }

    @Override
    public String getFamilyName() {
        return Trans.lator(String.format("entity.moeblocks.%s", this.getBlockName()), String.format("block.%s", this.getBlockName()));
    }

    @Override
    public String getHonorific() {
        if (this.getBlockData().isIn(MoeTags.FULLSIZED)) { return super.getHonorific(); }
        if (this.getBlockData().isIn(MoeTags.BABY)) { return "tan"; }
        return this.getScale() < 1.0F ? "tan" : super.getHonorific();
    }

    public String getBlockName() {
        ResourceLocation block = MoeBlocks.get(this.getBlockData().getBlock()).getRegistryName();
        return String.format("%s.%s", block.getNamespace(), block.getPath());
    }

    public TileEntity getTileEntity() {
        return this.getBlockData().hasTileEntity() ? TileEntity.readTileEntity(this.getBlockData(), this.getExtraBlockData()) : null;
    }

    public MoeEntity.CupSizes getCupSize() {
        return MoeEntity.CupSizes.get(this.brassiere.getSizeInventory());
    }

    public void setCupSize(MoeEntity.CupSizes cup) {
        ListNBT items = this.brassiere.write();
        this.brassiere = new Inventory(cup.getSize());
        this.brassiere.read(items);
    }

    public CompoundNBT getExtraBlockData() {
        return this.extraBlockData;
    }

    public void setExtraBlockData(CompoundNBT compound) {
        this.extraBlockData = compound == null ? new CompoundNBT() : compound;
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return this.getCupSize().getContainer(id, inventory, this.getBrassiere());
    }

    public Inventory getBrassiere() {
        return this.brassiere;
    }

    protected void setBrassiere(CompoundNBT compound) {
        MoeEntity.CupSizes cup = compound.contains("CupSizes") ? MoeEntity.CupSizes.valueOf(compound.getString("CupSizes")) : MoeEntity.CupSizes.B;
        this.brassiere = new Inventory(cup.getSize());
        this.brassiere.read(compound.getList("Brassiere", 10));
    }

    public boolean isBlockGlowing() {
        return this.getBlockData().isIn(MoeTags.GLOWING);
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
        MaterialColor aura = Deres.getAura(this.getDere());
        return new int[]{block.colorValue, aura.colorValue};
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(MoeBlocks.getStepSound(this.getBlockData()), 0.15F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }

    @Override
    public boolean isImmuneToFire() {
        return this.getBlockData().isFlammable(this.world, this.getPosition(), this.getHorizontalFacing());
    }    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MoeSounds.MOE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MoeSounds.MOE_DEAD.get();
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
    protected SoundEvent getAmbientSound() {
        return this.getEmotion().sound;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            this.playSound(MoeSounds.MOE_YELL.get());
            return true;
        }
        return false;
    }

    @Override
    public void onInventoryChanged(IInventory inventory) {

    }

    public enum CupSizes {
        B(1), C(2), D(3), DD(6);

        private final int size;
        private final int rows;

        CupSizes(int rows) {
            this.size = (this.rows = rows) * 9;
        }

        public static CupSizes get(int size) {
            for (CupSizes cup : CupSizes.values()) { if (cup.getSize() == size) { return cup; } }
            return CupSizes.B;
        }

        public int getSize() {
            return this.size;
        }

        public Container getContainer(int id, PlayerInventory inventory, Inventory brassiere) {
            switch (this.rows) {
            default:
                return new ChestContainer(ContainerType.GENERIC_9X1, id, inventory, brassiere, this.rows);
            case 2:
                return new ChestContainer(ContainerType.GENERIC_9X2, id, inventory, brassiere, this.rows);
            case 3:
                return new ChestContainer(ContainerType.GENERIC_9X3, id, inventory, brassiere, this.rows);
            case 4:
                return new ChestContainer(ContainerType.GENERIC_9X4, id, inventory, brassiere, this.rows);
            case 5:
                return new ChestContainer(ContainerType.GENERIC_9X5, id, inventory, brassiere, this.rows);
            case 6:
                return new ChestContainer(ContainerType.GENERIC_9X6, id, inventory, brassiere, this.rows);
            }
        }
    }














}
