package block_party.scene;

import java.util.List;

public record DiagnosticResult(boolean passed, List<String> reasons) {
    public static DiagnosticResult pass() {
        return new DiagnosticResult(true, List.of());
    }

    public static DiagnosticResult fail(String reason) {
        return new DiagnosticResult(false, List.of(reason));
    }
}
