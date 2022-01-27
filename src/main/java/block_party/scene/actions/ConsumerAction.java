package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.function.Consumer;

public class ConsumerAction implements ISceneAction {
    private final Consumer<BlockPartyNPC> consumer;
    private final ISceneAction state;

    public ConsumerAction(Consumer<BlockPartyNPC> consumer) {
        this(consumer, null);
    }

    public ConsumerAction(Consumer<BlockPartyNPC> consumer, ISceneAction state) {
        this.consumer = consumer;
        this.state = state;
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
