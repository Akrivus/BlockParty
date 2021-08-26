package block_party.mob.automata.state;

import block_party.mob.Partyer;
import block_party.mob.automata.IState;

import java.util.function.Supplier;

public class SupplierState implements IState {
    private final Supplier<IState> state;

    SupplierState(Supplier<IState> state) {
        this.state = state;
    }

    @Override
    public void terminate(Partyer npc) { }

    @Override
    public void onTransfer(Partyer npc) { }

    @Override
    public IState transfer(Partyer npc) {
        return this.state.get();
    }

    @Override
    public boolean isDone(Partyer npc) {
        return true;
    }
}
