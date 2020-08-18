package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.util.VoiceLines;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

import java.util.List;
import java.util.UUID;

public class Relationship extends AbstractState implements Comparable<Relationship> {
    protected UUID uuid;
    protected float affection;
    protected float trust;
    private int timeSinceSeen;
    private int timeUntilCheck;

    public Relationship() {
        this(null);
    }

    public Relationship(UUID uuid) {
        this(uuid, 0, 0);
    }

    public Relationship(UUID uuid, float affection, float trust) {
        this.uuid = uuid;
        this.affection = affection;
        this.trust = trust;
    }

    public static List<Relationship> sort(List<Relationship> list) {
        list.sort(Relationship::compareTo);
        return list;
    }

    @Override
    public int compareTo(Relationship other) {
        return Float.compare(this.getLoyalty(), other.getLoyalty());
    }

    public float getLoyalty() {
        return this.trust - this.entity.getStressStats().getStress() + this.affection;
    }

    @Override
    public void start() {
        this.entity.getRelationships().set(this.getUUID(), this);
    }

    @Override
    public void tick() {
        if (--this.timeUntilCheck < 0) {
            this.timeUntilCheck = 20;
            LivingEntity host = this.getHost();
            if (host != null && host.getDistance(this.entity) < 16.0F) {
                this.entity.getStressStats().addStressSilently(this.getAnxiety() * -1.0F);
                if (this.timeSinceSeen > 300) {
                    this.timeSinceSeen = 0;
                    this.onHello(host);
                    this.addAffection(2.0F);
                } else if (this.entity.isBeingWatchedBy(host)) {
                    this.onStare(host);
                    this.addAffection(0.1F);
                } else if (host.getDistance(this.entity) < 4.0F) {
                    this.addTrust(0.005F);
                } else {
                    this.addTrust(0.001F);
                }
            } else {
                ++this.timeSinceSeen;
                this.entity.getStressStats().addStressSilently(this.getAnxiety());
                this.addTrust(-0.001F);
            }
        }
    }

    public float getAnxiety() {
        return (this.getAffection() / 20.0F) / this.entity.getRelationships().size() * 0.001F;
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.timeSinceSeen = compound.getInt("TimeSinceSeen");
        this.timeUntilCheck = compound.getInt("TimeUntilCheck");
        this.uuid = compound.getUniqueId("Host");
        this.affection = compound.getFloat("Affection");
        this.trust = compound.getFloat("Trust");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("TimeSinceSeen", this.timeSinceSeen);
        compound.putInt("TimeUntilCheck", this.timeUntilCheck);
        compound.putUniqueId("Host", this.uuid);
        compound.putFloat("Affection", this.affection);
        compound.putFloat("Trust", this.trust);
    }

    @Override
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        return false;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (hand == Hand.MAIN_HAND && player.getUniqueID().equals(this.getUUID()) && this.canFollow()) {
            if (player.isSneaking()) {
                if (stack.getItem().isIn(ItemsMoe.Tags.EQUIPPABLES)) {
                    if (this.entity.tryEquipItem(stack.copy())) {
                        this.entity.playSound(VoiceLines.THANK_YOU.get(this.entity));
                        this.entity.getRelationships().resetGiftTimer();
                        float value = this.entity.getDere().getGiftValue(stack);
                        if (stack.getItem().isIn(ItemsMoe.Tags.GIFTABLES)) {
                            this.addAffection(value);
                        } else {
                            this.addTrust(value);
                        }
                    } else {
                        this.entity.playSound(VoiceLines.NO.get(this.entity));
                    }
                    return true;
                } else if (stack.isEmpty()) {
                    this.entity.playSound(VoiceLines.HURT.get(this.entity));
                    for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                        ItemStack drop = this.entity.getItemStackFromSlot(slot);
                        this.entity.entityDropItem(drop.split(drop.getCount()));
                    }
                    return true;
                }
                return false;
            }
            this.entity.playSound(VoiceLines.YES.get(this.entity));
            this.entity.setFollowTarget(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    public boolean canFollow() {
        return this.getLoyalty() > 0;
    }

    public void addAffection(float affection) {
        this.affection = Math.max(Math.min(this.affection + affection, 20.0F), -5.0F);
    }

    public void addTrust(float trust) {
        this.trust = Math.max(Math.min(this.trust + trust, 20.0F), -5.0F);
    }

    public boolean canDoChoresFor() {
        return this.getLoyalty() > 4;
    }

    public LivingEntity getHost() {
        return this.entity.getEntityFromUUID(this.getUUID());
    }

    public void onHello(LivingEntity host) {
        this.entity.getDere().onHello(host, this);
    }

    public void onStare(LivingEntity host) {
        this.entity.getDere().onStare(host, this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public float getAffection() {
        return this.affection;
    }

    public float getTrust() {
        return this.trust;
    }

    public float getDistance() {
        return 3.0F / (this.getLoyalty() / 20.0F);
    }

    public boolean canHate() {
        return this.getLoyalty() < 0;
    }

    public boolean canFightAlongside() {
        return this.getLoyalty() > 8;
    }

    public boolean canDefend() {
        return this.getLoyalty() > 12;
    }

    public boolean canDieFor() {
        return this.getLoyalty() > 16;
    }

    public boolean isPlayer() {
        return this.getPlayer() != null;
    }

    public PlayerEntity getPlayer() {
        LivingEntity entity = this.getHost();
        if (entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        } else {
            return null;
        }
    }
}
