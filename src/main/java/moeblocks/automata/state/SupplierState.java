package moeblocks.automata.state;

import moeblocks.automata.IState;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Supplier;

public class SupplierState implements IState {
    private final Supplier<IState> state;

    SupplierState(Supplier<IState> state) {
        this.state = state;
    }

    @Override
    public void terminate(AbstractNPCEntity npc) { }

    @Override
    public void onTransfer(AbstractNPCEntity npc) { }

    @Override
    public IState transfer(AbstractNPCEntity npc) {
        return this.state.get();
    }

    @Override
    public boolean isDone(AbstractNPCEntity npc) {
        return true;
    }
}
