package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.AbstractState;

import java.util.function.Supplier;

public enum DataStats {
    FOOD(FoodStats::new), RELATIONSHIP(Relationships::new), STRESS(StressStats::new);

    private final Supplier<? extends AbstractState> state;

    DataStats(Supplier<? extends AbstractState> state) {
        this.state = state;
    }

    public AbstractState get() {
        return this.state.get();
    }
}
