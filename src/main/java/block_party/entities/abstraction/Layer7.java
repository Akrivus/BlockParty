package block_party.entities.abstraction;

import block_party.client.animation.AbstractAnimation;
import block_party.client.animation.Animation;
import block_party.entities.BlockPartyNPC;
import block_party.messages.SOpenDialogue;
import block_party.registry.CustomMessenger;
import block_party.scene.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Abstraction layer 7: scene management and animation.
 */
public abstract class Layer7 extends Layer6 {
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Layer7.class, EntityDataSerializers.STRING);
    public final SceneManager sceneManager;
    private AbstractAnimation animation = Animation.DEFAULT.get();
    private Dialogue dialogue;
    private int timeUntilHungry;
    private int timeUntilLonely;
    private int timeUntilStress;
    private int timeSinceSleep;
    private long lastSeen;

    public Layer7(EntityType<? extends BlockPartyNPC> type, Level level) {
        super(type, level);
        this.sceneManager = new SceneManager(this.cast());
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(ANIMATION, Animation.DEFAULT.name());
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLonely", this.timeUntilLonely);
        compound.putInt("TimeUntilStress", this.timeUntilStress);
        compound.putInt("TimeSinceSleep", this.timeSinceSleep);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLonely = compound.getInt("TimeUntilLonely");
        this.timeUntilStress = compound.getInt("TimeUntilStress");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ANIMATION.equals(key)) { this.setAnimation(Animation.DEFAULT.fromValue(this.entityData.get(ANIMATION))); }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.sceneManager.tick(this.cast());
        if (this.isLocal()) {
            this.updateHungerState();
            this.updateLonelyState();
            this.updateStressState();
            this.updateActionState();
            this.updateSleepState();
        } else {
            this.animation.tick(this.cast());
        }
    }

    @Override
    public void customServerAiStep() {
        if (this.random.nextInt(20) == 0) { this.sceneManager.trigger(SceneTrigger.RANDOM_TICK); }
        if (this.isBeingLookedAt()) { this.sceneManager.trigger(SceneTrigger.STARE); }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vector, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) { return InteractionResult.PASS; }
        if (this.isPlayer(player)) {
            this.sceneManager.trigger(player.isCrouching() ? SceneTrigger.SHIFT_RIGHT_CLICK : SceneTrigger.RIGHT_CLICK);
        }
        return InteractionResult.SUCCESS;
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

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean attacked = this.doHurtTarget(target);
        if (attacked) { this.sceneManager.trigger(SceneTrigger.ATTACK); }
        return attacked;
    }

    public boolean is(ITrait trait) {
        return trait.isSharedWith(this.cast());
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

    public void setDialogue(Dialogue dialogue) {
        if (dialogue == null) { return; }
        Dialogue.Model message = new Dialogue.Model(dialogue.getText(), dialogue.isTooltip(), dialogue.getSpeaker(), this.getSpeakSound());
        for (Response.Icon icon : dialogue.responses.keySet()) {
            message.add(icon, dialogue.responses.get(icon).getText());
        }
        CustomMessenger.send(this.getPlayer(), new SOpenDialogue(this.getRow(), message));
        this.dialogue = dialogue;
    }

    public void setResponse(Response.Icon response) {
        if (this.dialogue != null) { this.dialogue.setResponse(response); }
    }

    public Animation getAnimationKey() {
        return Animation.DEFAULT.fromKey(this.entityData.get(ANIMATION));
    }

    public void setAnimationKey(Animation animation) {
        this.entityData.set(ANIMATION, animation.name());
    }

    public AbstractAnimation getAnimation() {
        return this.animation;
    }

    public void setAnimation(AbstractAnimation animation) {
        this.animation = animation;
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

    public SoundEvent getSpeakSound() {
        return null;
    }
}
