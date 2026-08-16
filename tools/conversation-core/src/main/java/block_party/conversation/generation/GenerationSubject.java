package block_party.conversation.generation;

public record GenerationSubject(String kind, String id, String block, String role) {
    public GenerationSubject {
        kind = kind == null ? "BLOCK" : kind.toUpperCase(java.util.Locale.ROOT);
        role = role == null ? "PRIMARY" : role.toUpperCase(java.util.Locale.ROOT);
    }

    public String resolvedBlock() {
        return "BLOCK".equals(kind) ? id : block;
    }
}
