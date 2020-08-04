package mod.moeblocks.entity.ai;

import mod.moeblocks.register.ItemsMoe;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.UUID;

public class Relationship extends AbstractState implements Comparable<Relationship> {
    protected UUID uuid;
    protected int affection;
    protected int trust;
    private int timeSinceSeen = 6001;
    private int timeUntilCheck = -1;

    public Relationship() {
        this(null);
    }

    public Relationship(UUID uuid) {
        this(uuid, 0, 0);
    }

    public Relationship(UUID uuid, int affection, int trust) {
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
        return Integer.compare(this.getLoyalty(), other.getLoyalty());
    }

    public int getLoyalty() {
        return this.trust - this.moe.getStressStats().getStress() + this.affection;
    }

    @Override
    public void start() {
        this.moe.getRelationships().set(this.getUUID(), this);
    }

    @Override
    public void tick() {
        if (this.timeUntilCheck < 0) {
            this.timeUntilCheck = 200;
            LivingEntity host = this.getHost();
            if (host != null && host.getDistance(this.moe) < 16.0F) {
                if (this.timeSinceSeen > 6000) {
                    this.moe.getDere().onHello(host, this);
                    this.trust += 4;
                } else if (this.moe.isBeingWatchedBy(host)) {
                    this.moe.getDere().onStare(host, this);
                    this.trust += 3;
                } else if (host.getDistance(this.moe) < 4.0F) {
                    this.trust += 2;
                } else {
                    this.trust += 1;
                }
            } else if (this.canDoChoresFor()) {
                this.trust -= 1;
            }
        } else {
            ++this.timeSinceSeen;
            --this.timeUntilCheck;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.timeSinceSeen = compound.getInt("TimeSinceSeen");
        this.timeUntilCheck = compound.getInt("TimeUntilCheck");
        this.uuid = compound.getUniqueId("Host");
        this.affection = compound.getInt("Affection");
        this.trust = compound.getInt("Trust");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("TimeSinceSeen", this.timeSinceSeen);
        compound.putInt("TimeUntilCheck", this.timeUntilCheck);
        compound.putUniqueId("Host", this.uuid);
        compound.putInt("Affection", this.affection);
        compound.putInt("Trust", this.trust);
    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        return false;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (hand == Hand.MAIN_HAND && player.getUniqueID().equals(this.getUUID()) && this.canFollow()) {
            if (stack.getItem().isIn(ItemsMoe.Tags.EQUIPPABLES) && !player.isSneaking()) {
                this.moe.playSound(SoundEventsMoe.THANK_YOU.get());
                return this.moe.tryEquipItem(stack);
            }
            if (player.equals(this.moe.getFollowTarget())) {
                this.moe.playSound(SoundEventsMoe.NO.get());
                this.moe.setFollowTarget(null);
            } else {
                this.moe.playSound(SoundEventsMoe.YES.get());
                this.moe.setFollowTarget(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    @Override
    public Enum<?> getKey() {
        return null;
    }

    public boolean canFollow() {
        return this.getLoyalty() > 20;
    }

    public boolean canDoChoresFor() {
        return this.getLoyalty() > 40;
    }

    public LivingEntity getHost() {
        Entity entity = this.moe.world.getServer().getWorld(this.moe.dimension).getEntityByUuid(this.getUUID());
        if (entity instanceof LivingEntity) {
            return (LivingEntity) entity;
        } else {
            return null;
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void addAffection(int affection) {
        this.affection += affection;
    }

    public int getAffection() {
        return this.affection;
    }

    public void addTrust(int trust) {
        this.trust += trust;
    }

    public int getTrust() {
        return this.trust;
    }

    public float getDistance() {
        return 3.0F / (this.getLoyalty() / 100.0F);
    }

    public boolean canHate() {
        return this.getLoyalty() < 0;
    }

    public boolean canFightAlongside() {
        return this.getLoyalty() > 60;
    }

    public boolean canDefend() {
        return this.getLoyalty() > 80;
    }

    public boolean canDieFor() {
        return this.getLoyalty() > 100;
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
