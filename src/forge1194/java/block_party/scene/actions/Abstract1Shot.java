package block_party.scene.actions;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneAction;

import java.util.function.Consumer;

public class Abstract1Shot implements ISceneAction {
    private Consumer<BlockPartyNPC> consumer;

    public Abstract1Shot(Consumer<BlockPartyNPC> consumer) {
        this.consumer = consumer;
    }

    public Abstract1Shot() {

    }

    @Override
    public void apply(BlockPartyNPC npc) {
        this.consumer.accept(npc);
    }

    @Override
    public boolean isComplete(BlockPartyNPC npc) {
        return true;
    }
}
