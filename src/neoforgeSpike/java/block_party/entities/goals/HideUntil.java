package block_party.entities.goals;

public enum HideUntil {
    EXPOSED,
    ONE_SECOND_PASSES;

    public String getValue() {
        return this.name();
    }

    public static HideUntil fromValue(String key) {
        try {
            return HideUntil.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return EXPOSED;
        }
    }
}
