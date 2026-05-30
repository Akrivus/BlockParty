package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import block_party.entities.ai.goal.MoeGoals;
import block_party.entities.chores.ChoreScheduler;
import block_party.entities.data.HidingSpots;
import block_party.entities.environment.MoeEnvironmentalBehavior;
import block_party.entities.environment.MoeEnvironmentalMemory;
import block_party.entities.environment.MoeEnvironmentalObservation;
import block_party.entities.environment.MoeEnvironmentalRules;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.goals.HideUntil;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.movement.MoeAnchorResolver;
import block_party.entities.movement.FollowSession;
import block_party.entities.movement.MoeRoutineBehavior;
import block_party.entities.movement.PartyInvites;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.entities.preferences.MoeGiftMemory;
import block_party.entities.preferences.MoeItemPreferences;
import block_party.entities.profile.MoeBlockProfile;
import block_party.entities.profile.MoeFamilyNames;
import block_party.entities.social.MoeSocialBehavior;
import block_party.entities.social.MoeSocialPlaceMemory;
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
import block_party.world.structure.MoeStructureAssignment;
import block_party.world.structure.MoeStructureCohortCoordinator;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.Vec3;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class Moe extends PathfinderMob implements ContainerListener, MenuProvider {
    private static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";
    public static final String NBT_DATABASE_ID = "DatabaseID";
    public static final String NBT_BLOCK_STATE = "BlockState";
    public static final String NBT_VISIBLE_BLOCK_STATE = "VisibleBlockState";
    public static final String NBT_SCALE = "Scale";
    public static final String NBT_FOLLOWING = "Following";
    public static final String NBT_SITTING = "Sitting";
    public static final String NBT_PLAYER_UUID = "PlayerUUID";
    public static final String NBT_OWNER_UUID = "OwnerUUID";
    public static final String NBT_GIVEN_NAME = "GivenName";
    public static final String NBT_GENDER = "Gender";
    public static final String NBT_BLOOD_TYPE = "BloodType";
    public static final String NBT_DERE = "Dere";
    public static final String NBT_ZODIAC = "Zodiac";
    public static final String NBT_EMOTION = "Emotion";
    public static final String NBT_ANIMATION = "Animation";
    public static final String NBT_FOOD_LEVEL = "FoodLevel";
    public static final String NBT_EXHAUSTION = "Exhaustion";
    public static final String NBT_SATURATION = "Saturation";
    public static final String NBT_STRESS = "Stress";
    public static final String NBT_RELAXATION = "Relaxation";
    public static final String NBT_LOYALTY = "Loyalty";
    public static final String NBT_AFFECTION = "Affection";
    public static final String NBT_SLOUCH = "Slouch";
    public static final String NBT_AGE = "Age";
    public static final String NBT_HAS_HOME = "HasHome";
    public static final String NBT_HOME = "Home";
    public static final String NBT_ROUTINE_INTENT = "RoutineIntent";
    public static final String NBT_FOLLOW_SESSION = "FollowSession";
    public static final String NBT_FOLLOW_PLAYER_UUID = "PlayerUUID";
    public static final String NBT_FOLLOW_INTENT = "Intent";
    public static final String NBT_FOLLOW_TICKS_REMAINING = "TicksRemaining";
    public static final String NBT_FOLLOW_CAN_CHANGE_DIMENSION = "CanChangeDimension";
    public static final String NBT_STRUCTURE_ASSIGNMENT = "StructureAssignment";
    public static final String NBT_CHORE = "Chore";
    public static final String NBT_REMEMBERED_PLACE = "RememberedPlace";
    public static final String NBT_LAST_SEEN_AT = "LastSeenAt";
    public static final String NBT_TIME_UNTIL_HUNGRY = "TimeUntilHungry";
    public static final String NBT_TIME_UNTIL_LONELY = "TimeUntilLonely";
    public static final String NBT_TIME_UNTIL_STRESS = "TimeUntilStress";
    public static final String NBT_TIME_SINCE_SLEEP = "TimeSinceSleep";
    public static final String NBT_INVENTORY = "Inventory";
    public static final String NBT_TILE_ENTITY = "TileEntity";
    private static final int COMPATIBILITY_FOLLOW_TICKS = 20 * 60 * 5;

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
    private int temporaryAnimationTicks;
    private String temporaryAnimationKey = "DEFAULT";
    private Dialogue dialogue;
    private Response response;
    private boolean guiPreview;
    private UUID dialogueTarget = new UUID(0L, 0L);
    private FollowSession followSession = FollowSession.none();
    private RoutineIntent routineIntent = RoutineIntent.IDLE;
    private MoeStructureAssignment structureAssignment = MoeStructureAssignment.none();
    private final ChoreScheduler chores = new ChoreScheduler(this);
    private final MoeEnvironmentalBehavior environment = new MoeEnvironmentalBehavior(this);
    private final MoeEnvironmentalMemory environmentalMemory = new MoeEnvironmentalMemory(this);
    private final MoeGiftMemory gifts = new MoeGiftMemory(this);
    private final MoeRoutineBehavior routine = new MoeRoutineBehavior(this);
    private final MoeSocialBehavior social = new MoeSocialBehavior(this);
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
    protected void registerGoals() {
        MoeGoals.register(this, this.goalSelector);
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
        this.setDatabaseID(compound.getLong(NBT_DATABASE_ID));
        this.setBlockState(Block.stateById(compound.getInt(NBT_BLOCK_STATE)), false);
        this.setVisibleBlockState(compound.contains(NBT_VISIBLE_BLOCK_STATE)
                ? Block.stateById(compound.getInt(NBT_VISIBLE_BLOCK_STATE))
                : this.getBlockState());
        this.setMoeScale(compound.contains(NBT_SCALE) ? compound.getFloat(NBT_SCALE) : 1.0F);
        this.setSitting(compound.getBoolean(NBT_SITTING));
        if (compound.contains(NBT_OWNER_UUID)) {
            this.setPlayerUUID(UUID.fromString(compound.getString(NBT_OWNER_UUID)));
        }
        if (compound.contains(NBT_PLAYER_UUID)) {
            this.setPlayerUUID(UUID.fromString(compound.getString(NBT_PLAYER_UUID)));
        }
        if (compound.contains(NBT_FOLLOW_SESSION)) {
            this.readFollowSession(compound.getCompound(NBT_FOLLOW_SESSION));
        } else {
            this.setFollowing(compound.getBoolean(NBT_FOLLOWING));
        }
        if (compound.contains(NBT_GIVEN_NAME)) {
            this.setGivenName(compound.getString(NBT_GIVEN_NAME));
        }
        if (compound.contains(NBT_GENDER)) {
            this.setGender(compound.getString(NBT_GENDER));
        }
        if (compound.contains(NBT_BLOOD_TYPE)) {
            this.setBloodType(compound.getString(NBT_BLOOD_TYPE));
        }
        if (compound.contains(NBT_DERE)) {
            this.setDere(compound.getString(NBT_DERE));
        }
        if (compound.contains(NBT_ZODIAC)) {
            this.setZodiac(compound.getString(NBT_ZODIAC));
        }
        if (compound.contains(NBT_EMOTION)) {
            this.setEmotion(compound.getString(NBT_EMOTION));
        }
        if (compound.contains(NBT_ANIMATION)) {
            this.setAnimationKey(compound.getString(NBT_ANIMATION));
        }
        this.temporaryAnimationTicks = 0;
        this.temporaryAnimationKey = "DEFAULT";
        if (compound.contains(NBT_FOOD_LEVEL)) {
            this.setFoodLevel(compound.getFloat(NBT_FOOD_LEVEL));
        }
        if (compound.contains(NBT_EXHAUSTION)) {
            this.setExhaustion(compound.getFloat(NBT_EXHAUSTION));
        }
        if (compound.contains(NBT_SATURATION)) {
            this.setSaturation(compound.getFloat(NBT_SATURATION));
        }
        if (compound.contains(NBT_STRESS)) {
            this.setStress(compound.getFloat(NBT_STRESS));
        }
        if (compound.contains(NBT_RELAXATION)) {
            this.setRelaxation(compound.getFloat(NBT_RELAXATION));
        }
        if (compound.contains(NBT_LOYALTY)) {
            this.setLoyalty(compound.getFloat(NBT_LOYALTY));
        }
        if (compound.contains(NBT_AFFECTION)) {
            this.setAffection(compound.getFloat(NBT_AFFECTION));
        }
        if (compound.contains(NBT_AGE)) {
            this.setAge(compound.getFloat(NBT_AGE));
        }
        this.setHasHome(compound.getBoolean(NBT_HAS_HOME));
        if (compound.contains(NBT_HOME)) {
            this.setHome(new DimBlockPos(compound.getCompound(NBT_HOME)));
        }
        if (compound.contains(NBT_ROUTINE_INTENT)) {
            this.setRoutineIntent(RoutineIntent.fromValue(compound.getString(NBT_ROUTINE_INTENT)));
        }
        if (compound.contains(NBT_STRUCTURE_ASSIGNMENT)) {
            this.setStructureAssignment(MoeStructureAssignment.read(compound.getCompound(NBT_STRUCTURE_ASSIGNMENT)), false);
        }
        this.chores.read(compound);
        this.environmentalMemory.readRememberedPlace(compound, NBT_REMEMBERED_PLACE);
        if (compound.contains(NBT_LAST_SEEN_AT)) {
            this.setLastSeen(compound.getLong(NBT_LAST_SEEN_AT));
        }
        this.timeUntilHungry = compound.getInt(NBT_TIME_UNTIL_HUNGRY);
        this.timeUntilLonely = compound.getInt(NBT_TIME_UNTIL_LONELY);
        this.timeUntilStress = compound.getInt(NBT_TIME_UNTIL_STRESS);
        this.timeSinceSleep = compound.getInt(NBT_TIME_SINCE_SLEEP);
        this.inventory.fromTag(compound.getList(NBT_INVENTORY, 10), this.registryAccess());
        if (compound.contains(NBT_SLOUCH)) {
            this.setSlouch(compound.getFloat(NBT_SLOUCH));
        }
        this.setTileEntityData(compound.getCompound(NBT_TILE_ENTITY));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong(NBT_DATABASE_ID, this.getDatabaseID());
        compound.putInt(NBT_BLOCK_STATE, Block.getId(this.getBlockState()));
        compound.putInt(NBT_VISIBLE_BLOCK_STATE, Block.getId(this.getVisibleBlockState()));
        compound.putFloat(NBT_SCALE, this.getMoeScale());
        compound.putBoolean(NBT_FOLLOWING, this.isFollowing());
        if (this.isFollowing()) {
            compound.put(NBT_FOLLOW_SESSION, this.writeFollowSession());
        }
        compound.putBoolean(NBT_SITTING, this.isSitting());
        compound.putString(NBT_PLAYER_UUID, this.getPlayerUUID().toString());
        compound.putString(NBT_OWNER_UUID, this.getPlayerUUID().toString());
        compound.putString(NBT_GIVEN_NAME, this.getGivenName());
        compound.putString(NBT_GENDER, this.getGender());
        compound.putString(NBT_BLOOD_TYPE, this.getBloodType());
        compound.putString(NBT_DERE, this.getDere());
        compound.putString(NBT_ZODIAC, this.getZodiac());
        compound.putString(NBT_EMOTION, this.getEmotion());
        compound.putString(NBT_ANIMATION, this.temporaryAnimationTicks > 0 ? "DEFAULT" : this.getAnimationKey());
        compound.putFloat(NBT_FOOD_LEVEL, this.getFoodLevel());
        compound.putFloat(NBT_EXHAUSTION, this.getExhaustion());
        compound.putFloat(NBT_SATURATION, this.getSaturation());
        compound.putFloat(NBT_STRESS, this.getStress());
        compound.putFloat(NBT_RELAXATION, this.getRelaxation());
        compound.putFloat(NBT_LOYALTY, this.getLoyalty());
        compound.putFloat(NBT_AFFECTION, this.getAffection());
        compound.putFloat(NBT_SLOUCH, this.getSlouch());
        compound.putFloat(NBT_AGE, this.getAge());
        compound.putBoolean(NBT_HAS_HOME, this.hasHome());
        compound.put(NBT_HOME, this.getHome().write());
        compound.putString(NBT_ROUTINE_INTENT, this.getRoutineIntent().name());
        if (this.structureAssignment.assigned()) {
            compound.put(NBT_STRUCTURE_ASSIGNMENT, this.structureAssignment.write());
        }
        this.chores.write(compound);
        this.environmentalMemory.writeRememberedPlace(compound, NBT_REMEMBERED_PLACE);
        compound.putLong(NBT_LAST_SEEN_AT, this.getLastSeen());
        compound.putInt(NBT_TIME_UNTIL_HUNGRY, this.getTimeUntilHungry());
        compound.putInt(NBT_TIME_UNTIL_LONELY, this.getTimeUntilLonely());
        compound.putInt(NBT_TIME_UNTIL_STRESS, this.getTimeUntilStress());
        compound.putInt(NBT_TIME_SINCE_SLEEP, this.getTimeSinceSleep());
        compound.put(NBT_INVENTORY, this.inventory.createTag(this.registryAccess()));
        compound.put(NBT_TILE_ENTITY, this.getTileEntityData().copy());
    }

    private void readFollowSession(CompoundTag compound) {
        UUID playerUuid = compound.contains(NBT_FOLLOW_PLAYER_UUID)
                ? UUID.fromString(compound.getString(NBT_FOLLOW_PLAYER_UUID))
                : this.getPlayerUUID();
        PlayerMovementIntent intent = PlayerMovementIntent.FOLLOW_REQUEST;
        if (compound.contains(NBT_FOLLOW_INTENT)) {
            try {
                intent = PlayerMovementIntent.valueOf(compound.getString(NBT_FOLLOW_INTENT));
            } catch (IllegalArgumentException ignored) {
                intent = PlayerMovementIntent.FOLLOW_REQUEST;
            }
        }
        this.startFollowSession(
                playerUuid,
                intent,
                compound.getInt(NBT_FOLLOW_TICKS_REMAINING),
                compound.getBoolean(NBT_FOLLOW_CAN_CHANGE_DIMENSION));
    }

    private CompoundTag writeFollowSession() {
        CompoundTag compound = new CompoundTag();
        compound.putString(NBT_FOLLOW_PLAYER_UUID, this.followSession.playerUuid().toString());
        compound.putString(NBT_FOLLOW_INTENT, this.followSession.intent().name());
        compound.putInt(NBT_FOLLOW_TICKS_REMAINING, this.followSession.ticksRemaining());
        compound.putBoolean(NBT_FOLLOW_CAN_CHANGE_DIMENSION, this.followSession.canChangeDimension());
        return compound;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.sceneManager.tick();
            this.tickTemporaryAnimation();
            this.environmentalMemory.tick();
            this.gifts.tick();
            this.tickFollowSession();
            this.updateHungerState();
            this.updateLonelyState();
            this.updateStressState();
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
            MoeStructureCohortCoordinator.onThreatened(this);
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
            MoeStructureCohortCoordinator.onThreatened(this);
            this.setDialogueTarget(player.getUUID());
            this.triggerScene(player.isShiftKeyDown() ? SceneTrigger.SHIFT_LEFT_CLICK : SceneTrigger.LEFT_CLICK);
            return false;
        }
        boolean hurt = super.hurtServer(level, damageSource, amount * this.getBlockBuffer());
        if (hurt) {
            MoeStructureCohortCoordinator.onThreatened(this);
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
        String givenName = this.getGivenName();
        if (this.isCardinal() || givenName == null || givenName.isBlank()) {
            return this.getFamilyNameComponent();
        }
        return Component.literal(givenName);
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
        String normalized = normalize(emotion, "NORMAL", "ANGRY", "BEGGING", "CONFUSED", "CRYING", "MISCHIEVOUS", "EMBARRASSED", "HAPPY", "NORMAL", "PAINED", "PSYCHOTIC", "SCARED", "SICK", "SNOOTY", "SMITTEN", "TIRED");
        this.entityData.set(EMOTION, normalized);
        if ("BEGGING".equals(normalized)) {
            this.setTemporaryAnimationKey("BEG", 50);
        }
    }

    public String getAnimationKey() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimationKey(String animation) {
        this.clearTemporaryAnimation();
        this.setNormalizedAnimationKey(animation);
    }

    public void setTemporaryAnimationKey(String animation, int ticks) {
        if (this.hasDialogue() || ticks <= 0) {
            return;
        }
        String normalized = normalizeAnimation(animation);
        if ("DEFAULT".equals(normalized)) {
            return;
        }
        this.temporaryAnimationKey = normalized;
        this.temporaryAnimationTicks = ticks;
        this.entityData.set(ANIMATION, normalized);
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

    public MoeItemPreferences.PreferenceSignal receiveGift(ItemStack stack) {
        return this.gifts.receive(stack);
    }

    public Optional<MoeItemPreferences.PreferenceSignal> latestGiftPreferenceSignal() {
        return this.gifts.latestSignal();
    }

    public Optional<ItemStack> latestGiftItem() {
        return this.gifts.latestItem();
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
        return MoeFamilyNames.get(this.getVisibleBlockState());
    }

    public Component getFamilyNameComponent() {
        return MoeFamilyNames.component(this.getVisibleBlockState());
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
        if (dialogue != null) {
            this.setAnimationKey(dialogue.speaker().animation());
        }
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
        MoeGoals.updateActionState(this);
    }

    public boolean updateFollowSessionMovement() {
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

    public Optional<MoeAnchor> currentRoutineAnchor() {
        return MoeAnchorResolver.bestRoutineAnchor(this);
    }

    public RoutineIntent getRoutineIntent() {
        return this.routineIntent;
    }

    public void setRoutineIntent(RoutineIntent routineIntent) {
        this.routineIntent = routineIntent == null ? RoutineIntent.IDLE : routineIntent;
    }

    public MoeStructureAssignment structureAssignment() {
        return this.structureAssignment;
    }

    public void setStructureAssignment(MoeStructureAssignment assignment) {
        this.setStructureAssignment(assignment, true);
    }

    public void setStructureAssignment(MoeStructureAssignment assignment, boolean syncToDb) {
        this.structureAssignment = assignment == null ? MoeStructureAssignment.none() : assignment;
        if (syncToDb) {
            this.syncStructureAssignmentToDb();
        }
    }

    private void syncStructureAssignmentToDb() {
        if (!(this.level() instanceof ServerLevel level) || this.getDatabaseID() < 0L) {
            return;
        }
        try {
            NPC.updateStructureAssignment(BlockPartyDB.get(level), this.getDatabaseID(), this.structureAssignment);
        } catch (SQLException ignored) {
        }
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
        Optional<MoeAnchor> anchor = this.currentRoutineAnchor();
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

    public ChoreScheduler chores() {
        return this.chores;
    }

    public MoeEnvironmentalBehavior environment() {
        return this.environment;
    }

    public MoeRoutineBehavior routine() {
        return this.routine;
    }

    public MoeSocialBehavior social() {
        return this.social;
    }

    public MoeEnvironmentalRules.ShelterScore shelterScoreAt(BlockPos feetPos) {
        return this.environmentalMemory.shelterScoreAt(feetPos);
    }

    public Optional<MoeEnvironmentalObservation.Observation> observeEnvironmentNow() {
        return this.environmentalMemory.observeEnvironmentNow();
    }

    public Optional<MoeEnvironmentalObservation.Observation> latestEnvironmentalObservation() {
        return this.environmentalMemory.latestObservation();
    }

    public Optional<MoePlaceMemory.Place> observePlaceNow() {
        return this.environmentalMemory.observePlaceNow();
    }

    public Optional<MoePlaceMemory.Place> rememberedPlace() {
        return this.environmentalMemory.rememberedPlace();
    }

    public void rememberPlace(MoePlaceMemory.Place place) {
        this.environmentalMemory.rememberPlace(place);
    }

    public void clearRememberedPlace() {
        this.environmentalMemory.clearRememberedPlace();
    }

    public boolean shouldSkipGoalMovement() {
        return this.level().isClientSide || this.hasDialogue() || this.isSitting() || this.isPassenger();
    }

    public Optional<MoeSocialPlaceMemory> socialPlaceMemoryForTests() {
        return this.social.placeMemoryForTests();
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
        return MoeBlockProfile.hasWings(this.getVisibleBlockState());
    }

    public boolean hasCatFeatures() {
        return MoeBlockProfile.hasCatFeatures(this.getVisibleBlockState());
    }

    public boolean hasGlow() {
        return MoeBlockProfile.hasGlow(this.getVisibleBlockState());
    }

    public boolean ignoresVolume() {
        return MoeBlockProfile.ignoresVolume(this.getVisibleBlockState());
    }

    public boolean ignoresRain() {
        return MoeBlockProfile.ignoresRain(this.getVisibleBlockState());
    }

    public boolean ignoresDarkness() {
        return MoeBlockProfile.ignoresDarkness(this.getVisibleBlockState());
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
        MoeBlockProfile.applyIdentity(this, state);
    }

    private void applyBlockPhysicalState(BlockState state) {
        this.setMoeScale(MoeBlockProfile.ignoresVolume(state) ? 1.0F : this.getBlockVolume(state));
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
        return MoeBlockProfile.volume(state);
    }

    public float getBlockBuffer() {
        return MoeBlockProfile.blockBuffer(this);
    }

    @Override
    public float getVoicePitch() {
        float pitch = (super.getVoicePitch() + this.getBlockBuffer() + 0.6F) / 2.0F;
        return pitch + (1.0F - this.getMoeScale());
    }

    @Override
    public boolean fireImmune() {
        return MoeBlockProfile.fireImmune(this);
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

    private void tickTemporaryAnimation() {
        if (this.temporaryAnimationTicks <= 0) {
            return;
        }
        if (this.hasDialogue()) {
            this.clearTemporaryAnimation();
            return;
        }
        --this.temporaryAnimationTicks;
        if (this.temporaryAnimationTicks <= 0) {
            if (this.temporaryAnimationKey.equals(this.getAnimationKey())) {
                this.setNormalizedAnimationKey("DEFAULT");
            }
            this.clearTemporaryAnimation();
        }
    }

    private void clearTemporaryAnimation() {
        this.temporaryAnimationTicks = 0;
        this.temporaryAnimationKey = "DEFAULT";
    }

    private void setNormalizedAnimationKey(String animation) {
        this.entityData.set(ANIMATION, normalizeAnimation(animation));
    }

    private static String normalizeAnimation(String animation) {
        return normalize(animation, "DEFAULT", "DEFAULT", "AWE", "BEG", "HAPPY_DANCE", "LOOK_AROUND", "SHIVER", "YEARBOOK", "WAVE");
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
