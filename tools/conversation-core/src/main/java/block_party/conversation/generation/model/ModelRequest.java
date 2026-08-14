package block_party.conversation.generation.model;

import block_party.conversation.generation.GenerationStage;
import com.google.gson.JsonObject;

public record ModelRequest(
        GenerationStage stage,
        String systemPrompt,
        String userPrompt,
        JsonObject outputSchema,
        int maximumOutputCharacters) {
}
