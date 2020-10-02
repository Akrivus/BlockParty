package moe.blocks.mod.data.dating;

import moe.blocks.mod.entity.ai.automata.ReactiveState;
import moe.blocks.mod.util.Trans;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Relationship {
    protected final Map<Interactions, Integer> interactions = new HashMap<>();
    protected Phases phase = Phases.INTRODUCTION;
    protected UUID playerUUID;
    protected float love;
    protected int timeSinceInteraction;

    public Relationship(UUID uuid) {
        this.playerUUID = uuid;
    }

    public Relationship(INBT compound) {
        this((CompoundNBT) compound);
    }

    public Relationship(CompoundNBT compound) {
        this.phase = Phases.valueOf(compound.getString("Phase"));
        this.love = compound.getFloat("Love");
        this.timeSinceInteraction = compound.getInt("TimeSinceInteraction");
        for (Interactions interaction : Interactions.values()) {
            if (compound.contains(interaction.getKey())) { this.interactions.put(interaction, compound.getInt(interaction.getKey())); }
        }
    }

    public boolean can(Actions action) {
        return action.inRange(this.love);
    }

    public PlayerEntity getPlayer(World world) {
        return world.getPlayerByUuid(this.playerUUID);
    }

    public ReactiveState getReaction(Interactions interaction) {
        if (this.interactions.getOrDefault(interaction, -1) < 0) { this.addLove(interaction.getLove()); }
        this.interactions.put(interaction, interaction.getCooldown());
        return interaction.reaction.state;
    }

    public void addLove(float love) {
        this.setLove(this.love + love);
    }

    public boolean isUUID(UUID uuid) {
        return this.playerUUID.equals(uuid);
    }

    public void tick() {
        this.interactions.forEach((interaction, timeout) -> --timeout);
        if (++this.timeSinceInteraction > 24000) {
            this.timeSinceInteraction = 0;
            this.love -= this.getPhase().getDecay();
            if (this.love < 0.0F) { this.love = 0.0F; }
            if (this.love > 20.0F) {
                this.love = 20.0F;
            }
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putString("Phase", this.getPhase().name());
        compound.putFloat("Love", this.getLove());
        compound.putInt("TimeSinceInteraction", this.timeSinceInteraction);
        this.interactions.forEach((interaction, timeout) -> compound.putInt(interaction.getKey(), timeout));
        return compound;
    }

    public Phases getPhase() {
        return this.phase;
    }

    public void setPhase(Phases phase) {
        this.phase = phase;
    }

    public float getLove() {
        return this.love;
    }

    public void setLove(float love) {
        this.love = Math.max(Math.min(love, 20.0F), 0.0F);
    }

    public enum Phases {
        INTRODUCTION(0.1F), INFATUATION(1.0F), CONFUSION(2.0F), CONFESSION(1.0F);

        private final float decay;

        Phases(float decay) {
            this.decay = decay;
        }

        public float getDecay() {
            return this.decay;
        }
    }

    public enum Status {
        SINGLE, TAKEN, DEAD;

        @Override
        public String toString() {
            return Trans.late(String.format("debug.moeblocks.status.%s", this.name().toLowerCase()));
        }
    }

    public enum Actions {
        FOLLOW(2.0F), FIGHT(3.0F), TELEPORT(4.0F), LOOK_IN_BRA(6.0F);

        private final float min;

        Actions(float min) {
            this.min = min;
        }

        public boolean inRange(float love) {
            return this.min <= love;
        }
    }
}
