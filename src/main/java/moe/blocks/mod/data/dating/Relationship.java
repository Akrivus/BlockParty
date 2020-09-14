package moe.blocks.mod.data.dating;

import moe.blocks.mod.entity.ai.automata.ReactiveState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;

import java.util.*;

public class Relationship {
    protected final Map<Interactions, Integer> interactions = new HashMap<>();
    protected final List<Relationship> rivals = new ArrayList<>();
    protected UUID playerUUID;
    protected Phases phase;
    protected float affection;
    protected float trust;
    protected int timeSinceInteraction;

    public Relationship(UUID uuid) {
        this.playerUUID = uuid;
    }

    public Relationship(INBT compound) {
        this((CompoundNBT) compound);
    }

    public Relationship(CompoundNBT compound) {
        this.phase = Phases.valueOf(compound.getString("Phase"));
    }

    public CompoundNBT write(CompoundNBT compound) {
        return compound;
    }

    public void tick() {
        this.interactions.forEach((interaction, timeout) -> --timeout);
        if (++this.timeSinceInteraction > 24000) {
            this.timeSinceInteraction = 0;
            this.affection -= this.getPhase().getDecay();
            if (this.affection < 0.0F) { this.affection = 0.0F; }
            if (this.affection >= 20.0F) {
                this.affection = 20.0F;
            }
        }
    }

    public Phases getPhase() {
        return this.phase;
    }

    public void setPhase(Phases phase) {
        this.phase = phase;
    }

    public float getAffection() {
        return this.affection;
    }

    public float getTrust() {
        return this.trust;
    }

    public boolean isPlayer(PlayerEntity entity) {
        return this.playerUUID.equals(entity.getUniqueID());
    }

    public PlayerEntity getPlayer(World world) {
        return world.getPlayerByUuid(this.playerUUID);
    }

    public boolean can(Actions action) {
        return action.inRange(this.affection);
    }

    public ReactiveState getReaction(Interactions interaction) {
        if (this.interactions.getOrDefault(interaction, 0) <= 0) { this.affection += interaction.getAffection(); }
        this.interactions.put(interaction, interaction.getCooldown());
        return interaction.reaction.state;
    }

    public boolean isUUID(UUID uuid) {
        return this.playerUUID.equals(uuid);
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

    public enum Actions {
        FOLLOW(2.0F), FIGHT(3.0F);

        private final float min;

        Actions(float min) {
            this.min = min;
        }

        public boolean inRange(float affection) {
            return this.min <= affection;
        }
    }
}
