package block_party.npc.automata.state;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.function.Consumer;

public class ConsumerState implements ISceneAction {
    private final Consumer<BlockPartyNPC> consumer;
    private final ISceneAction state;

    public ConsumerState(Consumer<BlockPartyNPC> consumer, ISceneAction state) {
        this.consumer = consumer;
        this.state = state;
    }

    public ConsumerState(Consumer<BlockPartyNPC> consumer) {
        this(consumer, null);
    }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.consumer.accept(npc);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void onComplete() {

    }
}
