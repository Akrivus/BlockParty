package block_party.entities;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.NPC;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
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
    private Dialogue dialogue;
    private Response response;
    private boolean guiPreview;
    private UUID dialogueTarget = new UUID(0L, 0L);
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
        this.setFollowing(compound.getBoolean("Following"));
        this.setSitting(compound.getBoolean("Sitting"));
        if (compound.contains("OwnerUUID")) {
            this.setPlayerUUID(UUID.fromString(compound.getString("OwnerUUID")));
        }
        if (compound.contains("PlayerUUID")) {
            this.setPlayerUUID(UUID.fromString(compound.getString("PlayerUUID")));
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
        compound.putLong("LastSeenAt", this.getLastSeen());
        compound.putInt("TimeUntilHungry", this.getTimeUntilHungry());
        compound.putInt("TimeUntilLonely", this.getTimeUntilLonely());
        compound.putInt("TimeUntilStress", this.getTimeUntilStress());
        compound.putInt("TimeSinceSleep", this.getTimeSinceSleep());
        compound.put("Inventory", this.inventory.createTag(this.registryAccess()));
        compound.put("TileEntity", this.getTileEntityData().copy());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.sceneManager.tick();
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
        this.entityData.set(FOLLOWING, following);
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
