package block_party.scene.filters;

import block_party.entities.BlockPartyNPC;
import block_party.scene.ISceneFilter;

import java.util.function.Predicate;

public class AbstractBoolean implements ISceneFilter {
    protected Predicate<BlockPartyNPC> function;

    public AbstractBoolean(Predicate<BlockPartyNPC> function) {
        this.function = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.function.test(npc);
    }
}
