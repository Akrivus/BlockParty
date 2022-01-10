package block_party.scene.dialogue;

import net.minecraft.resources.ResourceLocation;

public enum ResponseIcon {
    GREEN_CHECKMARK,
    RED_X,
    CHAT_BUBBLE,
    LOVELY_HEART,
    TRUSTY_ARMOR,
    STRESSFUL_SKULL,
    LEATHER_BAG,
    ANVIL,
    NEXT_RESPONSE,
    CLOSE_DIALOGUE,
    OPEN_DIALOGUE;

    public ResponseIcon fromValue(String key) {
        try {
            return ResponseIcon.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public ResponseIcon fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }
}
