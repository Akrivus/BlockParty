package block_party.scene;

import block_party.npc.BlockPartyNPC;

import java.util.List;

public class Scene {
    private final List<ISceneFilter> filters;
    private final List<ISceneAction> actions;

    public Scene(List<ISceneFilter> filters, List<ISceneAction> actions) {
        this.filters = filters;
        this.actions = actions;
    }

    public boolean fulfills(BlockPartyNPC npc) {
        for (ISceneFilter filter : this.filters) {
            if (!filter.verify(npc)) { return false; }
        }
        return true;
    }

    public List<ISceneAction> getActions() {
        return this.actions;
    }
}
