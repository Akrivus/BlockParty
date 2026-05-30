package block_party.scene;

import block_party.scene.data.Cookies;
import block_party.scene.data.Counters;

public final class SceneVariableStore {
    private final Cookies cookies;
    private final Counters counters;

    SceneVariableStore(Cookies cookies, Counters counters) {
        this.cookies = cookies;
        this.counters = counters;
    }

    public Cookies cookies() {
        return this.cookies;
    }

    public Counters counters() {
        return this.counters;
    }
}
