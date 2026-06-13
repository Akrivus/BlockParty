package block_party.scene;

import block_party.entities.Moe;

public interface SceneObservation {
    boolean verify(Moe moe);

    default DiagnosticResult diagnose(Moe moe) {
        return this.verify(moe) ? DiagnosticResult.pass() : DiagnosticResult.fail("filter failed");
    }
}
