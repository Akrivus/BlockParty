package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.state.ConsumerState;
import block_party.npc.state.LookAtPlayerState;

import java.util.function.Supplier;

public enum State implements IStateEnum {
    JUMP(() -> new ConsumerState((npc) -> npc.jumpFromGround())),
    LOOK_AT_PLAYER(() -> new LookAtPlayerState(1.0F, 20)),
    RESET(() -> null);

    private final Supplier<IState> state;

    State(Supplier<IState> state) {
        this.state = state;
    }

    @Override
    public IState transfer(BlockPartyNPC npc) {
        return this.state.get();
    }
}
