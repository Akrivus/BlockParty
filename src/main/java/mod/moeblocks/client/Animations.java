package mod.moeblocks.client;

import mod.moeblocks.client.animation.Animation;

import java.util.function.Supplier;

public enum Animations {
    DEFAULT(Animation::new);

    private final Supplier<? extends Animation> animation;

    Animations(Supplier<? extends Animation> animation) {
        this.animation = animation;
    }

    public Animation get() {
        return this.animation.get();
    }
}
