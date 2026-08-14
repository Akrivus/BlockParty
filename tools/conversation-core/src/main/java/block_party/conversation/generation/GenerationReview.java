package block_party.conversation.generation;

import java.util.List;

public record GenerationReview(List<ReviewFinding> findings) {
    public GenerationReview {
        findings = findings == null ? List.of() : List.copyOf(findings);
    }
}
