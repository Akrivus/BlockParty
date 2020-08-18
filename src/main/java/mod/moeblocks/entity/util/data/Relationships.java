package mod.moeblocks.entity.util.data;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class Relationships extends AbstractState implements Iterable<Relationship> {
    private final HashMap<UUID, Relationship> relationships = new HashMap<>();
    private int timeUntilGiftReset;
    private boolean hasGift;

    public void resetGiftTimer() {
        this.timeUntilGiftReset = 6000;
        this.hasGift = true;
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        if (this.hasGift && --this.timeUntilGiftReset < 0) {
            this.hasGift = false;
            if (this.entity.getHeldItem(Hand.OFF_HAND).getItem().isIn(ItemsMoe.Tags.GIFTABLES)) {
                this.entity.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.timeUntilGiftReset = compound.getInt("TimeUntilGiftReset");
        this.hasGift = compound.getBoolean("HasGift");
        ListNBT list = compound.getList("Relationships", 10);
        list.forEach(tag -> {
            Relationship relationship = new Relationship();
            relationship.read((CompoundNBT) tag);
            relationship.start(this.entity);
        });
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("TimeUntilGiftReset", this.timeUntilGiftReset);
        compound.putBoolean("HasGift", this.hasGift);
        ListNBT list = new ListNBT();
        Iterator<Relationship> it = this.iterator();
        while (it.hasNext()) {
            CompoundNBT tag = new CompoundNBT();
            it.next().write(tag);
            list.add(tag);
        }
        compound.put("Relationships", list);
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
        Iterator<Relationship> it = this.iterator();
        while (it.hasNext()) {
            if (it.next().onInteract(player, stack, hand)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    @Override
    public Iterator<Relationship> iterator() {
        return this.relationships.values().iterator();
    }

    public Relationship get(LivingEntity entity) {
        return this.get(entity.getUniqueID());
    }

    public Relationship get(UUID uuid) {
        Relationship relationship = this.relationships.get(uuid);
        if (relationship == null) {
            relationship = new Relationship(uuid);
            relationship.start(this.entity);
        }
        return relationship;
    }

    public void set(UUID uuid, Relationship relationship) {
        this.relationships.put(uuid, relationship);
    }

    public int size() {
        return this.relationships.size();
    }
}