package moeblocks.automata.state.keys;

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
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        if (key.isEmpty()) { return Animation.DEFAULT; }
        return Animation.valueOf(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return Animation.values();
    }
}
