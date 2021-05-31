package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.ICondition;
import moeblocks.automata.IState;
import moeblocks.automata.trait.BloodType;
import moeblocks.automata.trait.Dere;
import moeblocks.automata.trait.Emotion;
import moeblocks.automata.trait.Gender;
import moeblocks.client.animation.AbstractAnimation;
import moeblocks.client.animation.Animation;
import moeblocks.data.AbstractNPC;
import moeblocks.data.IModelEntity;
import moeblocks.init.MoeOverrides;
import moeblocks.util.DimBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractNPCEntity<NPC extends AbstractNPC> extends CreatureEntity implements IInventoryChangedListener, IModelEntity<NPC>, INamedContainerProvider {
    public static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> PLAYER_UUID = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DATABASE_ID = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> GENDER = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> GIVEN_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<Float> FULLNESS = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> EXHAUSTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> SATURATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> STRESS = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> RELAXATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> LOYALTY = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> AFFECTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> SLOUCH = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Float> AGE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.FLOAT);
    public final Inventory inventory = new Inventory(36);
    public final Automaton automaton;
    private final LazyOptional<?> itemHandler = LazyOptional.of(() -> new InvWrapper(this.inventory));
    private final Queue<Consumer<AbstractNPCEntity>> nextTickOps;
    private AbstractAnimation animation;
    private int timeUntilHungry;
    private int timeUntilLonely;
    private int timeUntilStress;
    private int timeSinceSlept;
    private long lastSeen;

    protected AbstractNPCEntity(EntityType<? extends AbstractNPCEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.nextTickOps = new LinkedList<>();
        this.automaton = new Automaton(this);
    }

    @Override
    public void registerData() {
        this.dataManager.register(FOLLOWING, false);
        this.dataManager.register(PLAYER_UUID, "00000000-0000-0000-0000-000000000000");
        this.dataManager.register(DATABASE_ID, "00000000-0000-0000-0000-000000000000");
        this.dataManager.register(BLOOD_TYPE, BloodType.O.getValue());
        this.dataManager.register(DERE, Dere.NYANDERE.getValue());
        this.dataManager.register(EMOTION, Emotion.NORMAL.getValue());
        this.dataManager.register(GENDER, Gender.FEMININE.getValue());
        this.dataManager.register(ANIMATION, Animation.DEFAULT.name());
        this.dataManager.register(GIVEN_NAME, "Tokumei");
        this.dataManager.register(SLOUCH, 0.0F);
        this.dataManager.register(FULLNESS, 20.0F);
        this.dataManager.register(EXHAUSTION, 0.0F);
        this.dataManager.register(SATURATION, 6.0F);
        this.dataManager.register(STRESS, 0.0F);
        this.dataManager.register(RELAXATION, 0.0F);
        this.dataManager.register(LOYALTY, 6.0F);
        this.dataManager.register(AFFECTION, 0.0F);
        this.dataManager.register(AGE, 0.0F);
        super.registerData();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putUniqueId("DatabaseID", this.getDatabaseID());
        compound.putBoolean("Following", this.isFollowing());
        compound.put("Inventory", this.inventory.write());
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLonely", this.timeUntilLonely);
        compound.putInt("TimeUntilStress", this.timeUntilStress);
        compound.putInt("TimeSinceSlept", this.timeSinceSlept);
        this.automaton.write(compound);
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setDatabaseID(compound.getUniqueId("DatabaseID"));
        this.setFollowing(compound.getBoolean("Following"));
        this.inventory.read(compound.getList("Inventory", Constants.NBT.TAG_COMPOUND));
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSlept = compound.getInt("TimeSinceSlept");
        this.automaton.read(compound);
        this.getRow().load(this);
        super.readAdditional(compound);
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (stack.isEmpty()) { continue; }
            this.entityDropItem(stack);
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
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

    @Override
    public void setRawPosition(double x, double y, double z) {
        super.setRawPosition(x, y, z);
        if (this.isRemote() || this.ticksExisted < 1) { return; }
        if (this.prevPosX != x || this.prevPosY != y || this.prevPosZ != z) {
            this.getRow().update(this);
        }
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) { return false; }

    @Override
    public void updateAITasks() {
        Consumer<AbstractNPCEntity> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
    }

    @Override
    protected float getDropChance(EquipmentSlotType slot) { return 0.0F; }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) { return; }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData data, CompoundNBT compound) {
        this.setGivenName(this.getGender().getUniqueName(this.world));
        this.setBloodType(this.getBloodType().weigh(this.rand));
        return super.onInitialSpawn(world, difficulty, reason, data, compound);
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vector, Hand hand) {
        return this.onInteract(player, player.getHeldItem(hand), hand);
    }

    public void jump() {
        super.jump();
    }

    public void setState(IState state, IState... states) {
        this.automaton.setState(this, state, states);
    }

    public abstract ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand);

    public boolean isLocal() {
        return !this.isRemote();
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public abstract void updateHungerState();

    public abstract void updateLonelyState();

    public abstract void updateStressState();

    public abstract void updateActionState();

    public abstract void updateSleepState();

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
        return this.dataManager.get(FOLLOWING);
    }

    public void setFollowing(boolean following) {
        this.dataManager.set(FOLLOWING, following);
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(this.dataManager.get(PLAYER_UUID));
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.dataManager.set(PLAYER_UUID, playerUUID.toString());
    }

    public UUID getDatabaseID() {
        return UUID.fromString(this.dataManager.get(DATABASE_ID));
    }

    public void setDatabaseID(UUID uuid) {
        this.dataManager.set(DATABASE_ID, uuid.toString());
    }

    public String getGivenName() {
        return this.dataManager.get(GIVEN_NAME);
    }

    public void setGivenName(String name) {
        this.dataManager.set(GIVEN_NAME, name);
    }

    public float getFullness() {
        return this.dataManager.get(FULLNESS);
    }

    public void setFullness(float fullness) {
        this.dataManager.set(FULLNESS, fullness);
    }

    public float getExhaustion() {
        return this.dataManager.get(EXHAUSTION);
    }

    public void setExhaustion(float exhaustion) {
        this.dataManager.set(EXHAUSTION, exhaustion);
    }

    public float getSaturation() {
        return this.dataManager.get(SATURATION);
    }

    public void setSaturation(float saturation) {
        this.dataManager.set(SATURATION, saturation);
    }

    public float getStress() {
        return this.dataManager.get(STRESS);
    }

    public void setStress(float stress) {
        this.dataManager.set(STRESS, stress);
    }

    public float getRelaxation() {
        return this.dataManager.get(RELAXATION);
    }

    public void setRelaxation(float relaxation) {
        this.dataManager.set(RELAXATION, relaxation);
    }

    public float getLoyalty() {
        return this.dataManager.get(LOYALTY);
    }

    public void setLoyalty(float loyalty) {
        this.dataManager.set(LOYALTY, loyalty);
    }

    public float getAffection() {
        return this.dataManager.get(AFFECTION);
    }

    public void setAffection(float affection) {
        this.dataManager.set(AFFECTION, affection);
    }

    public float getSlouch() {
        return this.dataManager.get(SLOUCH);
    }

    public void setSlouch(float slouch) {
        this.dataManager.set(SLOUCH, slouch);
    }

    public float getAge() {
        return this.dataManager.get(AGE);
    }

    public void setAge(float age) {
        this.dataManager.set(AGE, age);
    }

    public BloodType getBloodType() {
        return BloodType.O.fromValue(this.dataManager.get(BLOOD_TYPE));
    }

    public void setBloodType(BloodType bloodType) {
        this.dataManager.set(BLOOD_TYPE, bloodType.getValue());
    }

    public Dere getDere() {
        return Dere.NYANDERE.fromValue(this.dataManager.get(DERE));
    }

    public void setDere(Dere dere) {
        this.dataManager.set(DERE, dere.getValue());
    }

    public Emotion getEmotion() {
        return Emotion.NORMAL.fromValue(this.dataManager.get(GENDER));
    }

    public void setEmotion(Emotion emotion) {
        this.dataManager.set(EMOTION, emotion.getValue());
    }

    public Gender getGender() {
        return Gender.FEMININE.fromValue(this.dataManager.get(GENDER));
    }

    public void setGender(Gender gender) {
        this.dataManager.set(GENDER, gender.getValue());
    }

    public ITextComponent getProfessionName() {
        return new TranslationTextComponent("entity.moeblocks.profession", this.getGivenName(), this.getBlockName());
    }

    public abstract String getBlockName();

    @Override
    public void copyDataFromOld(Entity entity) {
        super.copyDataFromOld(entity);
        this.setUniqueId(UUID.randomUUID());
    }

    public ITextComponent getCustomName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getGivenName(), this.getHonorific());
    }

    public abstract String getHonorific();

    @Override
    public boolean hasCustomName() { return true; }

    @Override
    protected void dropInventory() {
        for (int slot = 0; slot < 36; ++slot) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) { continue; }
            this.entityDropItem(stack);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) { return this.itemHandler.cast(); }
        return super.getCapability(capability, facing);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.itemHandler.invalidate();
    }

    @Override
    public void onInventoryChanged(IInventory inventory) {
        this.resetSlouch();
    }

    public void resetSlouch() {
        this.setSlouch(this.recalcSlouch());
    }

    public float recalcSlouch() {
        float size = 0.0F;
        for (int i = 0; i < 36; ++i) {
            if (inventory.getStackInSlot(i).isEmpty()) { continue; }
            size += 0.0277777778F;
        }
        return size;
    }

    public boolean isBeingLookedThrough() {
        if (!this.isPlayerBusy()) { return false; }
        Container container = this.getPlayer().openContainer;
        if (container instanceof ChestContainer) {
            IInventory inventory = ((ChestContainer) container).getLowerChestInventory();
            return inventory.equals(this.inventory);
        }
        return false;
    }

    public boolean isPlayerBusy() {
        return this.isPlayerOnline() && this.getPlayer().openContainer != this.getPlayer().container;
    }

    public boolean isPlayerOnline() {
        return this.getPlayer() != null;
    }

    public boolean isPlayer(Entity entity) {
        return entity != null && this.getPlayerUUID().equals(entity.getUniqueID());
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.world.getDimensionKey(), this.getPosition());
    }

    public void say(String key, Object... params) {
        this.world.getPlayers().forEach((player) -> {
            if (player.getDistance(this) < 8.0D) { this.say(player, key, params); }
        });
    }

    public void say(PlayerEntity player, String key, Object... params) {
        this.say(player, new TranslationTextComponent(key, params));
    }

    public void say(PlayerEntity player, ITextComponent component) {
        player.sendMessage(component, player.getUniqueID());
    }

    public AbstractNPCEntity teleport(ServerWorld world, ITeleporter teleporter) {
        Entity entity = this.changeDimension(world, teleporter);
        if (entity instanceof AbstractNPCEntity) {
            return this.onTeleport((AbstractNPCEntity) entity);
        } else {
            return this;
        }
    }

    public abstract AbstractNPCEntity onTeleport(AbstractNPCEntity npc);

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

    public boolean openChestFor(PlayerEntity player) {
        player.openContainer(this);
        return true;
    }

    public void addNextTickOp(Consumer<AbstractNPCEntity> op) {
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
        return Animation.valueOf(this.dataManager.get(ANIMATION));
    }

    public void setAnimationKey(Animation animation) {
        this.dataManager.set(ANIMATION, animation.name());
    }

    public PlayerEntity getPlayer() {
        return this.world.getPlayerByUuid(this.getPlayerUUID());
    }

    public void setPlayer(PlayerEntity player) {
        this.setPlayerUUID(player.getUniqueID());
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public boolean hasRow() {
        return this.isLocal() && this.getRow() != null;
    }

    @Override
    public boolean claim(PlayerEntity player) {
        if (IModelEntity.super.claim(player)) { this.getData().addTo(player, this.getDatabaseID()); }
        return this.isLocal();
    }

    public boolean is(ICondition condition) {
        return condition.isTrue(this);
    }

    public BlockState getExternalBlockState() {
        return MoeOverrides.get(this.getInternalBlockState().getBlock()).getDefaultState();
    }

    public abstract BlockState getInternalBlockState();

    public boolean is(ITag<Block> tag) {
        return this.getExternalBlockState().isIn(tag);
    }

    public float getAgeInYears() {
        return this.getBaseAge() + this.getAge();
    }

    public abstract int getBaseAge();

    public abstract boolean openSpecialMenuFor(PlayerEntity player);

    public abstract void setBlockState(BlockState state);

    public abstract boolean isSitting();

    public boolean isTimeBetween(int start, int end) {
        long time = this.world.getDayTime();
        return start <= time && time <= end;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.setAnimation(Animation.get(this.dataManager.get(ANIMATION))); }
        super.notifyDataManagerChange(key);
    }

    public abstract void onMention(PlayerEntity player, String message);
}
