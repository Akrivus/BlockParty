package block_party.npc.automata.state;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.IState;

import java.util.function.Supplier;

public class SupplierState implements IState {
    private final Supplier<IState> state;

    SupplierState(Supplier<IState> state) {
        this.state = state;
    }

    @Override
    public void terminate(BlockPartyNPC npc) { }

    @Override
    public void onTransfer(BlockPartyNPC npc) { }

    @Override
    public IState transfer(BlockPartyNPC npc) {
        return this.state.get();
    }

    @Override
    public boolean isDone(BlockPartyNPC npc) {
        return true;
    }
}
