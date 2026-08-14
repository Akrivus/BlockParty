package block_party.conversation.generation.model;

import com.google.gson.JsonElement;

public record ModelResponse(JsonElement structuredOutput, ModelUsage usage, String requestId, String provider, String model) {
}
