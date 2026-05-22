package block_party.scene;

import net.minecraft.resources.ResourceLocation;

public enum Response {
    GREEN_CHECKMARK, RED_X, CHAT_BUBBLE, LOVELY_HEART, TRUSTY_ARMOR, STRESSFUL_SKULL, LEATHER_BAG, ANVIL, NEXT_RESPONSE, CLOSE_DIALOGUE, OPEN_DIALOGUE;

    public Response fromValue(ResourceLocation location) {
        return fromValue(location.getPath());
    }

    public Response fromValue(String key) {
        try {
            return Response.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public String getTranslationKey() {
        return String.format("gui.block_party.dialogue.response.%s", this.name().toLowerCase());
    }
}
