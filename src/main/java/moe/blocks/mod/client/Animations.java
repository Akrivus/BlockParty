package moe.blocks.mod.client;

import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.animation.state.*;

import java.util.function.Supplier;

public enum Animations {
    AIM(Aim::new),
    DEFAULT(Default::new),
    IDLE(Idle::new),
    JELLY_ARMS(JellyArms::new),
    WAVE(Wave::new);

    private final Supplier<? extends Animation> animation;

    Animations(Supplier<? extends Animation> animation) {
        this.animation = animation;
    }

    public Animation get() {
        return this.animation.get();
    }
}
