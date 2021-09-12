package block_party.mob.automata;

import block_party.mob.BlockPartyNPC;

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
