package moeblocks.automata.state;

import moeblocks.automata.IState;
import moeblocks.automata.State;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Consumer;

public class ConsumerState implements IState {
    private final Consumer<AbstractNPCEntity> consumer;
    private final IState state;

    public ConsumerState(Consumer<AbstractNPCEntity> consumer, IState state) {
        this.consumer = consumer;
        this.state = state;
    }

    public ConsumerState(Consumer<AbstractNPCEntity> consumer) {
        this(consumer, State.RESET);
    }

    @Override
    public void terminate(AbstractNPCEntity npc) { }

    @Override
    public void onTransfer(AbstractNPCEntity npc) {
        this.consumer.accept(npc);
    }

    @Override
    public IState transfer(AbstractNPCEntity npc) {
        return this.state;
    }

    @Override
    public boolean isDone(AbstractNPCEntity npc) {
        return true;
    }
}
