package block_party.scene;

import block_party.entities.Moe;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public final class Scene {
    private final ResourceLocation id;
    private final List<SceneObservation> filters;
    private final List<SceneAction> actions;

    public Scene(ResourceLocation id, List<SceneObservation> filters, List<SceneAction> actions) {
        this.id = id;
        this.filters = List.copyOf(filters);
        this.actions = List.copyOf(actions);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public boolean fulfills(Moe moe) {
        for (SceneObservation filter : this.filters) {
            if (!filter.verify(moe)) {
                return false;
            }
        }
        return true;
    }

    public DiagnosticResult diagnose(Moe moe) {
        List<String> reasons = this.filters.stream()
                .map(filter -> filter.diagnose(moe))
                .filter(result -> !result.passed())
                .flatMap(result -> result.reasons().stream())
                .toList();
        return reasons.isEmpty() ? DiagnosticResult.pass() : new DiagnosticResult(false, reasons);
    }

    public List<SceneAction> getActions() {
        return this.actions;
    }

    public int filterCount() {
        return this.filters.size();
    }
}
