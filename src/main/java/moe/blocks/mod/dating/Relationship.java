package moe.blocks.mod.dating;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class Relationship {
    protected final UUID uuid;
    protected Phase phase;
    protected float affection;
    protected float trust;
    protected float passion;

    public Relationship(PlayerEntity player) {
        this(player.getUniqueID());
    }

    public Relationship(UUID uuid) {
        this.uuid = uuid;
    }

    public float getAffection() {
        return this.affection;
    }

    public float getTrust() {
        return this.trust;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public enum Phase {
        INTRODUCTION, INFATUATION, CONFUSION, CONFESSION, INTERMISSION
    }
}
