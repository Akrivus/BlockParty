package block_party.scene;

import block_party.entities.Moe;
import java.util.List;

public final class Scene {
    private final List<SceneObservation> filters;
    private final List<SceneAction> actions;

    public Scene(List<SceneObservation> filters, List<SceneAction> actions) {
        this.filters = List.copyOf(filters);
        this.actions = List.copyOf(actions);
    }

    public boolean fulfills(Moe moe) {
        for (SceneObservation filter : this.filters) {
            if (!filter.verify(moe)) {
                return false;
            }
        }
        return true;
    }

    public List<SceneAction> getActions() {
        return this.actions;
    }

    public int filterCount() {
        return this.filters.size();
    }
}
