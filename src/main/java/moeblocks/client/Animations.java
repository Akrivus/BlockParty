package moeblocks.client;

import moeblocks.client.animation.Animation;
import moeblocks.client.animation.state.*;
import moeblocks.client.animation.state.*;

import java.util.function.Supplier;

public enum Animations {
    AIM(Aim::new),
    DEFAULT(Default::new),
    FLAP_ARMS(FlapArms::new),
    IDLE(Idle::new),
    WAVE(Wave::new);

    private final Supplier<? extends Animation> animation;

    Animations(Supplier<? extends Animation> animation) {
        this.animation = animation;
    }

    public Animation get() {
        return this.animation.get();
    }
}
