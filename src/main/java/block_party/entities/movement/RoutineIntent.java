package block_party.entities.movement;

import java.util.Locale;

public enum RoutineIntent {
    IDLE,
    RELAX,
    REST,
    SLEEP,
    GATHER,
    VISIT,
    WORSHIP,
    CHORE;

    public static RoutineIntent fromValue(String value) {
        if (value == null || value.isBlank()) {
            return IDLE;
        }
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return IDLE;
        }
    }
}
