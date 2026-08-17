package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ReviewGate {
    private ReviewGate() {}

    public static boolean publishable(GenerationReview review) {
        return highFindings(review).isEmpty();
    }

    public static void requirePublishable(GenerationReview review, String operation) {
        List<ReviewFinding> findings = highFindings(review);
        if (findings.isEmpty()) return;
        String summary = findings.stream().limit(8)
                .map(finding -> (finding.code() == null ? "HIGH_REVIEW_FINDING" : finding.code())
                        + (finding.node() == null || finding.node().isBlank() ? "" : " [" + finding.node() + "]"))
                .collect(Collectors.joining(", "));
        throw new IllegalStateException(operation + " refused: generation review contains "
                + findings.size() + " high-severity finding(s): " + summary + ". Repair the project and review it again.");
    }

    public static void requireAdjacentReviewPublishable(Path projectPath, String operation) throws Exception {
        Optional<GenerationReview> review = readAdjacent(projectPath);
        if (review.isPresent()) requirePublishable(review.get(), operation);
    }

    public static Optional<GenerationReview> readAdjacent(Path projectPath) throws Exception {
        if (projectPath == null) return Optional.empty();
        Path parent = projectPath.toAbsolutePath().normalize().getParent();
        if (parent == null) return Optional.empty();
        Path review = parent.resolve("review.json");
        if (!Files.isRegularFile(review)) return Optional.empty();
        return Optional.of(ProjectJson.gson().fromJson(Files.readString(review), GenerationReview.class));
    }

    private static List<ReviewFinding> highFindings(GenerationReview review) {
        if (review == null) return List.of();
        return review.findings().stream()
                .filter(finding -> finding != null && "high".equals(normalize(finding.severity())))
                .toList();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
