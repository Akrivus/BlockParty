package moeblocks.automata;

import moeblocks.automata.state.ConsumerState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.state.LookAtPlayerState;

import java.util.function.Supplier;

public enum State implements IStateEnum {
    JUMP(() -> new ConsumerState((npc) -> npc.jump())),
    LOOK_AT_PLAYER(() -> new LookAtPlayerState(1.0F, 20)),
    RESET(() -> null);

    private final Supplier<IState> state;

    State(Supplier<IState> state) {
        this.state = state;
    }

    @Override
    public IState transfer(AbstractNPCEntity npc) {
        return this.state.get();
    }
}
