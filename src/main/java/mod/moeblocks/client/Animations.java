package mod.moeblocks.client;

import mod.moeblocks.client.animation.AimAnimation;
import mod.moeblocks.client.animation.Animation;
import mod.moeblocks.client.animation.WaitingAnimation;
import mod.moeblocks.client.animation.WaveAnimation;

import java.util.function.Supplier;

public enum Animations {
    AIM(AimAnimation::new),
    DEFAULT(Animation::new),
    WAITING(WaitingAnimation::new),
    WAVE(WaveAnimation::new);

    private final Supplier<? extends Animation> animation;

    Animations(Supplier<? extends Animation> animation) {
        this.animation = animation;
    }

    public Animation get() {
        return this.animation.get();
    }
}
