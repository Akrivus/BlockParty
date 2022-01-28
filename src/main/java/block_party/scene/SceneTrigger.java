package block_party.scene;

import net.minecraft.resources.ResourceLocation;

public enum SceneTrigger {
    CREATION(10),
    HIDING_SPOT_DISCOVERED(10),
    SHIFT_LEFT_CLICK(9),
    LEFT_CLICK(9),
    SHIFT_RIGHT_CLICK(8),
    RIGHT_CLICK(8),
    HURT(7),
    ATTACK(6),
    STARE(5),
    RAINY_WEATHER(4),
    SUNNY_WEATHER(4),
    FULL_MOON(3),
    GIBBOUS_MOON(3),
    HALF_MOON(3),
    CRESCENT_MOON(3),
    NEW_MOON(3),
    TIME_SUNRISE(2),
    TIME_NOON(2),
    TIME_SUNSET(2),
    TIME_NIGHT(2),
    EVERY_MINUTE(1),
    EVERY_SECOND(1),
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
