package block_party.conversation.generation;

import java.util.List;

public record GenerationBrief(
        int generationFormat,
        String id,
        String namespace,
        String title,
        String prompt,
        List<String> characters,
        List<String> documents,
        List<String> existingPacks,
        GenerationConstraints constraints,
        GenerationBudget budget,
        String provider,
        String model,
        String recordedResponses) {
    public GenerationBrief {
        characters = characters == null ? List.of() : List.copyOf(characters);
        documents = documents == null ? List.of() : List.copyOf(documents);
        existingPacks = existingPacks == null ? List.of() : List.copyOf(existingPacks);
        constraints = constraints == null ? new GenerationConstraints(3, 12, 240, null, null, null, null) : constraints;
        budget = budget == null ? new GenerationBudget(20, 500_000, 200_000) : budget;
        provider = provider == null ? "recorded" : provider;
    }
}
