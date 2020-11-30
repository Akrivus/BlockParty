package moe.blocks.mod.data.dating;

public enum Interactions {
    HEADPAT(2.0F, 1000);

    private final float love;
    private final int cooldown;

    Interactions(float love, int cooldown) {
        this.love = love;
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public String getKey() {
        return String.format("TimeSince%s", this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase());
    }

    public float getLove() {
        return this.love;
    }
}