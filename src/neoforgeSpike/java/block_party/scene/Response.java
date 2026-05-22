package block_party.scene;

import net.minecraft.resources.ResourceLocation;

public enum Response {
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

    public Response fromValue(String key) {
        try {
            return Response.valueOf(key.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return this;
        }
    }

    public Response fromValue(ResourceLocation location) {
        return this.fromValue(location.getPath());
    }
}
