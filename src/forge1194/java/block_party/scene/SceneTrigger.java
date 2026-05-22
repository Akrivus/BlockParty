package block_party.scene;

import net.minecraft.resources.ResourceLocation;

public enum SceneTrigger {
    CREATION(8),
    HIDING_SPOT_DISCOVERED(8),
    SHIFT_LEFT_CLICK(7),
    LEFT_CLICK(6),
    SHIFT_RIGHT_CLICK(6),
    RIGHT_CLICK(5),
    HURT(4),
    ATTACK(3),
    STARE(2),
    EVERY_TICK(1),
    RANDOM_TICK(1),
    NULL(0);

    private final int priority;

    SceneTrigger(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public SceneTrigger fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }

    public SceneTrigger fromValue(String key) {
        try {
            return SceneTrigger.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }
}
