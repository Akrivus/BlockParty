package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.movement.MoeAnchorType;
import block_party.entities.movement.MoeAnchorResolver;
import block_party.entities.movement.FollowSession;
import block_party.entities.movement.PartyInvites;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.entities.social.MoeSocialRules;
import block_party.entities.social.MoeSocialContext;
import block_party.items.InviteItem;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.registry.resources.BlockAliasesReloadListener;
import block_party.registry.resources.MoeNamesReloadListener;
import block_party.registry.resources.MoeSounds;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.SceneManager;
import block_party.scene.SceneTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Moe extends PathfinderMob implements ContainerListener, MenuProvider {
    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";
    private static final int COMPATIBILITY_FOLLOW_TICKS = 20 * 60 * 5;
    private static final double IDLE_ANCHOR_RADIUS = 24.0D;
    private static final double IDLE_SOCIAL_RADIUS = 10.0D;

    public static final EntityDataAccessor<String> DATABASE_ID =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> OWNER_UUID =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<BlockState> BLOCK_STATE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<BlockState> VISIBLE_BLOCK_STATE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> FOLLOWING =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> GIVEN_NAME =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> GENDER =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> BLOOD_TYPE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> DERE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ZODIAC =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> EMOTION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Float> FOOD_LEVEL =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> EXHAUSTION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SATURATION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> STRESS =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> RELAXATION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LOYALTY =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AFFECTION =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> SLOUCH =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> AGE =
            SynchedEntityData.defineId(Moe.class, EntityDataSerializers.FLOAT);

    private CompoundTag tileEntityData = new CompoundTag();
    private final SimpleContainer inventory = new SimpleContainer(36);
    private boolean hasHome;
    private DimBlockPos home = new DimBlockPos();
    private long lastSeen;
    private int timeUntilHungry;
    private int timeUntilLonely;
    private int timeUntilStress;
    private int timeSinceSleep;
    private int socialTickDelay;
    private Dialogue dialogue;
    private Response response;
    private boolean guiPreview;
    private UUID dialogueTarget = new UUID(0L, 0L);
    private FollowSession followSession = FollowSession.none();
    private RoutineIntent routineIntent = RoutineIntent.IDLE;
    private final SceneManager sceneManager = new SceneManager(this);

    public Moe(EntityType<? extends Moe> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 0.0F);
        this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathfindingMalus(PathType.TRAPDOOR, 0.0F);
        this.restrictTo(this.blockPosition(), 16);
        this.home = this.getDimBlockPos();
        this.inventory.addListener(this);
        this.setBloodType(this.weightedBloodType());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATABASE_ID, "-1");
        builder.define(OWNER_UUID, EMPTY_UUID);
        builder.define(BLOCK_STATE, Blocks.AIR.defaultBlockState());
        builder.define(VISIBLE_BLOCK_STATE, Blocks.AIR.defaultBlockState());
        builder.define(SCALE, 1.0F);
        builder.define(FOLLOWING, false);
        builder.define(SITTING, false);
        builder.define(GIVEN_NAME, "Tokumei");
        builder.define(GENDER, "FEMALE");
        builder.define(BLOOD_TYPE, "O");
        builder.define(DERE, "NYANDERE");
        builder.define(ZODIAC, "ARIES");
        builder.define(EMOTION, "NORMAL");
        builder.define(ANIMATION, "DEFAULT");
        builder.define(FOOD_LEVEL, 20.0F);
        builder.define(EXHAUSTION, 0.0F);
        builder.define(SATURATION, 6.0F);
        builder.define(STRESS, 0.0F);
        builder.define(RELAXATION, 0.0F);
        builder.define(LOYALTY, 6.0F);
        builder.define(AFFECTION, 0.0F);
        builder.define(SLOUCH, 0.0F);
        builder.define(AGE, 0.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setDatabaseID(compound.getLong("DatabaseID"));
        this.setBlockState(Block.stateById(compound.getInt("BlockState")), false);
        this.setVisibleBlockState(compound.contains("VisibleBlockState")
                ? Block.stateById(compound.getInt("VisibleBlockState"))
                : this.getBlockState());
        this.setMoeScale(compound.contains("Scale") ? compound.getFloat("Scale") : 1.0F);
        this.setSitting(compound.getBoolean("Sitting"));
        if (compound.contains("OwnerUUID")) {
            this.setPlayerUUID(UUID.fromString(compound.getString("OwnerUUID")));
        }
        if (compound.contains("PlayerUUID")) {
            this.setPlayerUUID(UUID.fromString(compound.getString("PlayerUUID")));
        }
        if (compound.contains("FollowSession")) {
            this.readFollowSession(compound.getCompound("FollowSession"));
        } else {
            this.setFollowing(compound.getBoolean("Following"));
        }
        if (compound.contains("GivenName")) {
            this.setGivenName(compound.getString("GivenName"));
        }
        if (compound.contains("Gender")) {
            this.setGender(compound.getString("Gender"));
        }
        if (compound.contains("BloodType")) {
            this.setBloodType(compound.getString("BloodType"));
        }
        if (compound.contains("Dere")) {
            this.setDere(compound.getString("Dere"));
        }
        if (compound.contains("Zodiac")) {
            this.setZodiac(compound.getString("Zodiac"));
        }
        if (compound.contains("Emotion")) {
            this.setEmotion(compound.getString("Emotion"));
        }
        if (compound.contains("Animation")) {
            this.setAnimationKey(compound.getString("Animation"));
        }
        if (compound.contains("FoodLevel")) {
            this.setFoodLevel(compound.getFloat("FoodLevel"));
        }
        if (compound.contains("Exhaustion")) {
            this.setExhaustion(compound.getFloat("Exhaustion"));
        }
        if (compound.contains("Saturation")) {
            this.setSaturation(compound.getFloat("Saturation"));
        }
        if (compound.contains("Stress")) {
            this.setStress(compound.getFloat("Stress"));
        }
        if (compound.contains("Relaxation")) {
            this.setRelaxation(compound.getFloat("Relaxation"));
        }
        if (compound.contains("Loyalty")) {
            this.setLoyalty(compound.getFloat("Loyalty"));
        }
        if (compound.contains("Affection")) {
            this.setAffection(compound.getFloat("Affection"));
        }
        if (compound.contains("Age")) {
            this.setAge(compound.getFloat("Age"));
        }
        this.setHasHome(compound.getBoolean("HasHome"));
        if (compound.contains("Home")) {
            this.setHome(new DimBlockPos(compound.getCompound("Home")));
        }
        if (compound.contains("RoutineIntent")) {
            this.setRoutineIntent(RoutineIntent.fromValue(compound.getString("RoutineIntent")));
        }
        if (compound.contains("LastSeenAt")) {
            this.setLastSeen(compound.getLong("LastSeenAt"));
        }
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        this.inventory.fromTag(compound.getList("Inventory", 10), this.registryAccess());
        if (compound.contains("Slouch")) {
            this.setSlouch(compound.getFloat("Slouch"));
        }
        this.setTileEntityData(compound.getCompound("TileEntity"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("DatabaseID", this.getDatabaseID());
        compound.putInt("BlockState", Block.getId(this.getBlockState()));
        compound.putInt("VisibleBlockState", Block.getId(this.getVisibleBlockState()));
        compound.putFloat("Scale", this.getMoeScale());
        compound.putBoolean("Following", this.isFollowing());
        if (this.isFollowing()) {
            compound.put("FollowSession", this.writeFollowSession());
        }
        compound.putBoolean("Sitting", this.isSitting());
        compound.putString("PlayerUUID", this.getPlayerUUID().toString());
        compound.putString("OwnerUUID", this.getPlayerUUID().toString());
        compound.putString("GivenName", this.getGivenName());
        compound.putString("Gender", this.getGender());
        compound.putString("BloodType", this.getBloodType());
        compound.putString("Dere", this.getDere());
        compound.putString("Zodiac", this.getZodiac());
        compound.putString("Emotion", this.getEmotion());
        compound.putString("Animation", this.getAnimationKey());
        compound.putFloat("FoodLevel", this.getFoodLevel());
        compound.putFloat("Exhaustion", this.getExhaustion());
        compound.putFloat("Saturation", this.getSaturation());
        compound.putFloat("Stress", this.getStress());
        compound.putFloat("Relaxation", this.getRelaxation());
        compound.putFloat("Loyalty", this.getLoyalty());
        compound.putFloat("Affection", this.getAffection());
        compound.putFloat("Slouch", this.getSlouch());
        compound.putFloat("Age", this.getAge());
        compound.putBoolean("HasHome", this.hasHome());
        compound.put("Home", this.getHome().write());
        compound.putString("RoutineIntent", this.getRoutineIntent().name());
        compound.putLong("LastSeenAt", this.getLastSeen());
        compound.putInt("TimeUntilHungry", this.getTimeUntilHungry());
        compound.putInt("TimeUntilLonely", this.getTimeUntilLonely());
        compound.putInt("TimeUntilStress", this.getTimeUntilStress());
        compound.putInt("TimeSinceSleep", this.getTimeSinceSleep());
        compound.put("Inventory", this.inventory.createTag(this.registryAccess()));
        compound.put("TileEntity", this.getTileEntityData().copy());
    }

    private void readFollowSession(CompoundTag compound) {
        UUID playerUuid = compound.contains("PlayerUUID") ? UUID.fromString(compound.getString("PlayerUUID")) : this.getPlayerUUID();
        PlayerMovementIntent intent = PlayerMovementIntent.FOLLOW_REQUEST;
        if (compound.contains("Intent")) {
            try {
                intent = PlayerMovementIntent.valueOf(compound.getString("Intent"));
            } catch (IllegalArgumentException ignored) {
                intent = PlayerMovementIntent.FOLLOW_REQUEST;
            }
        }
        this.startFollowSession(
                playerUuid,
                intent,
                compound.getInt("TicksRemaining"),
                compound.getBoolean("CanChangeDimension"));
    }

    private CompoundTag writeFollowSession() {
        CompoundTag compound = new CompoundTag();
        compound.putString("PlayerUUID", this.followSession.playerUuid().toString());
        compound.putString("Intent", this.followSession.intent().name());
        compound.putInt("TicksRemaining", this.followSession.ticksRemaining());
        compound.putBoolean("CanChangeDimension", this.followSession.canChangeDimension());
        return compound;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.sceneManager.tick();
            this.tickFollowSession();
            this.updateHungerState();
            this.updateLonelyState();
            this.updateStressState();
            this.updateActionState();
            this.updateSleepState();
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        return this.hasCatFeatures() ? MoeSounds.get(this, MoeSounds.Sound.MEOW) : super.getAmbientSound();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return MoeSounds.get(this, MoeSounds.Sound.HURT);
    }

    @Override
    public SoundEvent getDeathSound() {
        return MoeSounds.get(this, MoeSounds.Sound.DEAD);
    }

    public SoundEvent getAttackSound() {
        return MoeSounds.get(this, MoeSounds.Sound.ATTACK);
    }

    public SoundEvent getStepSound() {
        return MoeSounds.get(this, MoeSounds.Sound.STEP);
    }

    public SoundEvent getSpeakSound() {
        return MoeSounds.get(this, MoeSounds.Sound.SAY);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        super.playStepSound(pos, state);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return this.handleOwnerInteract(player, hand);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vector, InteractionHand hand) {
        return this.handleOwnerInteract(player, hand);
    }

    private InteractionResult handleOwnerInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            if (player.getItemInHand(hand).getItem() instanceof InviteItem) {
                return PartyInvites.request(player, this);
            }
            if (this.canDialogueWith(player)) {
                this.setDialogueTarget(player.getUUID());
                this.triggerScene(player.isShiftKeyDown() ? SceneTrigger.SHIFT_RIGHT_CLICK : SceneTrigger.RIGHT_CLICK);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        if (attacker instanceof Player player && !this.level().isClientSide && this.canDialogueWith(player, false)) {
            this.setDialogueTarget(player.getUUID());
            this.triggerScene(player.isShiftKeyDown() ? SceneTrigger.SHIFT_LEFT_CLICK : SceneTrigger.LEFT_CLICK);
            return true;
        }
        return super.skipAttackInteraction(attacker);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        Entity attacker = damageSource.getDirectEntity();
        if (attacker instanceof Player player && this.canDialogueWith(player, false)) {
            this.setDialogueTarget(player.getUUID());
            this.triggerScene(player.isShiftKeyDown() ? SceneTrigger.SHIFT_LEFT_CLICK : SceneTrigger.LEFT_CLICK);
            return false;
        }
        boolean hurt = super.hurtServer(level, damageSource, amount * this.getBlockBuffer());
        if (hurt) {
            this.syncHealthToDb(level);
            this.triggerScene(SceneTrigger.HURT);
        }
        return hurt;
    }

    @Override
    public void heal(float amount) {
        float before = this.getHealth();
        super.heal(amount);
        if (this.getHealth() != before && this.level() instanceof ServerLevel level) {
            this.syncHealthToDb(level);
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (!this.level().isClientSide && this.isCorporeal()) {
            this.hide(HideUntil.EXPOSED);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean attacked = super.doHurtTarget(level, target);
        if (attacked) {
            this.triggerScene(SceneTrigger.ATTACK);
            this.playSound(this.getAttackSound(), this.getSoundVolume(), this.getVoicePitch());
        }
        return attacked;
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        this.triggerServerSceneHooks(this.random.nextInt(20) == 0, this.isBeingLookedAt());
    }

    public void triggerServerSceneHooks(boolean randomTick, boolean beingLookedAt) {
        if (randomTick) {
            this.triggerScene(SceneTrigger.RANDOM_TICK);
        }
        if (beingLookedAt) {
            this.triggerScene(SceneTrigger.STARE);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return 0.0F;
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        for (int slot = 0; slot < this.inventory.getContainerSize(); ++slot) {
            ItemStack stack = this.inventory.getItem(slot);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(level, stack);
            }
        }
    }

    @Override
    public void containerChanged(net.minecraft.world.Container inventory) {
        this.setSlouch(this.recalcSlouch());
    }

    private void syncHealthToDb(ServerLevel level) {
        long id = this.getDatabaseID();
        if (id < 0L) {
            return;
        }
        try {
            NPC.updateHealth(BlockPartyDB.get(level), id, this.getHealth());
        } catch (SQLException ignored) {
        }
    }

    private void syncFoodToDb() {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        long id = this.getDatabaseID();
        if (id < 0L) {
            return;
        }
        try {
            NPC.updateFood(BlockPartyDB.get(level), id, this.getFoodLevel(), this.getExhaustion(), this.getSaturation());
        } catch (SQLException ignored) {
        }
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return ChestMenu.threeRows(id, inventory, this.inventory);
    }

    public boolean openChestFor(Player player) {
        player.openMenu(this);
        return true;
    }

    public boolean openSpecialMenuFor(Player player) {
        return false;
    }

    public boolean isBeingLookedThrough() {
        if (!this.isPlayerBusy()) {
            return false;
        }
        return this.getPlayer().containerMenu instanceof ChestMenu menu && menu.getContainer().equals(this.inventory);
    }

    public float recalcSlouch() {
        float size = 0.0F;
        for (int slot = 0; slot < this.inventory.getContainerSize(); ++slot) {
            if (!this.inventory.getItem(slot).isEmpty()) {
                size += 0.0277777778F;
            }
        }
        return size;
    }

    public double movementSpeedAttribute() {
        return this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public long getDatabaseID() {
        return Long.parseLong(this.entityData.get(DATABASE_ID));
    }

    public void setDatabaseID(long id) {
        this.entityData.set(DATABASE_ID, Long.toString(id));
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.entityData.get(OWNER_UUID));
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.entityData.set(OWNER_UUID, playerUUID.toString());
    }

    @Deprecated
    public UUID getOwnerUUID() {
        return this.getPlayerUUID();
    }

    @Deprecated
    public void setOwnerUUID(UUID ownerUUID) {
        this.setPlayerUUID(ownerUUID);
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public BlockState getActualBlockState() {
        return this.getBlockState();
    }

    public void setBlockState(BlockState state) {
        this.setBlockState(state, true);
    }

    public void setBlockStateFromRow(BlockState state) {
        this.setBlockState(state, false);
    }

    private void setBlockState(BlockState state, boolean applyProfile) {
        BlockState safe = state == null ? Blocks.AIR.defaultBlockState() : state;
        this.entityData.set(BLOCK_STATE, safe);
        this.entityData.set(VISIBLE_BLOCK_STATE, BlockAliasesReloadListener.resolve(safe));
        if (applyProfile) {
            this.applyBlockProfileFromState(safe);
            this.applyBlockPhysicalState(safe);
        }
    }

    public BlockState getVisibleBlockState() {
        return this.entityData.get(VISIBLE_BLOCK_STATE);
    }

    public void setVisibleBlockState(BlockState state) {
        this.entityData.set(VISIBLE_BLOCK_STATE, state == null ? this.getBlockState() : state);
    }

    public float getMoeScale() {
        return this.entityData.get(SCALE);
    }

    public void setMoeScale(float scale) {
        this.entityData.set(SCALE, scale);
    }

    public boolean isCorporeal() {
        return !this.isCardinal();
    }

    public boolean isCardinal() {
        return this.getVisibleBlockState().is(CustomTags.CARDINAL);
    }

    public void setCorporeal(boolean corporeal) {
        // Corporeality is now derived from the visible block's cardinal trait tag.
    }

    public void setIsCorporeal(boolean corporeal) {
        this.setCorporeal(corporeal);
    }

    public boolean isFollowing() {
        return this.entityData.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        if (following) {
            UUID playerUuid = this.dialogueTarget.equals(new UUID(0L, 0L)) ? this.getPlayerUUID() : this.dialogueTarget;
            this.startFollowSession(playerUuid, PlayerMovementIntent.FOLLOW_REQUEST, COMPATIBILITY_FOLLOW_TICKS, false);
        } else {
            this.clearFollowSession();
        }
    }

    public FollowSession getFollowSession() {
        return this.followSession;
    }

    public UUID getFollowPlayerUUID() {
        return this.followSession.playerUuid();
    }

    public PlayerMovementIntent getFollowIntent() {
        return this.followSession.intent();
    }

    public int getFollowTicksRemaining() {
        return this.followSession.ticksRemaining();
    }

    public boolean canFollowAcrossDimensions() {
        return this.followSession.canChangeDimension();
    }

    public void startFollowSession(UUID playerUuid, PlayerMovementIntent intent, int ticksRemaining, boolean canChangeDimension) {
        this.startFollowSession(playerUuid, intent, ticksRemaining, canChangeDimension, true);
    }

    public void startFollowSession(UUID playerUuid, PlayerMovementIntent intent, int ticksRemaining, boolean canChangeDimension, boolean triggerScene) {
        boolean wasFollowing = this.isFollowing();
        FollowSession session = new FollowSession(playerUuid, intent, ticksRemaining, canChangeDimension);
        if (session.active()) {
            this.followSession = session;
            this.entityData.set(FOLLOWING, true);
            if (!wasFollowing && triggerScene) {
                this.triggerScene(SceneTrigger.FOLLOW_STARTED);
            }
        } else {
            this.clearFollowSession();
        }
    }

    public void clearFollowSession() {
        this.clearFollowSession(true);
    }

    public void clearFollowSession(boolean triggerScene) {
        boolean wasFollowing = this.isFollowing();
        this.followSession = FollowSession.none();
        this.entityData.set(FOLLOWING, false);
        if (wasFollowing && triggerScene) {
            this.triggerScene(SceneTrigger.FOLLOW_ENDED);
        }
    }

    public void tickFollowSession() {
        if (!this.isFollowing()) {
            return;
        }
        this.followSession = this.followSession.tick();
        if (this.followSession.active()) {
            this.entityData.set(FOLLOWING, true);
        } else {
            this.clearFollowSession();
        }
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    public String getGivenName() {
        return this.entityData.get(GIVEN_NAME);
    }

    public void setGivenName(String givenName) {
        this.entityData.set(GIVEN_NAME, givenName);
    }

    public String getGender() {
        return this.entityData.get(GENDER);
    }

    public void setGender(String gender) {
        this.entityData.set(GENDER, normalize(gender, "FEMALE", "MALE", "FEMALE", "NONBINARY"));
    }

    public String getBloodType() {
        return this.entityData.get(BLOOD_TYPE);
    }

    public void setBloodType(String bloodType) {
        this.entityData.set(BLOOD_TYPE, normalize(bloodType, "O", "A", "AB", "B", "O"));
    }

    public String getDere() {
        return this.entityData.get(DERE);
    }

    public void setDere(String dere) {
        this.entityData.set(DERE, normalize(dere, "NYANDERE", "NYANDERE", "HIMEDERE", "KUUDERE", "TSUNDERE", "YANDERE", "DEREDERE", "DANDERE"));
    }

    public String getZodiac() {
        return this.entityData.get(ZODIAC);
    }

    public void setZodiac(String zodiac) {
        this.entityData.set(ZODIAC, normalize(zodiac, "ARIES", "ARIES", "TAURUS", "GEMINI", "CANCER", "LEO", "VIRGO", "LIBRA", "SCORPIO", "SAGITTARIUS", "CAPRICORN", "AQUARIUS", "PISCES"));
    }

    public String getEmotion() {
        return this.entityData.get(EMOTION);
    }

    public void setEmotion(String emotion) {
        this.entityData.set(EMOTION, normalize(emotion, "NORMAL", "ANGRY", "BEGGING", "CONFUSED", "CRYING", "MISCHIEVOUS", "EMBARRASSED", "HAPPY", "NORMAL", "PAINED", "PSYCHOTIC", "SCARED", "SICK", "SNOOTY", "SMITTEN", "TIRED"));
    }

    public String getAnimationKey() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimationKey(String animation) {
        this.entityData.set(ANIMATION, normalize(animation, "DEFAULT", "DEFAULT", "YEARBOOK", "WAVE"));
    }

    public boolean isGuiPreview() {
        return this.guiPreview;
    }

    public void setGuiPreview(boolean guiPreview) {
        this.guiPreview = guiPreview;
    }

    public float getFoodLevel() {
        return this.entityData.get(FOOD_LEVEL);
    }

    public void setFoodLevel(float foodLevel) {
        this.entityData.set(FOOD_LEVEL, foodLevel);
    }

    public void addFoodLevel(float foodLevel) {
        this.setFoodLevel(this.getFoodLevel() + foodLevel);
        this.syncFoodToDb();
    }

    public float getExhaustion() {
        return this.entityData.get(EXHAUSTION);
    }

    public void setExhaustion(float exhaustion) {
        this.entityData.set(EXHAUSTION, exhaustion);
    }

    public void addExhaustion(float exhaustion) {
        this.setExhaustion(this.getExhaustion() + exhaustion);
        this.syncFoodToDb();
    }

    public float getSaturation() {
        return this.entityData.get(SATURATION);
    }

    public void setSaturation(float saturation) {
        this.entityData.set(SATURATION, saturation);
    }

    public void addSaturation(float saturation) {
        this.setSaturation(this.getSaturation() + saturation);
        this.syncFoodToDb();
    }

    public float getStress() {
        return this.entityData.get(STRESS);
    }

    public void setStress(float stress) {
        this.entityData.set(STRESS, stress);
    }

    public void addStress(float stress) {
        this.setStress(this.getStress() + stress);
    }

    public float getRelaxation() {
        return this.entityData.get(RELAXATION);
    }

    public void setRelaxation(float relaxation) {
        this.entityData.set(RELAXATION, relaxation);
    }

    public void addRelaxation(float relaxation) {
        this.setRelaxation(this.getRelaxation() + relaxation);
    }

    public float getLoyalty() {
        return this.entityData.get(LOYALTY);
    }

    public void setLoyalty(float loyalty) {
        this.entityData.set(LOYALTY, loyalty);
    }

    public void addLoyalty(float loyalty) {
        this.setLoyalty(this.getLoyalty() + loyalty);
    }

    public float getAffection() {
        return this.entityData.get(AFFECTION);
    }

    public void setAffection(float affection) {
        this.entityData.set(AFFECTION, affection);
    }

    public void addAffection(float affection) {
        this.setAffection(this.getAffection() + affection);
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

    public void addAge(float age) {
        this.setAge(this.getAge() + age);
    }

    public String getFamilyName() {
        return "Minashigo";
    }

    public boolean hasHome() {
        return this.hasHome;
    }

    public void setHasHome(boolean hasHome) {
        this.hasHome = hasHome;
    }

    public DimBlockPos getHome() {
        return this.home;
    }

    public void setHome(DimBlockPos home) {
        this.home = home == null ? new DimBlockPos() : home;
    }

    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.level().dimension(), this.blockPosition());
    }

    public void setHomeToCurrentPosition() {
        this.setHasHome(true);
        this.setHome(this.getDimBlockPos());
    }

    public Player getPlayer() {
        return this.level().getPlayerByUUID(this.getPlayerUUID());
    }

    public ServerPlayer getServerPlayer() {
        return this.getServer() == null ? null : this.getServer().getPlayerList().getPlayer(this.getPlayerUUID());
    }

    public void setPlayer(Player player) {
        this.setPlayerUUID(player.getUUID());
    }

    public boolean isPlayerOnline() {
        return this.getPlayer() != null;
    }

    public boolean isPlayerBusy() {
        Player player = this.getPlayer();
        return player != null && player.containerMenu != player.inventoryMenu;
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Dialogue getDialogue() {
        return this.dialogue;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
        this.response = null;
        this.setAnimationKey(dialogue == null ? "DEFAULT" : dialogue.speaker().animation());
    }

    public boolean hasDialogue() {
        return this.dialogue != null;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        if (this.dialogue != null) {
            this.response = response;
        }
    }

    public boolean hasResponse() {
        return this.response != null;
    }

    public void clearDialogue() {
        this.dialogue = null;
        this.response = null;
        this.setAnimationKey("DEFAULT");
    }

    public SceneManager sceneManager() {
        return this.sceneManager;
    }

    public boolean triggerScene(SceneTrigger trigger) {
        return this.sceneManager.trigger(trigger);
    }

    public UUID getDialogueTarget() {
        return this.dialogueTarget;
    }

    public void setDialogueTarget(UUID player) {
        this.dialogueTarget = player == null ? new UUID(0L, 0L) : player;
    }

    public void sayInChat(String key, Object... params) {
        Component message = Component.translatable(key, params);
        for (Player player : this.level().players()) {
            if (player.distanceTo(this) < 8.0D) {
                this.sayInChat(player, message);
            }
        }
    }

    public void sayInChat(Player player, String key, Object... params) {
        this.sayInChat(player, Component.translatable(key, params));
    }

    public void sayInChat(Player player, Component component) {
        player.displayClientMessage(component, false);
    }

    public void updateHungerState() {
    }

    public void updateLonelyState() {
    }

    public void updateStressState() {
    }

    public void updateActionState() {
        if (this.level().isClientSide || this.hasDialogue()) {
            return;
        }
        if (this.updateFollowSessionMovement()) {
            return;
        }
        if (this.socialTickDelay > 0) {
            --this.socialTickDelay;
            return;
        }
        this.socialTickDelay = MoeSocialRules.socialTickDelay(this.getDere(), this.random.nextInt());
        if (!this.updateBloodTypeSocialState()) {
            this.updateIdleRoutineMovement();
        }
    }

    private boolean updateFollowSessionMovement() {
        if (!this.isFollowing()) {
            return false;
        }
        Player target = this.followTargetPlayer();
        Vec3 destination = this.routineAwareFollowDestination(target);
        if (destination == null) {
            return true;
        }
        double stopDistance = target == null || this.isRoutineDriftDestination(destination) ? 2.0D : 3.0D;
        if (this.position().distanceToSqr(destination) > stopDistance * stopDistance) {
            this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, this.followMoveSpeed());
        }
        return true;
    }

    public Vec3 routineAwareFollowDestination(Player target) {
        Vec3 routine = this.followRoutinePosition();
        if (target == null || target.level() != this.level()) {
            return routine;
        }
        Vec3 playerPosition = target.position();
        double distanceToPlayerSqr = this.distanceToSqr(target);
        if (distanceToPlayerSqr > 16.0D * 16.0D) {
            return playerPosition;
        }
        if (routine == null) {
            return playerPosition;
        }
        if (distanceToPlayerSqr <= 8.0D * 8.0D && this.followRoutineDriftWeight() >= 0.35F) {
            return routine;
        }
        return playerPosition;
    }

    public float followRoutineDriftWeight() {
        if (!this.isFollowing() || this.currentRoutineAnchor().isEmpty()) {
            return 0.0F;
        }
        float stressPull = Math.min(0.45F, Math.max(0.0F, this.getStress()) / 40.0F);
        float timePull = this.getFollowTicksRemaining() <= 20 * 45 ? 0.35F : 0.0F;
        float intentPull = this.getFollowIntent() == PlayerMovementIntent.PARTY_INVITE ? 0.15F : 0.05F;
        return Math.min(1.0F, stressPull + timePull + intentPull);
    }

    private Player followTargetPlayer() {
        if (this.getServer() == null) {
            return null;
        }
        return this.getServer().getPlayerList().getPlayer(this.getFollowPlayerUUID());
    }

    private Vec3 followRoutinePosition() {
        return this.currentRoutineAnchor()
                .map(anchor -> Vec3.atBottomCenterOf(anchor.dimPos().getPos()))
                .orElse(null);
    }

    public java.util.Optional<MoeAnchor> currentRoutineAnchor() {
        return MoeAnchorResolver.bestRoutineAnchor(this);
    }

    public RoutineIntent getRoutineIntent() {
        return this.routineIntent;
    }

    public void setRoutineIntent(RoutineIntent routineIntent) {
        this.routineIntent = routineIntent == null ? RoutineIntent.IDLE : routineIntent;
    }

    public RoutineIntent getEffectiveRoutineIntent() {
        if (this.routineIntent != RoutineIntent.IDLE) {
            return this.routineIntent;
        }
        if (this.getStress() >= 10.0F && this.getRelaxation() < this.getStress()) {
            return RoutineIntent.RELAX;
        }
        if (this.getFoodLevel() <= 6.0F) {
            return RoutineIntent.GATHER;
        }
        return RoutineIntent.IDLE;
    }

    public boolean moveTowardCurrentRoutineAnchor(double speed) {
        java.util.Optional<MoeAnchor> anchor = this.currentRoutineAnchor();
        if (anchor.isEmpty() || anchor.get().dimPos().getDim() != this.level().dimension()) {
            return false;
        }
        Vec3 destination = Vec3.atBottomCenterOf(anchor.get().dimPos().getPos());
        this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
        return true;
    }

    public boolean sleepAtHome(HideUntil until) {
        if (!this.canSleepAtHome()) {
            return false;
        }
        this.setTimeSinceSleep(0);
        this.addRelaxation(0.5F);
        this.setStress(Math.max(0.0F, this.getStress() - 0.5F));
        return this.hide(until) != null;
    }

    public boolean canSleepAtHome() {
        if (!(this.level() instanceof ServerLevel level) || !this.hasHome() || this.getHome().isEmpty()) {
            return false;
        }
        BlockPos homePos = this.getHome().getPos();
        return this.getHome().getDim() == level.dimension()
                && this.blockPosition().equals(homePos)
                && level.isEmptyBlock(homePos);
    }

    public Vec3 idleRoutineDestination() {
        if (this.isFollowing() || this.isSitting() || this.isPassenger()) {
            return null;
        }
        Vec3 socialDestination = this.idleSocialOrbitDestination();
        if (socialDestination != null) {
            return socialDestination;
        }
        return this.idleAnchorDestination();
    }

    private boolean updateIdleRoutineMovement() {
        if (this.getEffectiveRoutineIntent() == RoutineIntent.SLEEP && this.sleepAtHome(HideUntil.EXPOSED)) {
            return true;
        }
        Vec3 destination = this.idleRoutineDestination();
        if (destination == null || this.position().distanceToSqr(destination) <= 1.44D) {
            return false;
        }
        this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, this.idleRoutineMoveSpeed());
        this.applyIdleRoutineWellbeing();
        return true;
    }

    private Vec3 idleSocialOrbitDestination() {
        List<Moe> nearby = MoeSocialContext.nearby(this, IDLE_SOCIAL_RADIUS).stream()
                .filter(other -> !other.isFollowing() && !other.isSitting() && !other.isPassenger())
                .toList();
        if (nearby.isEmpty()) {
            return null;
        }
        Vec3 center = this.position();
        for (Moe other : nearby) {
            center = center.add(other.position());
        }
        center = center.scale(1.0D / (nearby.size() + 1));
        double radius = Math.min(4.5D, 2.25D + nearby.size() * 0.35D);
        return this.orbitDestination(center, radius, 1.75D);
    }

    private Vec3 idleAnchorDestination() {
        return MoeAnchorResolver.nearbyRoutineAnchor(this, IDLE_ANCHOR_RADIUS)
                .map(anchor -> {
                    Vec3 center = Vec3.atBottomCenterOf(anchor.dimPos().getPos());
                    double distanceSqr = this.position().distanceToSqr(center);
                    if (distanceSqr > 6.0D * 6.0D) {
                        return center;
                    }
                    return this.orbitDestination(center, 2.75D, 1.25D);
                })
                .orElse(null);
    }

    private Vec3 orbitDestination(Vec3 center, double radius, double tangentStep) {
        Vec3 flat = new Vec3(this.getX() - center.x, 0.0D, this.getZ() - center.z);
        if (flat.lengthSqr() < 0.0001D) {
            double angle = Math.floorMod(this.getUUID().getLeastSignificantBits(), 6283L) / 1000.0D;
            flat = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
        }
        Vec3 radial = flat.normalize();
        Vec3 tangent = new Vec3(-radial.z, 0.0D, radial.x);
        return new Vec3(center.x, this.getY(), center.z)
                .add(radial.scale(radius))
                .add(tangent.scale(tangentStep));
    }

    private double idleRoutineMoveSpeed() {
        return 0.55D + Math.min(0.35D, MoeSocialRules.socialMoveSpeed(this.getDere()) * 0.2D);
    }

    private void applyIdleRoutineWellbeing() {
        if (this.getEffectiveRoutineIntent() == RoutineIntent.RELAX
                && this.currentRoutineAnchor().map(anchor -> anchor.type() == MoeAnchorType.GARDEN).orElse(false)) {
            this.addRelaxation(0.02F);
            if (this.getRelaxation() >= 1.0F && this.getStress() > 0.0F) {
                this.addStress(-0.01F);
            }
        }
    }

    private boolean isRoutineDriftDestination(Vec3 destination) {
        Vec3 routine = this.followRoutinePosition();
        return routine != null && routine.distanceToSqr(destination) < 0.0001D;
    }

    private double followMoveSpeed() {
        double speed = this.getFollowIntent() == PlayerMovementIntent.PARTY_INVITE ? 1.05D : 1.2D;
        if (this.getStress() >= 16.0F) {
            speed *= 0.75D;
        }
        return speed;
    }

    public void updateSleepState() {
    }

    private boolean updateBloodTypeSocialState() {
        MoeSocialContext context = MoeSocialContext.find(this, 8.0D).orElse(null);
        if (context == null) {
            return false;
        }
        Moe socialTarget = context.target();
        MoeSocialRules.SocialSignal strongest = context.signal();
        this.getLookControl().setLookAt(socialTarget, 30.0F, 30.0F);
        MoeSocialRules.SocialVisual visual = context.visual();
        MoeSocialRules.DereReaction reaction = context.reaction();
        boolean moved = this.updateBloodTypeSocialMovement(socialTarget, strongest);
        moved = this.updateDereSocialReaction(socialTarget, reaction) || moved;
        this.spawnBloodTypeSocialParticles(socialTarget, visual);
        this.spawnDereSocialParticles(reaction);
        boolean tense = strongest.tension() > strongest.affinity();
        if (strongest.affinity() >= 0.6F) {
            this.setEmotion(MoeSocialRules.reactionEmotion(this.getDere(), reaction, false));
            this.addRelaxation(0.05F);
        } else if (tense) {
            this.setEmotion(MoeSocialRules.reactionEmotion(this.getDere(), reaction, true));
            this.addStress(0.05F);
        }
        this.applyDereSocialFeeling(reaction);
        return moved;
    }

    private boolean updateBloodTypeSocialMovement(Moe socialTarget, MoeSocialRules.SocialSignal signal) {
        if (this.isFollowing() || this.isSitting() || this.isPassenger()) {
            return false;
        }
        double distanceSqr = this.distanceToSqr(socialTarget);
        double stepDistance = MoeSocialRules.socialStepDistance(this.getDere());
        double speed = MoeSocialRules.socialMoveSpeed(this.getDere());
        switch (MoeSocialRules.movementFor(signal, distanceSqr)) {
            case APPROACH -> {
                Vec3 toward = socialTarget.position().subtract(this.position());
                double distance = toward.length();
                if (distance > 0.0001D) {
                    Vec3 destination = this.position().add(toward.normalize().scale(Math.min(stepDistance, distance - 1.5D)));
                    this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
                    return true;
                }
            }
            case AVOID -> {
                Vec3 away = this.position().subtract(socialTarget.position());
                if (away.lengthSqr() < 0.0001D) {
                    away = new Vec3(this.random.nextDouble() - 0.5D, 0.0D, this.random.nextDouble() - 0.5D);
                }
                Vec3 destination = this.position().add(away.normalize().scale(stepDistance));
                this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
                return true;
            }
            case IDLE -> {
            }
        }
        return false;
    }

    private boolean updateDereSocialReaction(Moe socialTarget, MoeSocialRules.DereReaction reaction) {
        if (this.isFollowing() || this.isSitting() || this.isPassenger()) {
            return false;
        }
        return switch (reaction) {
            case CLING -> moveToward(socialTarget, MoeSocialRules.socialStepDistance(this.getDere()) * 1.25D, 1.25D, 0.75D);
            case FLUSTER_RETREAT -> moveAwayFrom(socialTarget, 3.0D, MoeSocialRules.socialMoveSpeed(this.getDere()));
            case SHY_RETREAT -> moveAwayFrom(socialTarget, 2.5D, 0.8D);
            case SHOW_OFF -> {
                Vec3 around = this.position().subtract(socialTarget.position());
                if (around.lengthSqr() < 0.0001D) {
                    around = new Vec3(1.0D, 0.0D, 0.0D);
                }
                Vec3 side = new Vec3(-around.z, 0.0D, around.x).normalize().scale(2.0D);
                Vec3 destination = this.position().add(side);
                this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, MoeSocialRules.socialMoveSpeed(this.getDere()));
                yield true;
            }
            case CELEBRATE, OBSERVE, NONE -> false;
        };
    }

    private boolean moveToward(Moe socialTarget, double stepDistance, double speed, double personalSpace) {
        Vec3 toward = socialTarget.position().subtract(this.position());
        double distance = toward.length();
        if (distance > personalSpace && distance > 0.0001D) {
            Vec3 destination = this.position().add(toward.normalize().scale(Math.min(stepDistance, distance - personalSpace)));
            this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
            return true;
        }
        return false;
    }

    private boolean moveAwayFrom(Moe socialTarget, double stepDistance, double speed) {
        Vec3 away = this.position().subtract(socialTarget.position());
        if (away.lengthSqr() < 0.0001D) {
            away = new Vec3(this.random.nextDouble() - 0.5D, 0.0D, this.random.nextDouble() - 0.5D);
        }
        Vec3 destination = this.position().add(away.normalize().scale(stepDistance));
        this.getMoveControl().setWantedPosition(destination.x, destination.y, destination.z, speed);
        return true;
    }

    private void applyDereSocialFeeling(MoeSocialRules.DereReaction reaction) {
        switch (reaction) {
            case CELEBRATE -> this.addRelaxation(0.04F);
            case CLING, FLUSTER_RETREAT, SHY_RETREAT, SHOW_OFF -> this.addStress(0.03F);
            case OBSERVE, NONE -> {
            }
        }
    }

    private void spawnBloodTypeSocialParticles(Moe socialTarget, MoeSocialRules.SocialVisual visual) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (visual == MoeSocialRules.SocialVisual.NONE) {
            return;
        }
        Entity focus = visual == MoeSocialRules.SocialVisual.FAME ? socialTarget : this;
        serverLevel.sendParticles(
                particleFor(visual),
                focus.getX(),
                focus.getY() + focus.getBbHeight() + 0.15D,
                focus.getZ(),
                visual == MoeSocialRules.SocialVisual.FAME ? 4 : 2,
                0.25D,
                0.2D,
                0.25D,
                0.02D);
    }

    private void spawnDereSocialParticles(MoeSocialRules.DereReaction reaction) {
        if (!(this.level() instanceof ServerLevel serverLevel) || reaction == MoeSocialRules.DereReaction.NONE) {
            return;
        }
        serverLevel.sendParticles(
                particleFor(reaction),
                this.getX(),
                this.getY() + this.getBbHeight() + 0.35D,
                this.getZ(),
                reaction == MoeSocialRules.DereReaction.OBSERVE ? 1 : 2,
                0.2D,
                0.15D,
                0.2D,
                0.01D);
    }

    private static ParticleOptions particleFor(MoeSocialRules.SocialVisual visual) {
        return switch (visual) {
            case AFFINITY -> ParticleTypes.HEART;
            case FAME -> ParticleTypes.NOTE;
            case INTEREST -> ParticleTypes.HAPPY_VILLAGER;
            case TENSION -> ParticleTypes.ANGRY_VILLAGER;
            case NONE -> ParticleTypes.POOF;
        };
    }

    private static ParticleOptions particleFor(MoeSocialRules.DereReaction reaction) {
        return switch (reaction) {
            case CELEBRATE -> ParticleTypes.HEART;
            case CLING -> ParticleTypes.CRIMSON_SPORE;
            case FLUSTER_RETREAT -> ParticleTypes.SMOKE;
            case SHY_RETREAT -> ParticleTypes.POOF;
            case SHOW_OFF -> ParticleTypes.NOTE;
            case OBSERVE -> ParticleTypes.HAPPY_VILLAGER;
            case NONE -> ParticleTypes.POOF;
        };
    }

    public int getTimeUntilHungry() {
        return this.timeUntilHungry;
    }

    public void setTimeUntilHungry(int timeUntilHungry) {
        this.timeUntilHungry = timeUntilHungry;
    }

    public int getTimeUntilLonely() {
        return this.timeUntilLonely;
    }

    public void setTimeUntilLonely(int timeUntilLonely) {
        this.timeUntilLonely = timeUntilLonely;
    }

    public int getTimeUntilStress() {
        return this.timeUntilStress;
    }

    public void setTimeUntilStress(int timeUntilStress) {
        this.timeUntilStress = timeUntilStress;
    }

    public int getTimeSinceSleep() {
        return this.timeSinceSleep;
    }

    public void setTimeSinceSleep(int timeSinceSleep) {
        this.timeSinceSleep = timeSinceSleep;
    }

    public CompoundTag getTileEntityData() {
        return this.tileEntityData.copy();
    }

    public void setTileEntityData(CompoundTag tileEntityData) {
        this.tileEntityData = tileEntityData == null ? new CompoundTag() : tileEntityData.copy();
    }

    public void moveToBlock(BlockPos pos) {
        this.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public boolean hasWings() {
        return this.getVisibleBlockState().is(CustomTags.HAS_WINGS);
    }

    public boolean hasCatFeatures() {
        return this.getVisibleBlockState().is(CustomTags.HAS_CAT_FEATURES);
    }

    public boolean hasGlow() {
        return this.getVisibleBlockState().is(CustomTags.HAS_GLOW);
    }

    public boolean ignoresVolume() {
        return this.getVisibleBlockState().is(CustomTags.IGNORES_VOLUME);
    }

    public boolean isAssociatedPlayer(Entity entity) {
        return entity != null && this.getPlayerUUID().equals(entity.getUUID());
    }

    @Deprecated
    public boolean isOwner(Entity entity) {
        return this.isAssociatedPlayer(entity);
    }

    public boolean canDialogueWith(Player player) {
        return this.canDialogueWith(player, true);
    }

    private boolean canDialogueWith(Player player, boolean createRelationship) {
        if (player == null) {
            return false;
        }
        if (this.isAssociatedPlayer(player)) {
            return true;
        }
        if (!(this.level() instanceof ServerLevel level)) {
            return false;
        }
        try {
            BlockPartyDB db = BlockPartyDB.get(level);
            if (createRelationship) {
                db.ensurePlayerRelationship(this.getDatabaseID(), player.getUUID());
                return true;
            }
            return db.hasPlayerRelationship(player.getUUID(), this.getDatabaseID());
        } catch (RuntimeException | SQLException exception) {
            return false;
        }
    }

    public boolean isBeingLookedAt() {
        Player player = this.level().getPlayerByUUID(this.getPlayerUUID());
        if (player == null) {
            return false;
        }
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 distance = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
        double length = distance.length();
        if (length <= 0.0D) {
            return false;
        }
        distance = distance.normalize();
        double dot = look.dot(distance);
        return dot > 1.0D - 0.025D / length && player.hasLineOfSight(this);
    }

    public void assignUniqueNameIfDefault() {
        if (!"Tokumei".equals(this.getGivenName())) {
            return;
        }
        List<String> names = MoeNamesReloadListener.names(this.getGender()).stream()
                .filter(name -> !BlockPartyDB.get(this.level()).names().contains(name))
                .toList();
        String name = names.isEmpty() ? "Tokumei" : names.get(this.random.nextInt(names.size()));
        this.setGivenName(name);
        if (!"Tokumei".equals(name)) {
            BlockPartyDB.get(this.level()).addName(name);
        }
    }

    private void applyBlockProfileFromState(BlockState state) {
        if (state.is(CustomTags.HAS_MALE_PRONOUNS)) {
            this.setGender("MALE");
        } else if (state.is(CustomTags.HAS_NONBINARY_PRONOUNS)) {
            this.setGender("NONBINARY");
        } else if (state.is(CustomTags.HAS_FEMALE_PRONOUNS)) {
            this.setGender("FEMALE");
        }

        if (state.is(CustomTags.BLOOD_TYPE_A)) {
            this.setBloodType("A");
        } else if (state.is(CustomTags.BLOOD_TYPE_AB)) {
            this.setBloodType("AB");
        } else if (state.is(CustomTags.BLOOD_TYPE_B)) {
            this.setBloodType("B");
        } else if (state.is(CustomTags.BLOOD_TYPE_O)) {
            this.setBloodType("O");
        }

        if (state.is(CustomTags.NYANDERE)) {
            this.setDere("NYANDERE");
        } else if (state.is(CustomTags.HIMEDERE)) {
            this.setDere("HIMEDERE");
        } else if (state.is(CustomTags.KUUDERE)) {
            this.setDere("KUUDERE");
        } else if (state.is(CustomTags.TSUNDERE)) {
            this.setDere("TSUNDERE");
        } else if (state.is(CustomTags.YANDERE)) {
            this.setDere("YANDERE");
        } else if (state.is(CustomTags.DEREDERE)) {
            this.setDere("DEREDERE");
        } else if (state.is(CustomTags.DANDERE)) {
            this.setDere("DANDERE");
        }

        if (state.is(CustomTags.ARIES)) {
            this.setZodiac("ARIES");
        } else if (state.is(CustomTags.TAURUS)) {
            this.setZodiac("TAURUS");
        } else if (state.is(CustomTags.GEMINI)) {
            this.setZodiac("GEMINI");
        } else if (state.is(CustomTags.CANCER)) {
            this.setZodiac("CANCER");
        } else if (state.is(CustomTags.LEO)) {
            this.setZodiac("LEO");
        } else if (state.is(CustomTags.VIRGO)) {
            this.setZodiac("VIRGO");
        } else if (state.is(CustomTags.LIBRA)) {
            this.setZodiac("LIBRA");
        } else if (state.is(CustomTags.SCORPIO)) {
            this.setZodiac("SCORPIO");
        } else if (state.is(CustomTags.SAGITTARIUS)) {
            this.setZodiac("SAGITTARIUS");
        } else if (state.is(CustomTags.CAPRICORN)) {
            this.setZodiac("CAPRICORN");
        } else if (state.is(CustomTags.AQUARIUS)) {
            this.setZodiac("AQUARIUS");
        } else if (state.is(CustomTags.PISCES)) {
            this.setZodiac("PISCES");
        }

        if (state.is(CustomTags.IGNORES_VOLUME)) {
            this.setMoeScale(1.0F);
        }
        if (!this.isCardinal()) {
            this.assignUniqueNameIfDefault();
        }
    }

    private void applyBlockPhysicalState(BlockState state) {
        this.setMoeScale(state.is(CustomTags.IGNORES_VOLUME) ? 1.0F : this.getBlockVolume(state));
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, this.fireImmune() ? 0.0F : -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, this.fireImmune() ? 0.0F : -1.0F);
        boolean flying = this.hasWings();
        this.moveControl = flying ? new FlyingMoveControl(this, 10, false) : new MoveControl(this);
        this.navigation = flying ? new FlyingPathNavigation(this, this.level()) : new GroundPathNavigation(this, this.level());
        if (this.navigation instanceof GroundPathNavigation ground) {
            ground.setCanOpenDoors(true);
        }
    }

    public float getBlockVolume(BlockState state) {
        double volume = 0.0D;
        for (AABB aabb : state.getOcclusionShape().toAabbs()) {
            volume += (aabb.maxX - aabb.minX) * (aabb.maxY - aabb.minY) * (aabb.maxZ - aabb.minZ);
        }
        volume = Math.cbrt(volume);
        if (!Double.isFinite(volume) || volume < 0.25D) {
            volume = 1.0D;
        }
        return (float) volume * 0.9375F;
    }

    public float getBlockBuffer() {
        return 0.5F / (this.getActualBlockState().getDestroySpeed(this.level(), this.blockPosition()) + 1.0F);
    }

    @Override
    public float getVoicePitch() {
        float pitch = (super.getVoicePitch() + this.getBlockBuffer() + 0.6F) / 2.0F;
        return pitch + (1.0F - this.getMoeScale());
    }

    @Override
    public boolean fireImmune() {
        return !this.getActualBlockState().isFlammable(this.level(), this.blockPosition(), this.getDirection());
    }

    private static String normalize(String value, String fallback, String... allowed) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        for (String option : allowed) {
            if (option.equals(normalized)) {
                return normalized;
            }
        }
        return fallback;
    }

    private String weightedBloodType() {
        int value = this.random.nextInt(8);
        if (value < 1) {
            return "AB";
        }
        if (value < 3) {
            return "B";
        }
        if (value < 5) {
            return "A";
        }
        return "O";
    }

    public MoeInHiding hide(HideUntil until) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        BlockPartyDB db = BlockPartyDB.get(serverLevel);
        NPC row;
        try {
            row = db.findNpc(this.getDatabaseID()).orElse(null);
            if (row == null) {
                return null;
            }
        } catch (SQLException exception) {
            return null;
        }

        BlockPos pos = this.blockPosition();
        serverLevel.setBlock(pos, this.getBlockState(), 3);
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.getPersistentData().merge(this.getTileEntityData());
            blockEntity.setChanged();
        }

        MoeInHiding hiding = new MoeInHiding(CustomEntities.MOE_IN_HIDING.get(), serverLevel);
        hiding.setDatabaseID(this.getDatabaseID());
        hiding.setPlayerUUID(this.getPlayerUUID());
        hiding.setAttachPos(pos);
        hiding.setHideUntil(until);
        hiding.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        if (!serverLevel.addFreshEntity(hiding)) {
            return null;
        }
        try {
            row.updateFromMoe(db, serverLevel, this);
            row.markHiding(db, serverLevel, pos, this.getBlockState());
        } catch (SQLException exception) {
            hiding.discard();
            return null;
        }
        HidingSpots.get(serverLevel).put(pos, this.getDatabaseID());
        this.discard();
        return hiding;
    }
}
