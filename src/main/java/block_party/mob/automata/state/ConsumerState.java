package block_party.mob.automata.state;

import block_party.mob.BlockPartyNPC;
import block_party.mob.automata.IState;
import block_party.mob.automata.State;

import java.util.function.Consumer;

public class ConsumerState implements IState {
    private final Consumer<BlockPartyNPC> consumer;
    private final IState state;

    public ConsumerState(Consumer<BlockPartyNPC> consumer, IState state) {
        this.consumer = consumer;
        this.state = state;
    }

    public ConsumerState(Consumer<BlockPartyNPC> consumer) {
        this(consumer, State.RESET);
    }

    @Override
    public void terminate(BlockPartyNPC npc) { }

    @Override
    public void onTransfer(BlockPartyNPC npc) {
        this.consumer.accept(npc);
    }

    @Override
    public IState transfer(BlockPartyNPC npc) {
        return this.state;
    }

    @Override
    public boolean isDone(BlockPartyNPC npc) {
        return true;
    }
}
