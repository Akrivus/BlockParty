package moeblocks.entity;

import moeblocks.automata.MarkovChain;
import moeblocks.automata.State;
import moeblocks.automata.trait.Dere;
import moeblocks.automata.trait.Gender;
import moeblocks.convo.Dialogue;
import moeblocks.convo.enums.Response;
import moeblocks.data.Moe;
import moeblocks.init.*;
import moeblocks.message.SOpenDialogue;
import moeblocks.util.Trans;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class MoeEntity extends AbstractNPCEntity<Moe> {
    public static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    public static final DataParameter<Float> SCALE = EntityDataManager.createKey(MoeEntity.class, DataSerializers.FLOAT);
    private CompoundNBT tileEntityData = new CompoundNBT();

    public MoeEntity(EntityType<? extends MoeEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void registerData() {
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.AIR.getDefaultState()));
        this.dataManager.register(SCALE, 1.0F);
        super.registerData();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("BlockState", Block.getStateId(this.getInternalBlockState()));
        compound.putFloat("Scale", this.getScale());
        compound.put("TileEntity", this.getTileEntityData());
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setBlockState(Block.getStateById(compound.getInt("BlockState")));
        this.setScale(compound.getFloat("Scale"));
        this.setTileEntityData(compound.getCompound("TileEntity"));
        super.readAdditional(compound);
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        Block.spawnDrops(this.getInternalBlockState(), this.world, this.getPosition(), this.getTileEntity(), cause.getTrueSource(), ItemStack.EMPTY);
        super.dropLoot(cause, player);
    }

    @Override
    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        switch (hand) {
        default:
            this.setState(MarkovChain.start(0.8, State.JUMP).chain(0.2, State.LOOK_AT_PLAYER));
            return ActionResultType.SUCCESS;
        case OFF_HAND:
            return ActionResultType.PASS;
        }
    }

    @Override
    public void updateHungerState() {

    }

    @Override
    public void updateLonelyState() {

    }

    @Override
    public void updateStressState() {

    }

    @Override
    public void updateActionState() {

    }

    @Override
    public void updateSleepState() {

    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.isRemote() && this.rand.nextInt(10) == 0 && (!Minecraft.getInstance().player.getUniqueID().equals(this.getPlayerUUID()) || this.getLoyalty() < 5.0F)) {
            Supplier<BasicParticleType> particle = this.getLoyalty() < 5.0F ? MoeParticles.WHITE_SAKURA : MoeParticles.SAKURA;
            double x = Math.sin(0.0174444444D * this.world.getDayTime() / 1000 * 15.0D) * this.rand.nextDouble();
            double z = Math.cos(0.0174444444D * this.world.getDayTime() / 1000 * 15.0D) * this.rand.nextDouble();
            double y = Math.abs(this.rand.nextGaussian()) * -1.0D;
            this.world.addParticle(particle.get(), this.getPosX() + this.rand.nextDouble() - 0.5, this.getPosY() + this.rand.nextDouble() * this.getHeight() / 2 + this.getHeight() / 2, this.getPosZ() + this.rand.nextDouble() - 0.5, x, y, z);
        }
    }

    public void say(PlayerEntity player, String line, Response... responses) {
        if (line.length() > 128) { throw new IllegalArgumentException("Lines can't be over 128 characters long."); }
        if (responses.length == 0) { responses = new Response[] { Response.CLOSE }; }
        MoeMessages.send(player, new SOpenDialogue(new Dialogue(this.getRow(), line, responses)));
    }

    public Gender getGender() {
        return this.isBlock(MoeTags.MALE) ? Gender.MASCULINE : Gender.FEMININE;
    }

    public boolean isBlock(ITag<Block> tag) {
        try {
            return this.getInternalBlockState().isIn(tag);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public String getBlockName() {
        ResourceLocation block = MoeOverrides.get(this.getInternalBlockState().getBlock()).getRegistryName();
        return Trans.late(String.format("entity.moeblocks.%s.%s", block.getNamespace(), block.getPath()));
    }

    @Override
    public String getHonorific() {
        String honorific = this.is(Gender.MASCULINE) ? "kun" : "chan";
        if (this.is(MoeTags.FULLSIZED)) {
            return honorific;
        } else if (this.is(MoeTags.BABY) || this.getScale() < 1.0F) {
            return "tan";
        }
        return honorific;
    }

    @Override
    public AbstractNPCEntity onTeleport(AbstractNPCEntity entity) {
        entity.setFollowing(true);
        entity.playSound(MoeSounds.ENTITY_MOE_FOLLOW.get());
        return entity;
    }

    @Override
    public Moe getRow() {
        return MoeWorldData.Moes.find(this.getDatabaseID());
    }

    @Override
    public Moe getNewRow() {
        return new Moe(this);
    }

    private TileEntity getTileEntity() {
        return this.getInternalBlockState().hasTileEntity() ? TileEntity.readTileEntity(this.getInternalBlockState(), this.getTileEntityData()) : null;
    }

    @Override
    public void setBlockState(BlockState state) {
        this.dataManager.set(BLOCK_STATE, Optional.of(state));
        if (this.isLocal()) {
            this.setScale(state.isIn(MoeTags.FULLSIZED) ? 1.0F : this.getBlockVolume(state));
            this.setPathPriority(PathNodeType.DAMAGE_FIRE, this.isImmuneToFire() ? 0.0F : -1.0F);
            this.setPathPriority(PathNodeType.DANGER_FIRE, this.isImmuneToFire() ? 0.0F : -1.0F);
            this.setCanFly(state.isIn(MoeTags.WINGED));
        }
    }

    private float getBlockVolume(BlockState state) {
        VoxelShape shape = state.getRenderShape(this.world, this.getPosition());
        float dX = (float) (shape.getEnd(Direction.Axis.X) - shape.getStart(Direction.Axis.X));
        float dY = (float) (shape.getEnd(Direction.Axis.Y) - shape.getStart(Direction.Axis.Y));
        float dZ = (float) (shape.getEnd(Direction.Axis.Z) - shape.getStart(Direction.Axis.Z));
        float volume = (float) (Math.cbrt(dX * dY * dZ));
        return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public boolean isImmuneToFire() {
        return !this.getInternalBlockState().isFlammable(this.world, this.getPosition(), this.getHorizontalFacing());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }

    public void setCanFly(boolean fly) {
        this.moveController = fly ? new FlyingMovementController(this, 10, false) : new MovementController(this);
        this.navigator = fly ? new FlyingPathNavigator(this, this.world) : new GroundPathNavigator(this, this.world);
        if (this.navigator instanceof GroundPathNavigator) {
            GroundPathNavigator ground = (GroundPathNavigator) this.navigator;
            ground.setBreakDoors(true);
        }
    }

    public CompoundNBT getTileEntityData() {
        return this.tileEntityData;
    }

    public void setTileEntityData(CompoundNBT compound) {
        this.tileEntityData = compound == null ? new CompoundNBT() : compound;
    }

    public float getScale() {
        return this.dataManager.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataManager.set(SCALE, scale);
    }

    @Override
    public BlockState getInternalBlockState() {
        return this.dataManager.get(BLOCK_STATE).orElseGet(() -> Blocks.AIR.getDefaultState());
    }

    @Override
    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }

    @Override
    public boolean openSpecialMenuFor(PlayerEntity player) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(MoeOverrides.getStepSound(this.getInternalBlockState()), 0.15F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount * this.getBlockBuffer());
    }

    @Override
    public float getSoundPitch() {
        float hardness = (1.0F - this.getBlockBuffer()) * 0.25F;
        return super.getSoundPitch() + hardness + (1.0F - this.getScale());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SCALE.equals(key)) { this.recalculateSize(); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void onMention(PlayerEntity player, String message) {
        this.playSound(MoeSounds.ENTITY_MOE_CONFUSED.get());
        this.jump();
        this.setState(State.LOOK_AT_PLAYER);
    }

    @Override
    public EntitySize getSize(Pose pose) {
        return super.getSize(pose).scale(this.getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return 0.908203125F * this.getScale();
    }

    private float getBlockBuffer() {
        return 0.5F / this.getInternalBlockState().getBlockHardness(this.world, this.getPosition());
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ChestContainer(ContainerType.GENERIC_9X3, id, inventory, this.inventory, 3);
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
        MaterialColor block = this.getInternalBlockState().getMaterial().getColor();
        return new int[] { block.colorValue, 0xffffff };
    }

    public boolean isBlockGlowing() {
        return this.isBlock(MoeTags.GLOWING);
    }

    public static boolean spawn(World world, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, PlayerEntity player) {
        BlockState state = world.getBlockState(block);
        if (!state.getBlock().isIn(MoeTags.Blocks.MOEABLES)) { return false; }
        TileEntity extra = world.getTileEntity(block);
        MoeEntity moe = MoeEntities.MOE.get().create(world);
        moe.setPositionAndRotation(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        moe.setDatabaseID(UUID.randomUUID());
        moe.setBlockState(state);
        moe.setTileEntityData(extra != null ? extra.getTileData() : new CompoundNBT());
        moe.setDere(dere);
        moe.claim(player);
        if (world.addEntity(moe)) {
            moe.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(spawn), SpawnReason.TRIGGERED, null, null);
            if (player != null) { moe.setPlayer(player); }
            return world.destroyBlock(block, false);
        }
        return false;
    }
}
