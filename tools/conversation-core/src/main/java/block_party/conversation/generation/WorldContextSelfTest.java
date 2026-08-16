package block_party.conversation.generation;

import block_party.conversation.model.ScenePackProject;
import java.nio.file.Path;
import java.util.List;

public final class WorldContextSelfTest {
    private WorldContextSelfTest() {
    }

    public static void main(String[] args) throws Exception {
        Path repository = Path.of(args[0]);
        ResolvedGenerationContext crying = new WorldContextResolver().resolve(
                brief("minecraft:crying_obsidian"), repository);
        List<String> cryingTags = crying.subjectTags().get("minecraft:crying_obsidian");
        if (!cryingTags.contains("block_party:moe/traits/cardinal")) {
            throw new AssertionError("Crying Obsidian must resolve as cardinal.");
        }
        ResolvedGenerationContext obsidian = new WorldContextResolver().resolve(
                brief("minecraft:obsidian"), repository);
        if (obsidian.subjectTags().get("minecraft:obsidian")
                .contains("block_party:moe/traits/cardinal")) {
            throw new AssertionError("Ordinary Obsidian must not inherit cardinal identity.");
        }
        if (obsidian.inclusions().size() < 3) {
            throw new AssertionError("Mandatory world context is missing.");
        }
        ResolvedGenerationContext oak = new WorldContextResolver().resolve(
                brief("minecraft:oak_log"), repository);
        if (!oak.subjectTags().get("minecraft:oak_log")
                .contains("block_party:moe/traits/cardinal")) {
            throw new AssertionError("Nested progression tags must resolve into cardinal membership.");
        }
        if (GenerationSchemas.forType(ArcPlan.class) == null
                || !GenerationSchemas.forType(ArcPlan.class)
                        .getAsJsonObject("properties").has("beats")) {
            throw new AssertionError("Arc plans must use a stage-specific output schema.");
        }
        if (!GenerationSchemas.forType(ScenePackProject.class)
                .getAsJsonObject("properties").has("nodes")) {
            throw new AssertionError("Scene packs must use a complete project output schema.");
        }
        System.out.println("World context check passed.");
    }

    private static GenerationBrief brief(String block) {
        return new GenerationBrief(
                1, "context_check", "context_check", "Context Check", "Check context.",
                List.of(new GenerationSubject("BLOCK", block, null, "PRIMARY")),
                true, List.of(), List.of(), List.of(), List.of(), null, List.of(), null, null,
                "recorded", "fixture", "unused");
    }
}
