package block_party.entities.goals;

import block_party.entities.MoeInHiding;

import java.util.function.Function;

public enum HideUntil {
    EXPOSED(moe -> false),
    ONE_SECOND_PASSES(moe -> moe.getTicksHidden() > 20);

    final Function<MoeInHiding, Boolean> condition;

    HideUntil(Function<MoeInHiding, Boolean> condition) {
        this.condition = condition;
    }

    public boolean isOver(MoeInHiding moe) {
        return this.condition.apply(moe) || moe.isAir();
    }

    public String getValue() {
        return this.name();
    }

    public HideUntil fromValue(String key) {
        try {
            return HideUntil.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }
}
