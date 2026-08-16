package block_party.scene;

import block_party.scene.data.Cookies;
import block_party.scene.data.Counters;
import block_party.scene.data.Locations;

public final class SceneVariableStore {
    private final Cookies cookies;
    private final Counters counters;
    private final Locations locations;

    SceneVariableStore(Cookies cookies, Counters counters, Locations locations) {
        this.cookies = cookies;
        this.counters = counters;
        this.locations = locations;
    }

    public Cookies cookies() {
        return this.cookies;
    }

    public Counters counters() {
        return this.counters;
    }

    public Locations locations() {
        return this.locations;
    }
}
