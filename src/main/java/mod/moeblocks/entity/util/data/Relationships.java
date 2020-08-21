package mod.moeblocks.entity.util.data;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.Relationship;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        ItemStack stack = this.entity.getHeldItem(Hand.OFF_HAND);
        this.hasGift = this.entity.getDere().getGiftValue(stack) > 0 && !stack.isFood();
        if (this.entity.isLocal() && this.hasGift && --this.timeUntilGiftReset < 0) {
            this.entity.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
        }
        this.iterate(relationship -> {
            relationship.tick();
            return true;
        });
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
        this.iterate(relationship -> {
            CompoundNBT tag = new CompoundNBT();
            relationship.write(tag);
            list.add(tag);
            return true;
        });
        compound.put("Relationships", list);
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.iterate(relationship -> {
            relationship.onDeath(cause);
            return true;
        });
    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        return this.iterate(relationship -> relationship.onDamage(cause, amount));
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        return this.get(player).onInteract(player, stack, hand);
    }

    @Override
    public boolean isArmed() {
        return this.iterate(Relationship::isArmed);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.get(target).canAttack();
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

    public boolean iterate(Predicate<Relationship> function) {
        boolean result = false;
        for (Relationship relationship : this) {
            result |= function.test(relationship);
        }
        return result;
    }

    @Override
    public Iterator<Relationship> iterator() {
        return this.relationships.values().iterator();
    }

    public void set(UUID uuid, Relationship relationship) {
        this.relationships.put(uuid, relationship);
    }

    public int size() {
        return this.relationships.size();
    }

    public boolean isReadyForGifts() {
        return !this.hasGift || this.timeUntilGiftReset < 0;
    }

    public boolean isFavorite(LivingEntity target) {
        List<Relationship> list = this.relationships.values().stream().sorted(Relationship::compareTo).collect(Collectors.toList());
        return this.get(target).getLoyalty() == (list.isEmpty() ? 0 : list.get(0).getLoyalty());
    }
}