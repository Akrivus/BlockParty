package block_party.registry.resources;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.entities.goals.HideUntil;
import block_party.scene.Response;
import block_party.scene.Scene;
import block_party.scene.SceneAction;
import block_party.scene.SceneObservation;
import block_party.scene.SceneObservationFactories;
import block_party.scene.SceneObservations;
import block_party.scene.SceneTrigger;
import block_party.scene.Speaker;
import block_party.scene.actions.CookieAction;
import block_party.scene.actions.CounterAction;
import block_party.scene.actions.CreateVoicemailAction;
import block_party.scene.actions.ClearRoutineIntentAction;
import block_party.scene.actions.ClearFollowSessionAction;
import block_party.scene.actions.EndAction;
import block_party.scene.actions.GiveItemAction;
import block_party.scene.actions.GoToAnchorAction;
import block_party.scene.actions.HideAction;
import block_party.scene.actions.OpenInventoryAction;
import block_party.scene.actions.SceneItemStacks;
import block_party.scene.actions.SendDialogueAction;
import block_party.scene.actions.SendResponseAction;
import block_party.scene.actions.SetHomeToAnchorAction;
import block_party.scene.actions.SetRoutineIntentAction;
import block_party.scene.actions.SleepAtHomeAction;
import block_party.scene.actions.StartFollowSessionAction;
import block_party.scene.actions.TakeItemAction;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public final class ScenesReloadListener implements PreparableReloadListener {
    private static final String DIRECTORY = "scenes";
    private static volatile int loadedCount;

    private Map<SceneTrigger, List<Scene>> scenes = Map.of();
    private Map<ResourceLocation, Scene> byName = Map.of();

    public static int loadedCount() {
        return loadedCount;
    }

    public int sceneCount() {
        return this.byName.size();
    }

    public Scene get(ResourceLocation id) {
        return this.byName.get(own(id));
    }

    public Scene get(SceneTrigger trigger, Moe moe) {
        List<Scene> candidates = new ArrayList<>(this.scenes.getOrDefault(trigger, List.of()));
        if (candidates.isEmpty()) {
            return null;
        }
        Collections.shuffle(candidates);
        candidates.removeIf(scene -> !scene.fulfills(moe));
        if (candidates.isEmpty()) {
            return null;
        }
        int mostSpecific = candidates.stream().mapToInt(Scene::filterCount).max().orElse(0);
        candidates.removeIf(scene -> scene.filterCount() < mostSpecific);
        return candidates.getFirst();
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            java.util.concurrent.Executor backgroundExecutor,
            java.util.concurrent.Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> load(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::applyLoaded, gameExecutor);
    }

    private static LoadedScenes load(ResourceManager resourceManager) {
        Map<SceneTrigger, List<Scene>> byTrigger = new ConcurrentHashMap<>();
        Map<ResourceLocation, Scene> byName = new LinkedHashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation id = own(resourceId(entry.getKey()));
            try (Reader reader = entry.getValue().openAsReader()) {
                ParsedScene parsed = parseScene(id, JsonParser.parseReader(reader));
                byTrigger.computeIfAbsent(parsed.trigger(), ignored -> new ArrayList<>()).add(parsed.scene());
                byName.put(id, parsed.scene());
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to parse Block Party scene " + id, exception);
            }
        }
        Map<SceneTrigger, List<Scene>> immutableByTrigger = new LinkedHashMap<>();
        byTrigger.forEach((trigger, scenes) -> immutableByTrigger.put(trigger, List.copyOf(scenes)));
        return new LoadedScenes(Map.copyOf(immutableByTrigger), Map.copyOf(byName));
    }

    private void applyLoaded(LoadedScenes loaded) {
        this.scenes = loaded.byTrigger();
        this.byName = loaded.byName();
        loadedCount = loaded.byName().size();
    }

    public static ParsedScene parseSceneForTests(ResourceLocation id, JsonElement element) {
        return parseScene(id, element);
    }

    public static SceneAction parseActionForTests(JsonObject json) {
        return parseAction(json);
    }

    private static ParsedScene parseScene(ResourceLocation id, JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element, "scene " + id);
        SceneTrigger trigger = SceneTrigger.NULL.fromValue(own(resource(GsonHelper.getAsString(json, "trigger", "block_party:null"))));
        List<SceneObservation> filters = parseFilters(json.has("filters") ? json.getAsJsonArray("filters") : new JsonArray());
        List<SceneAction> actions = parseActions(json.has("actions") ? json.getAsJsonArray("actions") : new JsonArray());
        return new ParsedScene(trigger, new Scene(filters, actions));
    }

    private static List<SceneObservation> parseFilters(JsonArray array) {
        List<SceneObservation> filters = new ArrayList<>();
        for (JsonElement element : array) {
            filters.add(parseFilter(element));
        }
        return filters;
    }

    private static SceneObservation parseFilter(JsonElement element) {
        ResourceLocation type;
        JsonObject source = new JsonObject();
        if (element.isJsonObject()) {
            source = element.getAsJsonObject();
            if (source.has("type")) {
                type = own(resource(GsonHelper.getAsString(source, "type", "block_party:always")));
            } else if (source.has("filter") && source.get("filter").isJsonPrimitive()) {
                type = own(resource(GsonHelper.getAsString(source, "filter", "block_party:always")));
            } else {
                type = BlockParty.source("always");
            }
            if (source.has("filter") && source.get("filter").isJsonObject()) {
                source = source.getAsJsonObject("filter");
            }
        } else {
            type = own(resource(element.getAsString()));
        }
        JsonObject json = source;
        return SceneObservationFactories.build(type, json);
    }

    private static List<SceneAction> parseActions(JsonArray array) {
        List<SceneAction> actions = new ArrayList<>();
        for (JsonElement element : array) {
            actions.add(parseAction(element));
        }
        return actions;
    }

    private static SceneAction parseAction(JsonElement element) {
        if (element.isJsonObject()) {
            return parseAction(element.getAsJsonObject());
        }
        ResourceLocation type = own(resource(element.getAsString()));
        return "end".equals(type.getPath()) ? EndAction.INSTANCE : EndAction.INSTANCE;
    }

    private static SceneAction parseAction(JsonObject json) {
        ResourceLocation type = actionType(json);
        JsonObject payload = json.has("action") && json.get("action").isJsonObject() ? json.getAsJsonObject("action") : json;
        return switch (type.getPath()) {
            case "send_dialogue" -> parseDialogue(payload);
            case "send_response" -> parseResponse(payload);
            case "hide" -> new HideAction(HideUntil.EXPOSED.fromValue(GsonHelper.getAsString(payload, "until", "exposed")));
            case "cookie" -> new CookieAction(
                    CookieAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "set")),
                    GsonHelper.getAsString(payload, "name", ""),
                    GsonHelper.getAsString(payload, "value", ""));
            case "counter" -> new CounterAction(
                    CounterAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "add")),
                    GsonHelper.getAsString(payload, "name", ""),
                    GsonHelper.getAsInt(payload, "value", 1));
            case "create_voicemail" -> new CreateVoicemailAction(
                    GsonHelper.getAsString(payload, "text", ""),
                    GsonHelper.getAsBoolean(payload, "tooltip", true),
                    parseSpeaker(payload.has("speaker") && payload.get("speaker").isJsonObject() ? payload.getAsJsonObject("speaker") : new JsonObject()),
                    payload.has("sound") ? resource(GsonHelper.getAsString(payload, "sound", "")) : null,
                    voicemailDelayMillis(payload));
            case "start_follow_session" -> new StartFollowSessionAction(
                    parseMovementIntent(GsonHelper.getAsString(payload, "intent", "follow_request")),
                    Math.max(0, GsonHelper.getAsInt(payload, "ticks", 20 * 60)),
                    GsonHelper.getAsBoolean(payload, "can_change_dimension", false),
                    GsonHelper.getAsBoolean(payload, "trigger_scene", false));
            case "go_to_anchor" -> new GoToAnchorAction(GsonHelper.getAsDouble(payload, "speed", 1.0D));
            case "set_home_to_anchor" -> SetHomeToAnchorAction.INSTANCE;
            case "set_routine_intent" -> new SetRoutineIntentAction(RoutineIntent.fromValue(GsonHelper.getAsString(payload, "intent", "idle")));
            case "clear_routine_intent" -> ClearRoutineIntentAction.INSTANCE;
            case "sleep_at_home" -> new SleepAtHomeAction(HideUntil.EXPOSED.fromValue(GsonHelper.getAsString(payload, "until", "exposed")));
            case "open_inventory" -> OpenInventoryAction.INSTANCE;
            case "give_item" -> new GiveItemAction(
                    SceneItemStacks.parse(payload),
                    GiveItemAction.Target.fromValue(GsonHelper.getAsString(payload, "target", "player")));
            case "take_item" -> new TakeItemAction(
                    payload,
                    Math.max(1, GsonHelper.getAsInt(payload, "count", 1)),
                    TakeItemAction.Source.fromValue(GsonHelper.getAsString(payload, "source", "player")),
                    TakeItemAction.Destination.fromValue(GsonHelper.getAsString(payload, "destination", "moe")));
            case "clear_follow_session", "wait", "dismiss" -> ClearFollowSessionAction.INSTANCE;
            default -> EndAction.INSTANCE;
        };
    }

    private static PlayerMovementIntent parseMovementIntent(String value) {
        try {
            return PlayerMovementIntent.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return PlayerMovementIntent.FOLLOW_REQUEST;
        }
    }

    private static long voicemailDelayMillis(JsonObject json) {
        if (json.has("delay_seconds")) {
            return Math.max(0L, GsonHelper.getAsLong(json, "delay_seconds", 0L)) * 1000L;
        }
        return Math.max(0L, GsonHelper.getAsLong(json, "delay_minutes", 60L)) * 60L * 1000L;
    }

    private static ResourceLocation actionType(JsonObject json) {
        if (json.has("type")) {
            return own(resource(GsonHelper.getAsString(json, "type", "block_party:end")));
        }
        if (json.has("action") && json.get("action").isJsonPrimitive()) {
            return own(resource(GsonHelper.getAsString(json, "action", "block_party:end")));
        }
        return BlockParty.source("end");
    }

    private static SendDialogueAction parseDialogue(JsonObject json) {
        Map<Response, SendResponseAction> responses = new LinkedHashMap<>();
        JsonArray responseArray = json.has("responses") && json.get("responses").isJsonArray()
                ? json.getAsJsonArray("responses")
                : new JsonArray();
        for (JsonElement element : responseArray) {
            if (!element.isJsonObject()) {
                continue;
            }
            SendResponseAction response = parseResponse(element.getAsJsonObject());
            responses.put(response.icon(), response);
        }
        return new SendDialogueAction(
                GsonHelper.getAsString(json, "text", ""),
                GsonHelper.getAsBoolean(json, "tooltip", false),
                parseSpeaker(json.has("speaker") && json.get("speaker").isJsonObject() ? json.getAsJsonObject("speaker") : new JsonObject()),
                json.has("sound") ? resource(GsonHelper.getAsString(json, "sound", "")) : null,
                responses);
    }

    private static SendResponseAction parseResponse(JsonObject json) {
        ResourceLocation icon = resource(GsonHelper.getAsString(json, "icon", "block_party:close_dialogue"));
        return new SendResponseAction(
                Response.CLOSE_DIALOGUE.fromValue(icon),
                GsonHelper.getAsString(json, "text", ""),
                parseActions(json.has("actions") && json.get("actions").isJsonArray() ? json.getAsJsonArray("actions") : new JsonArray()));
    }

    private static Speaker parseSpeaker(JsonObject json) {
        boolean speaks = GsonHelper.getAsBoolean(json, "speaks", false);
        return new Speaker(
                Speaker.Identity.CHARACTER.fromValue(GsonHelper.getAsString(json, "identity", "character")),
                Speaker.Position.LEFT.fromValue(GsonHelper.getAsString(json, "position", "left")),
                GsonHelper.getAsString(json, "animation", "DEFAULT"),
                GsonHelper.getAsString(json, "emotion", "NORMAL").toUpperCase(java.util.Locale.ROOT),
                speaks,
                speaks ? resource(GsonHelper.getAsString(json, "voice", "")) : null,
                GsonHelper.getAsFloat(json, "scale", 1.0F));
    }

    private static ResourceLocation resource(String value) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        return id == null ? BlockParty.source("") : id;
    }

    private static ResourceLocation own(ResourceLocation id) {
        return "minecraft".equals(id.getNamespace()) ? BlockParty.source(id.getPath()) : id;
    }

    private static ResourceLocation resourceId(ResourceLocation fileId) {
        String path = fileId.getPath();
        if (path.startsWith(DIRECTORY + "/")) {
            path = path.substring(DIRECTORY.length() + 1);
        }
        if (path.endsWith(".json")) {
            path = path.substring(0, path.length() - ".json".length());
        }
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), path);
    }

    public record ParsedScene(SceneTrigger trigger, Scene scene) {
    }

    private record LoadedScenes(Map<SceneTrigger, List<Scene>> byTrigger, Map<ResourceLocation, Scene> byName) {
    }
}
