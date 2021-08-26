package block_party.client.animation;

import block_party.client.animation.state.DefaultAnimation;
import block_party.client.animation.state.YearbookAnimation;

import java.util.function.Supplier;

public enum Animation {
    DEFAULT(DefaultAnimation::new), YEARBOOK(YearbookAnimation::new);

    private final Supplier<AbstractAnimation> animation;

    Animation(Supplier<AbstractAnimation> animation) {
        this.animation = animation;
    }

    public AbstractAnimation get() {
        return this.animation.get();
    }

    public static AbstractAnimation get(String key) {
        return Animation.valueOf(key).get();
    }
}
