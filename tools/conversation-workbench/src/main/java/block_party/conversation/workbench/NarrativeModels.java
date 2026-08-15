package block_party.conversation.workbench;

import block_party.conversation.generation.GenerationBrief;
import block_party.conversation.generation.model.NarrativeModel;
import block_party.conversation.generation.model.OpenAiResponsesModel;
import block_party.conversation.generation.model.RecordedDirectoryModel;
import java.nio.file.Path;

final class NarrativeModels {
    private NarrativeModels() {
    }

    static NarrativeModel create(GenerationBrief brief, Path repositoryRoot) {
        if ("openai".equalsIgnoreCase(brief.provider())) {
            return new OpenAiResponsesModel(brief.model(), System.getenv("OPENAI_API_KEY"));
        }

        String recordedResponses = brief.recordedResponses();
        if (recordedResponses == null || recordedResponses.isBlank()) {
            throw new IllegalArgumentException("recordedResponses is required for the recorded provider.");
        }

        Path recordedDirectory = Path.of(recordedResponses);
        if (!recordedDirectory.isAbsolute()) {
            recordedDirectory = repositoryRoot.resolve(recordedDirectory);
        }
        return new RecordedDirectoryModel(recordedDirectory.normalize());
    }
}
