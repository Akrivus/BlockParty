package block_party.conversation.model;

import java.util.List;

/** Response icons supported by the in-game dialogue UI. */
public final class ResponseCues {
    public static final List<String> VALUES = List.of(
            "green_checkmark", "red_x", "chat_bubble", "lovely_heart", "trusty_armor",
            "stressful_skull", "leather_bag", "anvil", "next_response", "close_dialogue", "open_dialogue");
    public static final List<String> NEUTRAL_DEFAULTS = List.of("chat_bubble", "next_response", "close_dialogue");

    private ResponseCues() {
    }

    public static boolean valid(String value) {
        if (value == null) return false;
        int separator = value.indexOf(':');
        return VALUES.contains(separator >= 0 ? value.substring(separator + 1) : value);
    }
}
