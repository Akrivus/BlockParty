package block_party.scene.filters;

import block_party.npc.BlockPartyNPC;
import block_party.scene.ISceneFilter;

import java.util.function.Predicate;

public class BooleanFilter implements ISceneFilter {
    protected Predicate<BlockPartyNPC> function;

    public BooleanFilter(Predicate<BlockPartyNPC> function) {
        this.function = function;
    }

    public boolean verify(BlockPartyNPC npc) {
        return this.function.test(npc);
    }
}
