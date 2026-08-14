package block_party.conversation.validation;

public record Diagnostic(Severity severity, String code, String node, String message) {
}
