package block_party.scene;

import java.util.Locale;

public enum SceneVariableScope {
    NPC("npc"),
    PLAYER("player"),
    WORLD("world");

    private final String serializedName;

    SceneVariableScope(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static SceneVariableScope fromValue(String value, SceneVariableScope fallback) {
        String normalized = value.toLowerCase(Locale.ROOT);
        if (normalized.equals("moe") || normalized.equals("npc")) {
            return NPC;
        }
        if (normalized.equals("player")) {
            return PLAYER;
        }
        if (normalized.equals("global") || normalized.equals("world")) {
            return WORLD;
        }
        return fallback;
    }
}
