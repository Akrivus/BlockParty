package block_party.registry.resources;

import block_party.entities.social.SocialAffinities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public final class SocialAffinityReloadListener implements PreparableReloadListener {
    private static final String DIRECTORY = "moes/social_affinities";

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(SocialAffinities::replaceRules, gameExecutor);
    }

    private static List<SocialAffinities.Rule> load(ResourceManager manager) {
        List<SocialAffinities.Rule> rules = new ArrayList<>();
        manager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()) {
                rules.addAll(safeParseRules(JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (RuntimeException | IOException ignored) {
                // Malformed tuning resources fail closed so one bad datapack file does not break reload.
            }
        });
        return List.copyOf(rules);
    }

    public static List<SocialAffinities.Rule> safeParseRules(JsonObject json) {
        try {
            return parseRules(json);
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    public static List<SocialAffinities.Rule> parseRules(JsonObject json) {
        JsonArray array = json.has("rules") ? GsonHelper.getAsJsonArray(json, "rules") : new JsonArray();
        List<SocialAffinities.Rule> rules = new ArrayList<>();
        for (JsonElement element : array) {
            parseRule(GsonHelper.convertToJsonObject(element, "social affinity rule")).ifPresent(rules::add);
        }
        return List.copyOf(rules);
    }

    private static Optional<SocialAffinities.Rule> parseRule(JsonObject json) {
        Optional<SocialAffinities.Matcher> observer = parseMatcher(GsonHelper.getAsJsonObject(json, "observer"));
        Optional<SocialAffinities.Matcher> target = parseMatcher(GsonHelper.getAsJsonObject(json, "target"));
        if (observer.isEmpty() || target.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SocialAffinities.Rule(
                observer.get(),
                target.get(),
                json.has("layer")
                        ? SocialAffinities.RuleLayer.fromString(GsonHelper.getAsString(json, "layer"))
                        : SocialAffinities.Rule.inferLayer(observer.get(), target.get()),
                GsonHelper.getAsFloat(json, "affinity", 0.0F),
                GsonHelper.getAsFloat(json, "tension", 0.0F),
                GsonHelper.getAsFloat(json, "interest", 0.0F)));
    }

    private static Optional<SocialAffinities.Matcher> parseMatcher(JsonObject json) {
        Optional<SocialAffinities.BlockMatcher> block = SocialAffinities.parseBlockMatcher(
                optionalString(json, "block"),
                optionalString(json, "block_tag"));
        SocialAffinities.Matcher matcher = new SocialAffinities.Matcher(
                block,
                optionalString(json, "blood_type"),
                optionalString(json, "dere"),
                optionalString(json, "zodiac"),
                optionalString(json, "gender"),
                optionalString(json, "emotion"));
        return matcher.isEmpty() ? Optional.empty() : Optional.of(matcher);
    }

    private static Optional<String> optionalString(JsonObject json, String key) {
        return json.has(key) ? Optional.of(GsonHelper.getAsString(json, key)) : Optional.empty();
    }
}
