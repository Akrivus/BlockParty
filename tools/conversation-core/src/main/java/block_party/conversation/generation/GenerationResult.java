package block_party.conversation.generation;

import block_party.conversation.model.ScenePackProject;
import java.nio.file.Path;

public record GenerationResult(ScenePackProject project, Path output, int modelCalls, int inputTokens, int outputTokens) {
}
