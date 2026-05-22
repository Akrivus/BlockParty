package block_party.scene;

import block_party.entities.BlockPartyNPC;

import java.util.List;

public class Scene {
    private final List<ISceneObservation> filters;
    private final List<ISceneAction> actions;

    public Scene(List<ISceneObservation> filters, List<ISceneAction> actions) {
        this.filters = filters;
        this.actions = actions;
    }

    public boolean fulfills(BlockPartyNPC npc) {
        for (ISceneObservation filter : this.filters) {
            if (!filter.verify(npc)) { return false; }
        }
        return true;
    }

    public List<ISceneAction> getActions() {
        return this.actions;
    }
}
