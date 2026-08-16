package block_party.conversation.model;

import java.util.List;
import java.util.Locale;

public final class TriggerTypes {
    private static final List<String> VALUES = List.of(
            "creation", "hiding_spot_discovered", "attention", "phone_call", "follow_started", "follow_ended",
            "party_invite", "gift_received", "wait", "dismiss", "shift_left_click", "left_click",
            "shift_right_click", "right_click", "hurt", "attack", "stare", "every_tick", "random_tick", "null");

    private TriggerTypes() {
    }

    public static List<String> values() {
        return VALUES;
    }

    public static String canonicalize(String value) {
        if (value == null || value.isBlank()) return "right_click";
        String trigger = value.toLowerCase(Locale.ROOT);
        if (trigger.startsWith("block_party:")) trigger = trigger.substring("block_party:".length());
        if (trigger.equals("interaction") || trigger.equals("interact")) return "right_click";
        return trigger;
    }

    public static boolean valid(String value) {
        return value == null || value.isBlank() || VALUES.contains(canonicalize(value));
    }

    public static String qualified(String value) {
        return "block_party:" + canonicalize(value);
    }
}
