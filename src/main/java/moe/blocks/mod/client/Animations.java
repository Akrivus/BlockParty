package moe.blocks.mod.client;

import moe.blocks.mod.client.animation.AimAnimation;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.animation.WaitingAnimation;
import moe.blocks.mod.client.animation.WaveAnimation;

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
