package block_party.mob;

import block_party.BlockParty;
import block_party.BlockPartyDB;
import block_party.client.animation.AbstractAnimation;
import block_party.client.animation.Animation;
import block_party.client.render.layer.MoeSpecialRenderer;
import block_party.client.render.layer.special.BarrelOverlay;
import block_party.convo.Dialogue;
import block_party.convo.enums.Response;
import block_party.db.Recordable;
import block_party.db.records.NPC;
import block_party.init.BlockPartyEntities;
import block_party.init.BlockPartyMessages;
import block_party.init.BlockPartySounds;
import block_party.init.BlockPartyTags;
import block_party.message.SOpenDialogue;
import block_party.mob.automata.*;
import block_party.mob.automata.trait.BloodType;
import block_party.mob.automata.trait.Dere;
import block_party.mob.automata.trait.Emotion;
import block_party.mob.automata.trait.Gender;
import block_party.util.DimBlockPos;
import block_party.util.Trans;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.Tag;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.*;
import java.util.function.Consumer;

public class Partyer extends PathfinderMob implements ContainerListener, Recordable<NPC>, MenuProvider {
    public static final EntityDataAccessor<Optional<BlockState>> BLOCK_STATE = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
    private CompoundTag tileEntityData = new CompoundTag();

    public Partyer(EntityType<? extends Partyer> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, 0.0F);
        this.restrictTo(this.blockPosition(), 16);
        this.nextTickOps = new LinkedList<>();
        this.automaton = new Automaton(this);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.of(Blocks.AIR.defaultBlockState()));
        this.entityData.define(SCALE, 1.0F);
        this.entityData.define(FOLLOWING, false);
        this.entityData.define(PLAYER_UUID, "00000000-0000-0000-0000-000000000000");
        this.entityData.define(DATABASE_ID, "00000000-0000-0000-0000-000000000000");
        this.entityData.define(BLOOD_TYPE, BloodType.O.getValue());
        this.entityData.define(DERE, Dere.NYANDERE.getValue());
        this.entityData.define(EMOTION, Emotion.NORMAL.getValue());
        this.entityData.define(GENDER, Gender.FEMININE.getValue());
        this.entityData.define(ANIMATION, Animation.DEFAULT.name());
        this.entityData.define(GIVEN_NAME, "Tokumei");
        this.entityData.define(SLOUCH, 0.0F);
        this.entityData.define(FULLNESS, 20.0F);
        this.entityData.define(EXHAUSTION, 0.0F);
        this.entityData.define(SATURATION, 6.0F);
        this.entityData.define(STRESS, 0.0F);
        this.entityData.define(RELAXATION, 0.0F);
        this.entityData.define(LOYALTY, 6.0F);
        this.entityData.define(AFFECTION, 0.0F);
        this.entityData.define(AGE, 0.0F);
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("BlockState", Block.getId(this.getInternalBlockState()));
        compound.putFloat("Scale", this.getScale());
        compound.put("TileEntity", this.getTileEntityData());
        compound.putUUID("DatabaseID", this.getDatabaseID());
        compound.putBoolean("Following", this.isFollowing());
        compound.put("Inventory", this.inventory.createTag());
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLonely", this.timeUntilLonely);
        compound.putInt("TimeUntilStress", this.timeUntilStress);
        compound.putInt("TimeSinceSlept", this.timeSinceSlept);
        this.automaton.write(compound);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setBlockState(Block.stateById(compound.getInt("BlockState")));
        this.setScale(compound.getFloat("Scale"));
        this.setTileEntityData(compound.getCompound("TileEntity"));
        this.setDatabaseID(compound.getUUID("DatabaseID"));
        this.setFollowing(compound.getBoolean("Following"));
        this.inventory.fromTag(compound.getList("Inventory", Constants.NBT.TAG_COMPOUND));
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSlept = compound.getInt("TimeSinceSlept");
        this.automaton.read(compound);
        this.getRow().load(this);
        super.readAdditionalSaveData(compound);
        this.readyToSync = true;
    }

    public InteractionResult onInteract(Player player, ItemStack stack, InteractionHand hand) {
        switch (hand) {
        default:
            this.setState(MarkovChain.start(0.8, State.JUMP).chain(0.2, State.LOOK_AT_PLAYER));
            return InteractionResult.SUCCESS;
        case OFF_HAND:
            return InteractionResult.PASS;
        }
    }

    public void updateHungerState() {

    }

    public void updateLonelyState() {

    }

    public void updateStressState() {

    }

    public void updateActionState() {

    }

    public void updateSleepState() {

    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.automaton.tick(this);
        if (this.isLocal()) {
            this.updateHungerState();
            this.updateLonelyState();
            this.updateStressState();
            this.updateActionState();
            this.updateSleepState();
        } else {
            this.animation.tick(this);
        }
    }

    public void say(Player player, String line, Response... responses) {
        if (line.length() > 128) { throw new IllegalArgumentException("Lines can't be over 128 characters long."); }
        if (responses.length == 0) { responses = new Response[] { Response.CLOSE }; }
        BlockPartyMessages.send(player, new SOpenDialogue(new Dialogue(this.getRow(), line, responses)));
    }

    public Gender getGender() {
        return this.isBlock(BlockPartyTags.MALE) ? Gender.MASCULINE : Gender.FEMININE;
    }

    public boolean isBlock(Tag<Block> tag) {
        try {
            return this.getInternalBlockState().is(tag);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public String getBlockName() {
        ResourceLocation block = Overrides.get(this.getInternalBlockState().getBlock()).getRegistryName();
        return Trans.late(String.format("entity.block_party.%s.%s", block.getNamespace(), block.getPath()));
    }

    public String getHonorific() {
        String honorific = this.is(Gender.MASCULINE) ? "kun" : "chan";
        if (this.is(BlockPartyTags.FULLSIZED)) {
            return honorific;
        } else if (this.is(BlockPartyTags.BABY) || this.getScale() < 1.0F) {
            return "tan";
        }
        return honorific;
    }

    public Partyer onTeleport(Partyer entity) {
        entity.setFollowing(true);
        entity.playSound(BlockPartySounds.ENTITY_MOE_FOLLOW.get());
        return entity;
    }

    @Override
    public NPC getRow() {
        return BlockPartyDB.Partyers.find(this.getDatabaseID());
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this);
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, Optional.of(state));
        if (this.isLocal()) {
            this.setScale(state.is(BlockPartyTags.FULLSIZED) ? 1.0F : this.getBlockVolume(state));
            this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setCanFly(state.is(BlockPartyTags.WINGED));
        }
    }

    private float getBlockVolume(BlockState state) {
        VoxelShape shape = state.getOcclusionShape(this.level, this.blockPosition());
        float dX = (float) (shape.max(Direction.Axis.X) - shape.min(Direction.Axis.X));
        float dY = (float) (shape.max(Direction.Axis.Y) - shape.min(Direction.Axis.Y));
        float dZ = (float) (shape.max(Direction.Axis.Z) - shape.min(Direction.Axis.Z));
        float volume = (float) (Math.cbrt(dX * dY * dZ));
        return Float.isFinite(volume) ? Math.min(Math.max(volume, 0.25F), 1.5F) : 1.0F;
    }

    public boolean isSitting() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return !this.getInternalBlockState().isFlammable(this.level, this.blockPosition(), this.getDirection());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }

    public void setCanFly(boolean fly) {
        this.moveControl = fly ? new FlyingMoveControl(this, 10, false) : new MoveControl(this);
        this.navigation = fly ? new FlyingPathNavigation(this, this.level) : new GroundPathNavigation(this, this.level);
        if (this.navigation instanceof GroundPathNavigation) {
            GroundPathNavigation ground = (GroundPathNavigation) this.navigation;
            ground.setCanOpenDoors(true);
        }
    }

    public CompoundTag getTileEntityData() {
        return this.tileEntityData;
    }

    public void setTileEntityData(CompoundTag compound) {
        this.tileEntityData = compound == null ? new CompoundTag() : compound;
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float scale) {
        this.entityData.set(SCALE, scale);
    }

    public BlockState getInternalBlockState() {
        return this.entityData.get(BLOCK_STATE).orElseGet(() -> Blocks.AIR.defaultBlockState());
    }

    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(Overrides.getStepSound(this.getInternalBlockState()), 0.15F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount * this.getBlockBuffer());
    }

    @Override
    public float getVoicePitch() {
        float hardness = (1.0F - this.getBlockBuffer()) * 0.25F;
        return super.getVoicePitch() + hardness + (1.0F - this.getScale());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SCALE.equals(key)) { this.refreshDimensions(); }
        if (ANIMATION.equals(key)) { this.setAnimation(Animation.get(this.entityData.get(ANIMATION))); }
        super.onSyncedDataUpdated(key);
        if (this.hasRow()) {
            this.getRow().update(this);
        }
    }

    public void onMention(Player player, String message) {
        this.playSound(BlockPartySounds.ENTITY_MOE_CONFUSED.get());
        this.jumpFromGround();
        this.setState(State.LOOK_AT_PLAYER);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.908203125F * this.getScale();
    }

    private float getBlockBuffer() {
        return 0.5F / this.getInternalBlockState().getDestroySpeed(this.level, this.blockPosition());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x3, id, inventory, this.inventory, 3);
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
        return new int[] { block.col, 0xffffff };
    }

    public boolean isBlockGlowing() {
        return this.isBlock(BlockPartyTags.GLOWING);
    }

    public static boolean spawn(Level world, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, Player player) {
        BlockState state = world.getBlockState(block);
        if (!state.is(BlockPartyTags.Blocks.MOEABLES)) { return false; }
        BlockEntity extra = world.getBlockEntity(block);
        Partyer partyer = BlockPartyEntities.PARTYER.get().create(world);
        partyer.absMoveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        partyer.setDatabaseID(UUID.randomUUID());
        partyer.setBlockState(state);
        partyer.setTileEntityData(extra != null ? extra.getTileData() : new CompoundTag());
        partyer.setDere(dere);
        partyer.claim(player);
        if (world.addFreshEntity(partyer)) {
            partyer.finalizeSpawn((ServerLevel) world, world.getCurrentDifficultyAt(spawn), MobSpawnType.TRIGGERED, null, null);
            if (player != null) { partyer.setPlayer(player); }
            return world.destroyBlock(block, false);
        }
        return false;
    }

        public static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.BOOLEAN);
        public static final EntityDataAccessor<String> PLAYER_UUID = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> BLOOD_TYPE = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> DERE = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> EMOTION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> GENDER = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<String> GIVEN_NAME = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.STRING);
        public static final EntityDataAccessor<Float> FULLNESS = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> EXHAUSTION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> SATURATION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> STRESS = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> RELAXATION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> LOYALTY = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> AFFECTION = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> SLOUCH = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public static final EntityDataAccessor<Float> AGE = SynchedEntityData.defineId(Partyer.class, EntityDataSerializers.FLOAT);
        public final SimpleContainer inventory = new SimpleContainer(36);
        public final Automaton automaton;
        private final LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
        private final Queue<Consumer<Partyer>> nextTickOps;
        private AbstractAnimation animation;
        private int timeUntilHungry;
        private int timeUntilLonely;
        private int timeUntilStress;
        private int timeSinceSlept;
        private long lastSeen;
        private boolean readyToSync;

        @Override
        protected void dropFromLootTable(DamageSource cause, boolean player) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = this.getItemBySlot(slot);
                if (stack.isEmpty()) { continue; }
                this.spawnAtLocation(stack);
            }
        }

        @Override
        public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

        @Override
        public void customServerAiStep() {
            Consumer<Partyer> op = this.nextTickOps.poll();
            if (op != null) { op.accept(this); }
        }

        @Override
        protected float getEquipmentDropChance(EquipmentSlot slot) { return 0.0F; }

        @Override
        protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) { return; }

        @Override
        public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag compound) {
            this.setGivenName(this.getGender().getUniqueName(this.level));
            this.setBloodType(this.getBloodType().weigh(this.random));
            return super.finalizeSpawn(world, difficulty, reason, data, compound);
        }

        @Override
        public InteractionResult interactAt(Player player, Vec3 vector, InteractionHand hand) {
            return this.onInteract(player, player.getItemInHand(hand), hand);
        }

        public void jumpFromGround() {
            super.jumpFromGround();
        }

        public void setState(IState state, IState... states) {
            this.automaton.setState(this, state, states);
        }

        public boolean isLocal() {
            return !this.isRemote();
        }

        public boolean isRemote() {
            return this.level.isClientSide();
        }

        public void setLastSeen(long lastSeen) {
            this.lastSeen = lastSeen;
        }

        public long getLastSeen() {
            return this.lastSeen;
        }

        public void addLastSeen(long lastSeen) {
            this.setLastSeen(this.lastSeen + lastSeen);
        }

        public boolean isFollowing() {
            return this.entityData.get(FOLLOWING);
        }

        public void setFollowing(boolean following) {
            this.entityData.set(FOLLOWING, following);
        }

        public UUID getPlayerUUID() {
            return UUID.fromString(this.entityData.get(PLAYER_UUID));
        }

        public void setPlayerUUID(UUID playerUUID) {
            this.entityData.set(PLAYER_UUID, playerUUID.toString());
        }

        public UUID getDatabaseID() {
            return UUID.fromString(this.entityData.get(DATABASE_ID));
        }

        public void setDatabaseID(UUID uuid) {
            this.entityData.set(DATABASE_ID, uuid.toString());
        }

        public String getGivenName() {
            return this.entityData.get(GIVEN_NAME);
        }

        public void setGivenName(String name) {
            this.entityData.set(GIVEN_NAME, name);
        }

        public float getFullness() {
            return this.entityData.get(FULLNESS);
        }

        public void setFullness(float fullness) {
            this.entityData.set(FULLNESS, fullness);
        }

        public float getExhaustion() {
            return this.entityData.get(EXHAUSTION);
        }

        public void setExhaustion(float exhaustion) {
            this.entityData.set(EXHAUSTION, exhaustion);
        }

        public float getSaturation() {
            return this.entityData.get(SATURATION);
        }

        public void setSaturation(float saturation) {
            this.entityData.set(SATURATION, saturation);
        }

        public float getStress() {
            return this.entityData.get(STRESS);
        }

        public void setStress(float stress) {
            this.entityData.set(STRESS, stress);
        }

        public float getRelaxation() {
            return this.entityData.get(RELAXATION);
        }

        public void setRelaxation(float relaxation) {
            this.entityData.set(RELAXATION, relaxation);
        }

        public float getLoyalty() {
            return this.entityData.get(LOYALTY);
        }

        public void setLoyalty(float loyalty) {
            this.entityData.set(LOYALTY, loyalty);
        }

        public float getAffection() {
            return this.entityData.get(AFFECTION);
        }

        public void setAffection(float affection) {
            this.entityData.set(AFFECTION, affection);
        }

        public float getSlouch() {
            return this.entityData.get(SLOUCH);
        }

        public void setSlouch(float slouch) {
            this.entityData.set(SLOUCH, slouch);
        }

        public float getAge() {
            return this.entityData.get(AGE);
        }

        public void setAge(float age) {
            this.entityData.set(AGE, age);
        }

        public BloodType getBloodType() {
            return BloodType.O.fromValue(this.entityData.get(BLOOD_TYPE));
        }

        public void setBloodType(BloodType bloodType) {
            this.entityData.set(BLOOD_TYPE, bloodType.getValue());
        }

        public Dere getDere() {
            return Dere.NYANDERE.fromValue(this.entityData.get(DERE));
        }

        public void setDere(Dere dere) {
            this.entityData.set(DERE, dere.getValue());
        }

        public Emotion getEmotion() {
            return Emotion.NORMAL.fromValue(this.entityData.get(GENDER));
        }

        public void setEmotion(Emotion emotion) {
            this.entityData.set(EMOTION, emotion.getValue());
        }

        public void setGender(Gender gender) {
            this.entityData.set(GENDER, gender.getValue());
        }

        public Component getTypeName() {
            return new TranslatableComponent("entity.block_party.profession", this.getGivenName(), this.getBlockName());
        }

        @Override
        public void restoreFrom(Entity entity) {
            super.restoreFrom(entity);
            this.setUUID(UUID.randomUUID());
        }

        public Component getCustomName() {
            return new TranslatableComponent("entity.block_party.generic", this.getGivenName(), this.getHonorific());
        }

        @Override
        public boolean hasCustomName() { return true; }

        @Override
        protected void dropEquipment() {
            for (int slot = 0; slot < 36; ++slot) {
                ItemStack stack = this.inventory.getItem(slot);
                if (stack.isEmpty()) { continue; }
                this.spawnAtLocation(stack);
            }
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) { return this.itemHandler.cast(); }
            return super.getCapability(capability, facing);
        }

        @Override
        public void invalidateCaps() {
            super.invalidateCaps();
            this.itemHandler.invalidate();
        }

        @Override
        public void containerChanged(Container inventory) {
            this.resetSlouch();
        }

        public void resetSlouch() {
            this.setSlouch(this.recalcSlouch());
        }

        public float recalcSlouch() {
            float size = 0.0F;
            for (int i = 0; i < 36; ++i) {
                if (inventory.getItem(i).isEmpty()) { continue; }
                size += 0.0277777778F;
            }
            return size;
        }

        public boolean isBeingLookedThrough() {
            if (!this.isPlayerBusy()) { return false; }
            AbstractContainerMenu container = this.getPlayer().containerMenu;
            if (container instanceof ChestMenu) {
                Container inventory = ((ChestMenu) container).getContainer();
                return inventory.equals(this.inventory);
            }
            return false;
        }

        public boolean isPlayerBusy() {
            return this.isPlayerOnline() && this.getPlayer().containerMenu != this.getPlayer().inventoryMenu;
        }

        public boolean isPlayerOnline() {
            return this.getPlayer() != null;
        }

        public boolean isPlayer(Entity entity) {
            return entity != null && this.getPlayerUUID().equals(entity.getUUID());
        }

        @Override
        public DimBlockPos getDimBlockPos() {
            return new DimBlockPos(this.level.dimension(), this.blockPosition());
        }

        public void say(String key, Object... params) {
            this.level.players().forEach((player) -> {
                if (player.distanceTo(this) < 8.0D) { this.say(player, key, params); }
            });
        }

        public void say(Player player, String key, Object... params) {
            this.say(player, new TranslatableComponent(key, params));
        }

        public void say(Player player, Component component) {
            player.sendMessage(component, player.getUUID());
        }

        public Partyer teleport(ServerLevel world, ITeleporter teleporter) {
            Entity entity = this.changeDimension(world, teleporter);
            if (entity instanceof Partyer) {
                return this.onTeleport((Partyer) entity);
            } else {
                return this;
            }
        }

        public void addExhaustion(float exhaustion) {
            this.setExhaustion(this.getExhaustion() + exhaustion);
        }

        public void addSaturation(float saturation) {
            this.setSaturation(this.getSaturation() + saturation);
        }

        public void addFullness(float fullness) {
            this.setFullness(this.getFullness() + fullness);
        }

        public void addAffection(float affection) {
            this.setAffection(this.getAffection() + affection);
        }

        public void addLoyalty(float loyalty) {
            this.setLoyalty(this.getLoyalty() + loyalty);
        }

        public boolean openChestFor(Player player) {
            player.openMenu(this);
            return true;
        }

        public void addNextTickOp(Consumer<Partyer> op) {
            this.nextTickOps.add(op);
        }

        public void addStress(float stress) {
            this.setStress(this.getStress() + stress);
        }

        public void addRelaxation(float relaxation) {
            this.setRelaxation(this.getRelaxation() + relaxation);
        }

        public void addAge(float age) {
            this.setAge(this.getAge() + age);
        }

        public AbstractAnimation getAnimation() {
            return this.animation;
        }

        public void setAnimation(AbstractAnimation animation) {
            this.animation = animation;
        }

        public Animation getAnimationKey() {
            return Animation.valueOf(this.entityData.get(ANIMATION));
        }

        public void setAnimationKey(Animation animation) {
            this.entityData.set(ANIMATION, animation.name());
        }

        public Player getPlayer() {
            return this.level.getPlayerByUUID(this.getPlayerUUID());
        }

        public void setPlayer(Player player) {
            this.setPlayerUUID(player.getUUID());
        }

        public void playSound(SoundEvent sound) {
            this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
        }

        @Override
        public boolean hasRow() {
            return this.isLocal() && this.readyToSync && this.getRow() != null;
        }

        @Override
        public boolean claim(Player player) {
            if (Recordable.super.claim(player)) {
                this.getData().addTo(player, this.getDatabaseID());
                this.readyToSync = true;
            }
            return this.isLocal();
        }

        public boolean is(ICondition condition) {
            return condition.isTrue(this);
        }

        public BlockState getExternalBlockState() {
            return Overrides.get(this.getInternalBlockState().getBlock()).defaultBlockState();
        }

        public boolean is(Tag<Block> tag) {
            return this.getExternalBlockState().is(tag);
        }

        public float getAgeInYears() {
            return this.getBaseAge() + this.getAge();
        }

        public boolean isTimeBetween(int start, int end) {
            long time = this.level.getDayTime();
            return start <= time && time <= end;
        }

        @Override
        public Level getWorld() {
            return this.level;
        }

    public static class Overrides {
        protected static HashMap<Block, SoundEvent> STEP_SOUNDS = new HashMap<>();
        protected static HashMap<Block, Property<?>> PROPS = new HashMap<>();
        protected static HashMap<Block, Block> ALIASES = new HashMap<>();

        public static ResourceLocation getNameOf(BlockState state) {
            return Overrides.getNameOf(state, null);
        }

        public static ResourceLocation getNameOf(BlockState state, String suffix) {
            Block block = state.getBlock();
            String key = Overrides.get(block).getRegistryName().toString().replace(':', '/');
            if (PROPS.containsKey(block)) { key += String.format(".%s", state.getValue(PROPS.get(block))); }
            if (BlockPartyTags.FESTIVES.contains(block) && BlockParty.isChristmas()) { suffix = "christmas"; }
            if (suffix != null) { key += String.format(".%s", suffix); }
            return new ResourceLocation(BlockParty.ID, String.format("textures/entity/partyer/skins/%s.png", key));
        }

        public static Block get(Block block) {
            return ALIASES.getOrDefault(block, block);
        }

        public static void registerAliases() {
            rename(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD);
            rename(Blocks.ACACIA_PLANKS, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_SLAB, Blocks.ACACIA_STAIRS);
            rename(Blocks.ANDESITE, Blocks.ANDESITE_SLAB, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE_WALL, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_STAIRS);
            rename(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD);
            rename(Blocks.BIRCH_PLANKS, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_SLAB, Blocks.BIRCH_STAIRS);
            rename(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
            rename(Blocks.BLACKSTONE, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_WALL);
            rename(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
            rename(Blocks.BRICKS, Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS, Blocks.BRICK_WALL);
            rename(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
            rename(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
            rename(Blocks.COBBLESTONE, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_WALL, Blocks.INFESTED_COBBLESTONE);
            rename(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
            rename(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_STAIRS);
            rename(Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE);
            rename(Blocks.CUT_SANDSTONE, Blocks.CUT_SANDSTONE_SLAB);
            rename(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
            rename(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD);
            rename(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS);
            rename(Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_STAIRS);
            rename(Blocks.DIORITE, Blocks.DIORITE_SLAB, Blocks.DIORITE_STAIRS, Blocks.DIORITE_WALL, Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE_STAIRS);
            rename(Blocks.DIRT, Blocks.COARSE_DIRT);
            rename(Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_WALL);
            rename(Blocks.GLASS, Blocks.GLASS_PANE);
            rename(Blocks.GRANITE, Blocks.GRANITE_SLAB, Blocks.GRANITE_STAIRS, Blocks.GRANITE_WALL, Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE_STAIRS);
            rename(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
            rename(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
            rename(Blocks.IRON_BLOCK, Blocks.IRON_BARS);
            rename(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD);
            rename(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_STAIRS);
            rename(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
            rename(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
            rename(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
            rename(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
            rename(Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_WALL);
            rename(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_WALL);
            rename(Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_WALL);
            rename(Blocks.OAK_LOG, Blocks.OAK_WOOD);
            rename(Blocks.OAK_PLANKS, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_SLAB, Blocks.OAK_STAIRS);
            rename(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
            rename(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
            rename(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            rename(Blocks.PRISMARINE, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE_WALL);
            rename(Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICK_STAIRS);
            rename(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
            rename(Blocks.PURPUR_BLOCK, Blocks.PURPUR_SLAB, Blocks.PURPUR_STAIRS);
            rename(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_SLAB, Blocks.QUARTZ_STAIRS);
            rename(Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_WALL);
            rename(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_WALL);
            rename(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
            rename(Blocks.SANDSTONE, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE_WALL);
            rename(Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_STAIRS);
            rename(Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
            rename(Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_STAIRS);
            rename(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
            rename(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD);
            rename(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_STAIRS);
            rename(Blocks.STONE, Blocks.INFESTED_STONE, Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_STAIRS);
            rename(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_WALL);
            rename(Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD);
            rename(Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD);
            rename(Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE);
            rename(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD);
            rename(Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD);
            rename(Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD);
            rename(Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD);
            rename(Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE);
            rename(Blocks.WARPED_PLANKS, Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_SLAB, Blocks.WARPED_STAIRS);
            rename(Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE);
            rename(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
            rename(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        }

        public static void rename(Block main, Block... aliases) {
            for (Block alias : aliases) {
                ALIASES.put(alias, main);
            }
        }

        public static void registerPropertyOverrides() {
            registerProperty(Blocks.CAKE, CakeBlock.BITES);
            registerProperty(Blocks.NOTE_BLOCK, NoteBlock.NOTE);
        }

        private static void registerProperty(Block block, Property<?> property) {
            PROPS.put(block, property);
        }

        public static void registerStepSounds() {
            registerStepSound(Blocks.BELL, BlockPartySounds.ENTITY_MOE_BELL_STEP.get());
        }

        private static void registerStepSound(Block block, SoundEvent sound) {
            STEP_SOUNDS.put(block, sound);
        }

        public static SoundEvent getStepSound(BlockState block) {
            return STEP_SOUNDS.getOrDefault(block.getBlock(), block.getSoundType().getStepSound());
        }

        public static void registerSpecialRenderers() {
            MoeSpecialRenderer.registerOverlay(Blocks.BARREL, BarrelOverlay::new);
        }
    }
}
