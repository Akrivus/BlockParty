package block_party.scene.observations;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneObservation;

import java.util.function.Predicate;

public class AbstractBoolean implements ISceneObservation {
    protected Predicate<BlockPartyNPC> function;

    public AbstractBoolean(Predicate<BlockPartyNPC> function) {
        this.function = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.function.test(npc);
    }
}
