package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ReviewGateSelfTest {
    private ReviewGateSelfTest() {}

    public static void main(String[] args) throws Exception {
        GenerationReview low = new GenerationReview(List.of(
                new ReviewFinding("medium", "scene", "VOICE", "Polish this line.")));
        if (!ReviewGate.publishable(low)) throw new IllegalStateException("Medium findings must not block publication.");

        GenerationReview high = new GenerationReview(List.of(
                new ReviewFinding("HIGH", "scene", "BROKEN_ROUTE", "Repair this route.")));
        if (ReviewGate.publishable(high)) throw new IllegalStateException("High findings must block publication.");

        Path directory = Files.createTempDirectory("block-party-review-gate-");
        try {
            Path project = directory.resolve("project.json");
            Files.writeString(project, "{}");
            Files.writeString(directory.resolve("review.json"), ProjectJson.gson().toJson(high));
            try {
                ReviewGate.requireAdjacentReviewPublishable(project, "Test publication");
                throw new IllegalStateException("Adjacent high review did not block publication.");
            } catch (IllegalStateException expected) {
                if (!expected.getMessage().contains("BROKEN_ROUTE")) throw expected;
            }
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.delete(path);
            }
        }
        System.out.println("Review publication gate check passed.");
    }
}
