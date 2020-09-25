package moe.blocks.mod.data.dating;

import moe.blocks.mod.data.conversation.Reactions;

public enum Interactions {
    HEADPAT(2.0F, 6000, Reactions.FLAP_ARMS);

    public final Reactions reaction;
    private final float love;
    private final int cooldown;

    Interactions(float love, int cooldown, Reactions reaction) {
        this.love = love;
        this.cooldown = cooldown;
        this.reaction = reaction;
    }

    public float getLove() {
        return this.love;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public String getKey() {
        return String.format("TimeSince%s", this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase());
    }
}