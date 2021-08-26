package block_party.mob.automata.state;

import block_party.mob.Partyer;
import block_party.mob.automata.IState;
import block_party.mob.automata.State;

import java.util.function.Consumer;

public class ConsumerState implements IState {
    private final Consumer<Partyer> consumer;
    private final IState state;

    public ConsumerState(Consumer<Partyer> consumer, IState state) {
        this.consumer = consumer;
        this.state = state;
    }

    public ConsumerState(Consumer<Partyer> consumer) {
        this(consumer, State.RESET);
    }

    @Override
    public void terminate(Partyer npc) { }

    @Override
    public void onTransfer(Partyer npc) {
        this.consumer.accept(npc);
    }

    @Override
    public IState transfer(Partyer npc) {
        return this.state;
    }

    @Override
    public boolean isDone(Partyer npc) {
        return true;
    }
}
