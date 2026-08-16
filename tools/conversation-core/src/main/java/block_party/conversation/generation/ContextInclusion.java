package block_party.conversation.generation;

public record ContextInclusion(
        String path,
        String title,
        String reason,
        String source,
        String sha256,
        String content) {
}
