package block_party.conversation.generation.model;

import block_party.conversation.io.ProjectJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public final class OpenAiResponsesModel implements NarrativeModel {
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(5);
    private static final int MAX_OUTPUT_TOKENS_PER_STAGE = 32_768;
    private static final int MAX_PROJECT_OUTPUT_TOKENS = 65_536;

    private final String model;
    private final String apiKey;
    private final HttpClient client;

    public OpenAiResponsesModel(String model, String apiKey) {
        if (model == null || model.isBlank()) throw new IllegalArgumentException("An OpenAI model must be configured in the brief.");
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("OPENAI_API_KEY is not set.");
        this.model = model;
        this.apiKey = apiKey;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    }

    @Override
    public ModelResponse generate(ModelRequest request) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("store", false);
        int requestedOutputTokens = Math.max(256, request.maximumOutputCharacters() / 3);
        int outputTokenAllowance = Math.min(requestedOutputTokens, outputTokenCeiling(request));
        body.addProperty("max_output_tokens", outputTokenAllowance);
        JsonObject reasoning = new JsonObject();
        reasoning.addProperty("effort", "low");
        body.add("reasoning", reasoning);
        JsonArray input = new JsonArray();
        input.add(message("system", request.systemPrompt()));
        input.add(message("user", request.userPrompt()));
        body.add("input", input);
        JsonObject text = new JsonObject();
        JsonObject format = new JsonObject();
        if (request.outputSchema() == null) {
            format.addProperty("type", "json_object");
        } else {
            format.addProperty("type", "json_schema");
            format.addProperty("name", request.stage().name().toLowerCase(java.util.Locale.ROOT));
            format.addProperty("strict", true);
            format.add("schema", request.outputSchema());
        }
        text.add("format", format);
        body.add("text", text);

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/responses"))
                .timeout(REQUEST_TIMEOUT)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ProjectJson.gson().toJson(body)))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException exception) {
            throw new IllegalStateException("OpenAI " + request.stage().name().toLowerCase(java.util.Locale.ROOT)
                    + " request timed out after " + REQUEST_TIMEOUT.toMinutes() + " minutes using " + model
                    + ". Retry the stage or reduce the requested scene size.", exception);
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI Responses API returned HTTP "
                    + response.statusCode() + ": " + apiError(response.body()));
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        ensureComplete(json, request, outputTokenAllowance);
        String outputText = outputText(json);
        JsonObject usage = json.has("usage") ? json.getAsJsonObject("usage") : new JsonObject();
        JsonElement structuredOutput;
        try {
            structuredOutput = JsonParser.parseString(outputText);
        } catch (JsonParseException exception) {
            throw new IllegalStateException("OpenAI " + stageName(request)
                    + " returned malformed or truncated JSON (" + outputText.length()
                    + " characters). Request ID: " + string(json, "id") + ".", exception);
        }
        return new ModelResponse(
                structuredOutput,
                new ModelUsage(integer(usage, "input_tokens"), integer(usage, "output_tokens")),
                string(json, "id"),
                "openai",
                model);
    }

    private static int outputTokenCeiling(ModelRequest request) {
        return switch (request.stage()) {
            case GRAPH, GRAPH_REPAIR -> MAX_PROJECT_OUTPUT_TOKENS;
            default -> MAX_OUTPUT_TOKENS_PER_STAGE;
        };
    }

    private static void ensureComplete(JsonObject response, ModelRequest request, int outputTokenAllowance) {
        if (!"incomplete".equals(string(response, "status"))) return;
        String reason = "unspecified reason";
        if (response.has("incomplete_details") && response.get("incomplete_details").isJsonObject()) {
            String reported = string(response.getAsJsonObject("incomplete_details"), "reason");
            if (!reported.isBlank()) reason = reported;
        }
        throw new IllegalStateException("OpenAI " + stageName(request) + " response was incomplete (" + reason
                + "). Request ID: " + string(response, "id")
                + ". Output allowance: " + outputTokenAllowance
                + " tokens. Reduce the scene size or raise the stage output allowance.");
    }

    private static String stageName(ModelRequest request) {
        return request.stage().name().toLowerCase(java.util.Locale.ROOT);
    }

    private static JsonObject message(String role, String content) {
        JsonObject result = new JsonObject();
        result.addProperty("role", role);
        result.addProperty("content", content);
        return result;
    }

    private static String outputText(JsonObject response) {
        for (JsonElement itemElement : response.getAsJsonArray("output")) {
            JsonObject item = itemElement.getAsJsonObject();
            if (!"message".equals(string(item, "type"))) continue;
            for (JsonElement contentElement : item.getAsJsonArray("content")) {
                JsonObject content = contentElement.getAsJsonObject();
                if ("refusal".equals(string(content, "type"))) {
                    throw new IllegalStateException("Model refused the generation request.");
                }
                if ("output_text".equals(string(content, "type"))) return string(content, "text");
            }
        }
        throw new IllegalStateException("OpenAI response contained no output_text.");
    }

    private static int integer(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsInt() : 0;
    }

    private static String apiError(String body) {
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            if (json.has("error") && json.getAsJsonObject("error").has("message")) {
                return json.getAsJsonObject("error").get("message").getAsString();
            }
        } catch (RuntimeException ignored) {
            // Fall back to a bounded response body when the API did not return its usual error shape.
        }
        return body == null || body.isBlank()
                ? "No error details returned."
                : body.substring(0, Math.min(body.length(), 500));
    }

    private static String string(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsString() : "";
    }
}
