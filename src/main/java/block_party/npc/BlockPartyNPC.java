package block_party.npc;

import block_party.db.BlockPartyDB;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.client.animation.AbstractAnimation;
import block_party.client.animation.Animation;
import block_party.convo.Dialogue;
import block_party.convo.enums.Response;
import block_party.db.Recordable;
import block_party.db.records.NPC;
import block_party.custom.CustomEntities;
import block_party.custom.CustomMessenger;
import block_party.custom.CustomSounds;
import block_party.custom.CustomTags;
import block_party.messages.SOpenDialogue;
import block_party.npc.automata.*;
import block_party.npc.automata.trait.BloodType;
import block_party.npc.automata.trait.Dere;
import block_party.npc.automata.trait.Emotion;
import block_party.npc.automata.trait.Gender;
import block_party.db.DimBlockPos;
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

import java.util.*;
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
    public static final EntityDataAccessor<Float> FULLNESS = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> EXHAUSTION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SATURATION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> STRESS = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> RELAXATION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LOYALTY = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AFFECTION = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SLOUCH = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AGE = SynchedEntityData.defineId(BlockPartyNPC.class, EntityDataSerializers.FLOAT);
    public final SimpleContainer inventory = new SimpleContainer(36);
    public final Automaton automaton;
    private final LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
    private final Queue<Consumer<BlockPartyNPC>> nextTickOps;
    private CompoundTag tileEntityData = new CompoundTag();
    private AbstractAnimation animation;
    private int timeUntilHungry;
    private int timeUntilLonely;
    private int timeUntilStress;
    private int timeSinceSlept;
    private long lastSeen;
    private boolean readyToSync;

    public BlockPartyNPC(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
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
        this.entityData.define(DATABASE_ID, "-1");
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
        compound.putLong("DatabaseID", this.getDatabaseID());
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
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setFollowing(compound.getBoolean("Following"));
        this.inventory.fromTag(compound.getList("Inventory", NBT.COMPOUND));
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSlept = compound.getInt("TimeSinceSlept");
        this.automaton.read(compound);
        this.getRow().load(this);
        System.out.println(this.getPlayerUUID());
        super.readAdditionalSaveData(compound);
        this.readyToSync = true;
    }

    @Override
    public NPC getRow() {
        return BlockPartyDB.NPCs.find(this.getDatabaseID());
    }

    @Override
    public NPC getNewRow() {
        return new NPC(this);
    }

    @Override
    public boolean claim(Player player) {
        if (Recordable.super.claim(player)) {
            this.getData().addTo(player, this.getDatabaseID());
            this.readyToSync = true;
        }
        return this.isLocal();
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(BLOCK_STATE, Optional.of(state));
        if (this.isLocal()) {
            this.setScale(state.is(CustomTags.FULLSIZED) ? 1.0F : this.getBlockVolume(state));
            this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, this.fireImmune() ? 0.0F : -1.0F);
            this.setCanFly(state.is(CustomTags.WINGED));
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
        return !this.getInternalBlockState().isFlammable(this.level, this.blockPosition(), this.getDirection());
    }

    public Component getTypeName() {
        return new TranslatableComponent("entity.block_party.profession", this.getGivenName(), this.getBlockName());
    }

    public String getBlockName() {
        ResourceLocation block = Quirks.getRenderedBlock(this.getInternalBlockState().getBlock()).getRegistryName();
        return Trans.late(String.format("entity.block_party.%s.%s", block.getNamespace(), block.getPath()));
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
        String honorific = this.is(Gender.MASCULINE) ? "kun" : "chan";
        if (this.is(CustomTags.FULLSIZED)) {
            return honorific;
        } else if (this.is(CustomTags.BABY) || this.getScale() < 1.0F) {
            return "tan";
        }
        return honorific;
    }

    public boolean is(ICondition condition) {
        return condition.isTrue(this);
    }

    public boolean is(Tag<Block> tag) {
        return this.getExternalBlockState().is(tag);
    }

    public BlockState getExternalBlockState() {
        return Quirks.getRenderedBlock(this.getInternalBlockState().getBlock()).defaultBlockState();
    }

    @Override
    public boolean hasCustomName() { return true; }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vector, InteractionHand hand) {
        return this.onInteract(player, player.getItemInHand(hand), hand);
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount * this.getBlockBuffer());
    }

    private float getBlockBuffer() {
        return 0.5F / this.getInternalBlockState().getDestroySpeed(this.level, this.blockPosition());
    }

    @Override
    protected void dropEquipment() {
        for (int slot = 0; slot < 36; ++slot) {
            ItemStack stack = this.inventory.getItem(slot);
            if (stack.isEmpty()) { continue; }
            this.spawnAtLocation(stack);
        }
    }

    public BlockState getInternalBlockState() {
        return this.entityData.get(BLOCK_STATE).orElseGet(() -> Blocks.AIR.defaultBlockState());
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
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(Quirks.getStepSound(this.getInternalBlockState()), 0.15F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
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

    @Override
    public Level getWorld() {
        return this.level;
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.level.dimension(), this.blockPosition());
    }

    public void say(Player player, String line, Response... responses) {
        if (line.length() > 128) { throw new IllegalArgumentException("Lines can't be over 128 characters long."); }
        if (responses.length == 0) { responses = new Response[] { Response.CLOSE }; }
        CustomMessenger.send(player, new SOpenDialogue(new Dialogue(this.getRow(), line, responses)));
    }

    public Gender getGender() {
        return this.isBlock(CustomTags.MALE) ? Gender.MASCULINE : Gender.FEMININE;
    }

    public void setGender(Gender gender) {
        this.entityData.set(GENDER, gender.getValue());
    }

    public boolean isSitting() {
        return false;
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
    }

    public void onMention(Player player, String message) {
        this.playSound(CustomSounds.NPC_CONFUSED.get());
        this.jumpFromGround();
        this.setState(State.LOOK_AT_PLAYER);
    }

    public void jumpFromGround() {
        super.jumpFromGround();
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

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return 0.908203125F * this.getScale();
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

    public void setState(IState state, IState... states) {
        this.automaton.setState(this, state, states);
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public float getVoicePitch() {
        float hardness = (1.0F - this.getBlockBuffer()) * 0.25F;
        return super.getVoicePitch() + hardness + (1.0F - this.getScale());
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
        return this.isBlock(CustomTags.GLOWING);
    }

    public boolean isBlock(Tag<Block> tag) {
        try {
            return this.getInternalBlockState().is(tag);
        } catch (IllegalStateException e) {
            return false;
        }
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
        return Emotion.NORMAL.fromValue(this.entityData.get(GENDER));
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

    public boolean isPlayerBusy() {
        return this.isPlayerOnline() && this.getPlayer().containerMenu != this.getPlayer().inventoryMenu;
    }

    public boolean isPlayerOnline() {
        return this.getPlayer() != null;
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

    @Override
    public boolean hasRow() {
        return this.isLocal() && this.readyToSync && this.getRow() != null;
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

    public BlockPartyNPC teleport(ServerLevel level, ITeleporter teleporter) {
        if (this.changeDimension(level, teleporter) instanceof BlockPartyNPC npc)
            return this.onTeleport(npc);
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

    public void addFullness(float fullness) {
        this.setFullness(this.getFullness() + fullness);
    }

    public float getFullness() {
        return this.entityData.get(FULLNESS);
    }

    public void setFullness(float fullness) {
        this.entityData.set(FULLNESS, fullness);
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

    public float getAgeInYears() {
        return this.getBaseAge() + this.getAge();
    }

    public int getBaseAge() {
        return (int) (this.getScale() * 5) + 13;
    }

    public boolean isTimeBetween(int start, int end) {
        long time = this.level.getDayTime();
        return start <= time && time <= end;
    }

    public static boolean spawn(Level level, BlockPos block, BlockPos spawn, float yaw, float pitch, Dere dere, Player player) {
        BlockState state = level.getBlockState(block);
        if (!state.is(CustomTags.Blocks.NPC_SPAWN_BLOCKS)) { return false; }
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
