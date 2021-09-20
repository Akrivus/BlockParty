package block_party.npc.automata;

import block_party.npc.BlockPartyNPC;

public interface IStateEnum extends IState {
    @Override
    default void terminate(BlockPartyNPC npc) { }

    @Override
    default void onTransfer(BlockPartyNPC npc) { }

    @Override
    default boolean isDone(BlockPartyNPC npc) {
        return true;
    }
}
