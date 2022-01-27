package block_party.npc;

import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.client.animation.AbstractAnimation;
import block_party.client.animation.Animation;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.Recordable;
import block_party.db.records.NPC;
import block_party.messages.SOpenDialogue;
import block_party.scene.*;
import block_party.scene.filters.BloodType;
import block_party.scene.filters.Dere;
import block_party.scene.filters.Emotion;
import block_party.scene.filters.Gender;
import block_party.registry.CustomEntities;
import block_party.registry.CustomMessenger;
import block_party.registry.CustomSounds;
import block_party.registry.CustomTags;
import block_party.registry.resources.BlockAliases;
import block_party.registry.resources.DollSounds;
import block_party.utils.NBT;
import block_party.utils.Trans;
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
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockPartyNPC extends PathfinderMob implements ContainerListener, Recordable<NPC>, MenuProvider {
    public static final EntityDataAccessor<Optional<BlockState>> BLOCK_STATE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> FOLLOWING = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> PLAYER_UUID = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DATABASE_ID = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BLOOD_TYPE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DERE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> EMOTION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GENDER = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GIVEN_NAME = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> FOOD_LEVEL = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> EXHAUSTION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SATURATION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> STRESS = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> RELAXATION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LOYALTY = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AFFECTION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SLOUCH = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AGE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public final SimpleContainer inventory = new SimpleContainer(36);
    public final SceneManager sceneManager;
    private final LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
    private final Queue<Consumer<BlockPartyNPC>> nextTickOps;
    private BlockState actualBlockState = Blocks.AIR.defaultBlockState();
    private CompoundTag tileEntityData = new CompoundTag();
    private AbstractAnimation animation = Animation.DEFAULT.get();
    private Dialogue dialogue;
    private int timeUntilHungry;
    private int timeUntilLonely;
    private int timeUntilStress;
    private int timeSinceSleep;
    private long lastSeen;
    private boolean readyToSync;

    public BlockPartyNPC(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, 0.0F);
        this.restrictTo(this.blockPosition(), 16);
        this.nextTickOps = new LinkedList<>();
        this.sceneManager = new SceneManager(this);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.of(Blocks.AIR.defaultBlockState()));
        this.entityData.define(SCALE, 1.0F);
        this.entityData.define(FOLLOWING, false);
        this.entityData.define(PLAYER_UUID, "00000000-0000-0000-0000-000000000000");
        this.entityData.define(DATABASE_ID, "-1");
        this.entityData.define(BLOOD_TYPE, BloodType.O.getValue());
        this.entityData.define(DERE, Dere.NYANDERE.getValue());
        this.entityData.define(EMOTION, Emotion.NORMAL.getValue());
        this.entityData.define(GENDER, Gender.FEMALE.getValue());
        this.entityData.define(ANIMATION, Animation.DEFAULT.name());
        this.entityData.define(GIVEN_NAME, "Tokumei");
        this.entityData.define(SLOUCH, 0.0F);
        this.entityData.define(FOOD_LEVEL, 20.0F);
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
    public SoundEvent getAmbientSound() {
        if (this.isBlock(CustomTags.HAS_CAT_FEATURES)) { return DollSounds.get(this, DollSounds.Sound.MEOW); }
        return super.getAmbientSound();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("BlockState", Block.getId(this.getActualBlockState()));
        compound.putFloat("Scale", this.getScale());
        compound.put("TileEntity", this.getTileEntityData());
        compound.putLong("DatabaseID", this.getDatabaseID());
        compound.putBoolean("Following", this.isFollowing());
        compound.put("Inventory", this.inventory.createTag());
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLonely", this.timeUntilLonely);
        compound.putInt("TimeUntilStress", this.timeUntilStress);
        compound.putInt("TimeSinceSleep", this.timeSinceSleep);
        this.sceneManager.write(compound);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setBlockState(Block.stateById(compound.getInt("BlockState")));
        this.setScale(compound.getFloat("Scale"));
        this.setTileEntityData(compound.getCompound("TileEntity"));
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setFollowing(compound.getBoolean("Following"));
        this.inventory.fromTag(compound.getList("Inventory", NBT.COMPOUND));
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        this.sceneManager.read(compound);
        this.getRow().load(this);
        super.readAdditionalSaveData(compound);
        this.readyToSync = true;
    }

    @Override
    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, Optional.of(BlockAliases.get(state)));
        this.actualBlockState = state;
        if (this.isLocal()) {
            this.setScale(state.is(CustomTags.IGNORES_VOLUME) ? 1.0F : this.getBlockVolume(state));
            this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setCanFly(state.is(CustomTags.HAS_WINGS));
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

    @Override
    public boolean fireImmune() {
        return !this.getActualBlockState().isFlammable(this.level, this.blockPosition(), this.getDirection());
    }

    public Component getTypeName() {
        return new TranslatableComponent("entity.block_party.profession", this.getGivenName(), this.getBlockName());
    }

    public String getBlockName() {
        ResourceLocation block = this.getBlock().getRegistryName();
        return Trans.late(String.format("entity.block_party.%s.%s", block.getNamespace(), block.getPath()));
    }

    public Block getBlock() {
        return this.getVisibleBlockState().getBlock();
    }

    public BlockState getVisibleBlockState() {
        return this.entityData.get(BLOCK_STATE).orElse(this.getActualBlockState());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source == DamageSource.DROWN;
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        this.setUUID(UUID.randomUUID());
    }

    public Component getCustomName() {
        return new TranslatableComponent("entity.block_party.generic", this.getGivenName(), this.getHonorific());
    }

    public String getHonorific() {
        return this.getGender().getHonorific();
    }

    public Gender getGender() {
        if (this.isBlock(CustomTags.HAS_MALE_PRONOUNS)) {
            return Gender.MALE;
        } else if (this.isBlock(CustomTags.HAS_NONBINARY_PRONOUNS)) {
            return Gender.NONBINARY;
        } else {
            return Gender.FEMALE;
        }
    }

    public boolean isBlock(Tag<Block> tag) {
        try {
            return this.getActualBlockState().is(tag);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public boolean hasCustomName() { return true; }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vector, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) { return InteractionResult.PASS; }
        if (this.isPlayer(player)) {
            this.sceneManager.trigger(player.isCrouching() ? SceneTrigger.SHIFT_RIGHT_CLICK : SceneTrigger.RIGHT_CLICK);
        }
        return InteractionResult.SUCCESS;
    }

    public boolean isPlayer(Entity entity) {
        return entity != null && this.getPlayerUUID().equals(entity.getUUID());
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.entityData.get(PLAYER_UUID));
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.entityData.set(PLAYER_UUID, playerUUID.toString());
    }

    public String getGivenName() {
        return this.entityData.get(GIVEN_NAME);
    }

    public void setGivenName(String name) {
        this.entityData.set(GIVEN_NAME, name);
    }

    public void setCanFly(boolean fly) {
        this.moveControl = fly ? new FlyingMoveControl(this, 10, false) : new MoveControl(this);
        this.navigation = fly ? new FlyingPathNavigation(this, this.level) : new GroundPathNavigation(this, this.level);
        if (this.navigation instanceof GroundPathNavigation) {
            GroundPathNavigation ground = (GroundPathNavigation) this.navigation;
            ground.setCanOpenDoors(true);
        }
    }

    public boolean isLocal() {
        return !this.isRemote();
    }

    public boolean isRemote() {
        return this.level.isClientSide();
    }

    @Override
    protected void dropFromLootTable(DamageSource cause, boolean player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (stack.isEmpty()) { continue; }
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.sceneManager.tick(this);
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
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

    @Override
    public void customServerAiStep() {
        Consumer<BlockPartyNPC> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
        if (this.random.nextInt(20) == 0) { this.sceneManager.trigger(SceneTrigger.RANDOM_TICK); }
        if (this.isBeingLookedAt()) { this.sceneManager.trigger(SceneTrigger.STARE); }
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
    public boolean doHurtTarget(Entity target) {
        boolean attacked = super.doHurtTarget(target);
        if (attacked) {
            this.playSound(DollSounds.get(this, DollSounds.Sound.ATTACK));
            this.sceneManager.trigger(SceneTrigger.ATTACK);
        }
        return attacked;
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

    public BlockState getActualBlockState() {
        return this.actualBlockState;
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.entityData.set(FOLLOWING, following);
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    @Override
    public boolean claim(Player player) {
        if (Recordable.super.claim(player)) {
            this.getData().addTo(player, this.getDatabaseID());
            this.readyToSync = true;
        }
        return this.isLocal();
    }

    @Override
    public boolean hasRow() {
        return this.isLocal() && this.readyToSync && this.getRow() != null;
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this);
    }

    @Override
    public Level getWorld() {
        return this.level;
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.level.dimension(), this.blockPosition());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity entity = source.getDirectEntity();
        if (this.isPlayer(entity)) {
            this.sceneManager.trigger(entity.isCrouching() ? SceneTrigger.SHIFT_LEFT_CLICK : SceneTrigger.LEFT_CLICK);
            return false;
        } else if (super.hurt(source, amount * this.getBlockBuffer())) {
            this.sceneManager.trigger(SceneTrigger.HURT);
            return true;
        } else {
            return false;
        }
    }

    private float getBlockBuffer() {
        return 0.5F / (this.getActualBlockState().getDestroySpeed(this.level, this.blockPosition()) + 1);
    }

    @Override
    protected void dropEquipment() {
        for (int slot = 0; slot < 36; ++slot) {
            ItemStack stack = this.inventory.getItem(slot);
            if (stack.isEmpty()) { continue; }
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource cause) {
        return DollSounds.get(this, DollSounds.Sound.HURT);
    }

    @Override
    public SoundEvent getDeathSound() {
        return DollSounds.get(this, DollSounds.Sound.DEAD);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(DollSounds.get(this, DollSounds.Sound.STEP), 0.15F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, block);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x3, id, inventory, this.inventory, 3);
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

    public boolean is(ITrait condition) {
        return condition.isSharedWith(this);
    }    public void jumpFromGround() {
        this.playSound(DollSounds.get(this, DollSounds.Sound.ATTACK));
        super.jumpFromGround();
    }

    public boolean is(Tag<Block> tag) {
        return this.getVisibleBlockState().is(tag);
    }

    public void setDialogue(Dialogue dialogue) {
        if (dialogue == null) { return; }
        Dialogue.Model message = new Dialogue.Model(dialogue.getText(), dialogue.isTooltip(), dialogue.getSpeaker(), DollSounds.get(this, DollSounds.Sound.SAY));
        for (Response.Icon icon : dialogue.responses.keySet()) {
            message.add(icon, dialogue.responses.get(icon).getText());
        }
        CustomMessenger.send(this.getPlayer(), new SOpenDialogue(this.getRow(), message));
        this.dialogue = dialogue;
    }
    
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SCALE.equals(key)) { this.refreshDimensions(); }
        if (ANIMATION.equals(key)) { this.setAnimation(Animation.DEFAULT.fromValue(this.entityData.get(ANIMATION))); }
        super.onSyncedDataUpdated(key);
        if (this.hasRow()) { this.getRow().update(this); }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.908203125F * this.getScale();
    }

    public Player getPlayer() {
        return this.level.getPlayerByUUID(this.getPlayerUUID());
    }

    public void ifPlayer(Consumer<Player> function) {
        Player player = this.getPlayer();
        if (player != null) { function.accept(player); }
    }

    public ServerPlayer getServerPlayer() {
        return this.getServer().getPlayerList().getPlayer(this.getPlayerUUID());
    }

    public void ifServerPlayer(Consumer<ServerPlayer> function) {
        ServerPlayer player = this.getServerPlayer();
        if (player != null) { function.accept(player); }
    }

    public void setPlayer(Player player) {
        this.setPlayerUUID(player.getUUID());
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) { return this.itemHandler.cast(); }
        return super.getCapability(capability, facing);
    }

    public void setResponse(Response.Icon response) {
        if (this.dialogue != null) { this.dialogue.setResponse(response); }
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandler.invalidate();
    }

    public boolean isSitting() {
        return false;
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
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

    private float[] getRGB(int hex) {
        float r = ((hex & 0xff0000) >> 16) / 255.0F;
        float g = ((hex & 0xff00) >> 8) / 255.0F;
        float b = ((hex & 0xff) >> 1) / 255.0F;
        return new float[] { r, g, b };
    }

    private int[] getAuraColor() {
        MaterialColor block = this.getActualBlockState().getMaterial().getColor();
        return new int[] { block.col, 0xffffff };
    }

    public boolean isBlockGlowing() {
        return this.isBlock(CustomTags.HAS_GLOW);
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void addLastSeen(long lastSeen) {
        this.setLastSeen(this.lastSeen + lastSeen);
    }

    public float getSlouch() {
        return this.entityData.get(SLOUCH);
    }

    public void setSlouch(float slouch) {
        this.entityData.set(SLOUCH, slouch);
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
        return Emotion.NORMAL.fromValue(this.entityData.get(EMOTION));
    }

    public void setEmotion(Emotion emotion) {
        this.entityData.set(EMOTION, emotion.getValue());
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

    public boolean isBeingLookedAt() {
        if (!this.isPlayerOnline()) { return false; }
        Player player = this.getPlayer();
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 dist = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double d0 = dist.length();
        dist = dist.normalize();
        double d1 = look.dot(dist);
        return d1 > 1.0D - 0.025D / d0 ? player.hasLineOfSight(this) : false;
    }

    public boolean isPlayerBusy() {
        return this.isPlayerOnline() && this.getPlayer().containerMenu != this.getPlayer().inventoryMenu;
    }

    public boolean isPlayerOnline() {
        return this.getPlayer() != null;
    }

    public void sayInChat(String key, Object... params) {
        this.level.players().forEach((player) -> {
            if (player.distanceTo(this) < 8.0D) { this.sayInChat(player, key, params); }
        });
    }

    public void sayInChat(Player player, String key, Object... params) {
        this.sayInChat(player, new TranslatableComponent(key, params));
    }

    public void sayInChat(Player player, Component component) {
        player.sendMessage(component, player.getUUID());
    }

    public BlockPartyNPC teleport(ServerLevel level, ITeleporter teleporter) {
        if (this.changeDimension(level, teleporter) instanceof BlockPartyNPC npc) { return this.onTeleport(npc); }
        return this;
    }

    public BlockPartyNPC onTeleport(BlockPartyNPC entity) {
        entity.setFollowing(true);
        entity.playSound(CustomSounds.NPC_FOLLOW.get());
        return entity;
    }

    public void addExhaustion(float exhaustion) {
        this.setExhaustion(this.getExhaustion() + exhaustion);
    }

    public float getExhaustion() {
        return this.entityData.get(EXHAUSTION);
    }

    public void setExhaustion(float exhaustion) {
        this.entityData.set(EXHAUSTION, exhaustion);
    }

    public void addSaturation(float saturation) {
        this.setSaturation(this.getSaturation() + saturation);
    }

    public float getSaturation() {
        return this.entityData.get(SATURATION);
    }

    public void setSaturation(float saturation) {
        this.entityData.set(SATURATION, saturation);
    }

    public void addFoodLevel(float food_level) {
        this.setFoodLevel(this.getFoodLevel() + food_level);
    }

    public float getFoodLevel() {
        return this.entityData.get(FOOD_LEVEL);
    }

    public void setFoodLevel(float food_level) {
        this.entityData.set(FOOD_LEVEL, food_level);
    }

    public void addAffection(float affection) {
        this.setAffection(this.getAffection() + affection);
    }

    public float getAffection() {
        return this.entityData.get(AFFECTION);
    }

    public void setAffection(float affection) {
        this.entityData.set(AFFECTION, affection);
    }

    public void addLoyalty(float loyalty) {
        this.setLoyalty(this.getLoyalty() + loyalty);
    }

    public float getLoyalty() {
        return this.entityData.get(LOYALTY);
    }

    public void setLoyalty(float loyalty) {
        this.entityData.set(LOYALTY, loyalty);
    }

    public boolean openChestFor(Player player) {
        player.openMenu(this);
        return true;
    }

    public void addNextTickOp(Consumer<BlockPartyNPC> op) {
        this.nextTickOps.add(op);
    }

    public void addStress(float stress) {
        this.setStress(this.getStress() + stress);
    }

    public float getStress() {
        return this.entityData.get(STRESS);
    }

    public void setStress(float stress) {
        this.entityData.set(STRESS, stress);
    }

    public void addRelaxation(float relaxation) {
        this.setRelaxation(this.getRelaxation() + relaxation);
    }

    public float getRelaxation() {
        return this.entityData.get(RELAXATION);
    }

    public void setRelaxation(float relaxation) {
        this.entityData.set(RELAXATION, relaxation);
    }

    public void addAge(float age) {
        this.setAge(this.getAge() + age);
    }

    public float getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(float age) {
        this.entityData.set(AGE, age);
    }

    public AbstractAnimation getAnimation() {
        return this.animation;
    }

    public void setAnimation(AbstractAnimation animation) {
        this.animation = animation;
    }

    public Animation getAnimationKey() {
        return Animation.DEFAULT.fromKey(this.entityData.get(ANIMATION));
    }

    public void setAnimationKey(Animation animation) {
        this.entityData.set(ANIMATION, animation.name());
    }

    public float getAgeInYears() {
        return this.getBaseAge() + this.getAge();
    }

    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 14;
    }

    public boolean isTimeBetween(int start, int end) {
        long time = this.level.getDayTime();
        return start <= time && time <= end;
    }

    public static boolean spawn(Level level, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, Player player) {
        BlockState state = level.getBlockState(block);
        if (!state.is(CustomTags.Blocks.SPAWNS_DOLLS)) { return false; }
        BlockEntity extra = level.getBlockEntity(block);
        BlockPartyNPC npc = CustomEntities.NPC.get().create(level);
        npc.absMoveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, yaw, pitch);
        /**
         * TODO: Need to move this to {@link ShimenawaBlockEntity#getNewRow()} and {@link CustomSpawnEggItem#useOn(UseOnContext)}
         */
        npc.setDatabaseID(block.asLong());
        npc.setBlockState(state);
        npc.setTileEntityData(extra != null ? extra.getTileData() : new CompoundTag());
        npc.setDere(dere);
        npc.claim(player);
        if (level.addFreshEntity(npc)) {
            npc.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(spawn), MobSpawnType.TRIGGERED, null, null);
            if (player != null) { npc.setPlayer(player); }
            return level.destroyBlock(block, false);
        }
        return false;
    }














}
