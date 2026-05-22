package block_party.entities.goals;

import block_party.entities.MoeInHiding;

public enum HideUntil {
    EXPOSED,
    ONE_SECOND_PASSES;

    public boolean isOver(MoeInHiding moe) {
        return switch (this) {
            case EXPOSED -> moe.isAir();
            case ONE_SECOND_PASSES -> moe.getTicksHidden() > 20 || moe.isAir();
        };
    }

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
