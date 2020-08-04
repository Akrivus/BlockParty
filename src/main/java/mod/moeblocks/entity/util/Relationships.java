package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.ai.Relationship;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class Relationships extends AbstractState implements Iterable<Relationship> {
    private HashMap<UUID, Relationship> relationships = new HashMap<>();

    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        ListNBT list = compound.getList("Relationships", 10);
        list.forEach(tag -> {
            Relationship relationship = new Relationship();
            relationship.read((CompoundNBT) tag);
            relationship.start(this.moe);
        });
    }

    @Override
    public void write(CompoundNBT compound) {
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
    public DataStats getKey() {
        return DataStats.RELATIONSHIP;
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
            relationship.start(this.moe);
        }
        return relationship;
    }

    public void set(UUID uuid, Relationship relationship) {
        this.relationships.put(uuid, relationship);
    }
}