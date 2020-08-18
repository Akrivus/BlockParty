package mod.moeblocks.client;

import mod.moeblocks.client.animation.*;

import java.util.function.Supplier;

public enum Animations {
    AIM(AimAnimation::new),
    CAST_SPELL(CastSpellAnimation::new),
    DAB(DabAnimation::new),
    DEFAULT(Animation::new),
    IDLE(IdleAnimation::new),
    WAVE(WaveAnimation::new);

    private final Supplier<? extends Animation> animation;

    Animations(Supplier<? extends Animation> animation) {
        this.animation = animation;
    }

    public Animation get() {
        return this.animation.get();
    }
}
