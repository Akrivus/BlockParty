package moeblocks.automata.state;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.animation.state.*;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Supplier;

public enum Animation implements IStateEnum<AbstractNPCEntity> {
    AIM(AimBow::new),
    DEFAULT(Default::new),
    FLAP_ARMS(HappyDance::new);

    private final Supplier<? extends AnimationState> animation;

    Animation(Supplier<? extends AnimationState> animation) {
        this.animation = animation;
    }

    public AnimationState get() {
        return this.animation.get();
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return this.animation.get();
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return Animation.DEFAULT; }
        return Animation.valueOf(token);
    }
}
