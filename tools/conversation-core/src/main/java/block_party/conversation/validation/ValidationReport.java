package block_party.conversation.validation;

import java.util.List;

public record ValidationReport(List<Diagnostic> diagnostics) {
    public ValidationReport {
        diagnostics = List.copyOf(diagnostics);
    }

    public boolean valid() {
        return diagnostics.stream().noneMatch(issue -> issue.severity() == Severity.ERROR);
    }

    public long errors() {
        return diagnostics.stream().filter(issue -> issue.severity() == Severity.ERROR).count();
    }

    public long warnings() {
        return diagnostics.stream().filter(issue -> issue.severity() == Severity.WARNING).count();
    }
}
