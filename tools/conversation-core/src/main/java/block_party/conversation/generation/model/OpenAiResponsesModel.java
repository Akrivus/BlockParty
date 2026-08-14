package block_party.conversation.generation.model;

import block_party.conversation.io.ProjectJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class OpenAiResponsesModel implements NarrativeModel {
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
        body.addProperty("max_output_tokens", Math.max(256, request.maximumOutputCharacters() / 3));
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
                .timeout(Duration.ofSeconds(120))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ProjectJson.gson().toJson(body)))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI Responses API returned HTTP " + response.statusCode() + ".");
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String outputText = outputText(json);
        JsonObject usage = json.has("usage") ? json.getAsJsonObject("usage") : new JsonObject();
        return new ModelResponse(
                JsonParser.parseString(outputText),
                new ModelUsage(integer(usage, "input_tokens"), integer(usage, "output_tokens")),
                string(json, "id"),
                "openai",
                model);
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

    private static String string(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsString() : "";
    }
}
