package block_party.conversation.generation;

import block_party.conversation.io.ProjectJson;
import block_party.conversation.model.ScenePackProject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ReviewGate {
    private static final String REVIEW_FINGERPRINT = "review-project.sha256";
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
        requireAdjacentReviewPublishable(projectPath, ProjectJson.read(projectPath), operation);
    }

    public static void requireAdjacentReviewPublishable(
            Path projectPath, ScenePackProject project, String operation) throws Exception {
        Optional<GenerationReview> review = readAdjacent(projectPath);
        if (review.isEmpty()) return;
        requirePublishable(review.get(), operation);
        Path fingerprint = adjacent(projectPath, REVIEW_FINGERPRINT);
        String expected = Files.isRegularFile(fingerprint) ? Files.readString(fingerprint).trim() : "";
        String actual = projectFingerprint(project);
        if (!actual.equalsIgnoreCase(expected)) {
            throw new IllegalStateException(operation
                    + " refused: the project has changed since its generation review. Review it again.");
        }
    }

    public static void recordReviewedProject(Path projectPath, ScenePackProject project) throws Exception {
        Files.writeString(adjacent(projectPath, REVIEW_FINGERPRINT), projectFingerprint(project) + System.lineSeparator());
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
                .filter(finding -> finding != null && blockingSeverity(finding.severity()))
                .toList();
    }

    private static boolean blockingSeverity(String severity) {
        String normalized = normalize(severity);
        return "high".equals(normalized) || "error".equals(normalized);
    }

    private static Path adjacent(Path projectPath, String name) {
        Path normalized = projectPath.toAbsolutePath().normalize();
        Path parent = normalized.getParent();
        if (parent == null) throw new IllegalArgumentException("Project has no parent directory: " + projectPath);
        return parent.resolve(name);
    }

    private static String projectFingerprint(ScenePackProject project) throws Exception {
        byte[] bytes = ProjectJson.gson().toJson(project).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
