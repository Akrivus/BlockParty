package block_party.scene;

import net.minecraft.resources.ResourceLocation;

public enum SceneTrigger {
    CREATION(10),
    SHIFT_LEFT_CLICK(9),
    LEFT_CLICK(9),
    SHIFT_RIGHT_CLICK(8),
    RIGHT_CLICK(8),
    HURT(7),
    ATTACK(6),
    STARE(5),
    SNOWY_WEATHER(4),
    STORMY_WEATHER(4),
    RAINY_WEATHER(4),
    CLEAR_WEATHER(4),
    FULL_MOON(3),
    GIBBOUS_MOON(3),
    CRESCENT_MOON(3),
    NEW_MOON(3),
    SUNRISE(2),
    NOON(2),
    SUNSET(2),
    NIGHT(2),
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
