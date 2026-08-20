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
import block_party.scene.SceneVariableScope;
import block_party.scene.Speaker;
import block_party.scene.actions.CookieAction;
import block_party.scene.actions.AcceptOfferedGiftAction;
import block_party.scene.actions.CounterAction;
import block_party.scene.actions.CreateVoicemailAction;
import block_party.scene.actions.ClearRoutineIntentAction;
import block_party.scene.actions.ClearFollowSessionAction;
import block_party.scene.actions.EndAction;
import block_party.scene.actions.GiveItemAction;
import block_party.scene.actions.GoToAnchorAction;
import block_party.scene.actions.HideAction;
import block_party.scene.actions.MarkTimeAction;
import block_party.scene.actions.LocationAction;
import block_party.scene.actions.SceneDirectiveAction;
import block_party.scene.actions.StageAction;
import block_party.scene.actions.TimedSceneAction;
import block_party.scene.actions.OpenInventoryAction;
import block_party.scene.actions.RefreshWoodFamilyProgressionAction;
import block_party.scene.actions.RefreshSamuraiProgressionAction;
import block_party.scene.actions.ResetProgressionCountersAction;
import block_party.scene.actions.SceneItemStacks;
import block_party.scene.actions.SendDialogueAction;
import block_party.scene.actions.SendResponseAction;
import block_party.scene.actions.SetHomeToAnchorAction;
import block_party.scene.actions.SetRoutineIntentAction;
import block_party.scene.actions.SleepAtHomeAction;
import block_party.scene.actions.StartFollowSessionAction;
import block_party.scene.actions.StatAction;
import block_party.scene.actions.TakeItemAction;
import block_party.entities.movement.PlayerMovementIntent;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.SceneFilters;
import block_party.world.progression.WoodFamilyProgression;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class ScenesReloadListener implements PreparableReloadListener {
    private static final String DIRECTORY = "scenes";
    private static final Map<String, ActionParser> ACTION_PARSERS = actionParsers();
    private static volatile int loadedCount;

    private Map<SceneTrigger, List<Scene>> scenes = Map.of();
    private Map<ResourceLocation, Scene> byName = Map.of();
    private List<ContentValidationIssue> validationIssues = List.of();

    public static int loadedCount() {
        return loadedCount;
    }

    public static Set<String> supportedActionPaths() {
        return ACTION_PARSERS.keySet();
    }

    public int sceneCount() {
        return this.byName.size();
    }

    public Scene get(ResourceLocation id) {
        return this.byName.get(own(id));
    }

    public Set<ResourceLocation> sceneIds() {
        return this.byName.keySet();
    }

    public List<ContentValidationIssue> validationIssues() {
        return this.validationIssues;
    }

    public Scene get(SceneTrigger trigger, Moe moe) {
        List<Scene> candidates = new ArrayList<>(this.scenes.getOrDefault(trigger, List.of()));
        if (candidates.isEmpty()) {
            return null;
        }
        candidates.removeIf(scene -> !scene.fulfills(moe));
        long gameTime = moe.level().getGameTime();
        candidates.removeIf(scene -> !moe.sceneSelectionMemory().eligible(scene, gameTime));
        if (candidates.isEmpty()) {
            return null;
        }
        int mostSpecific = candidates.stream().mapToInt(Scene::filterCount).max().orElse(0);
        candidates.removeIf(scene -> scene.filterCount() < mostSpecific);
        List<Scene> selectable = List.copyOf(candidates);
        candidates.removeIf(scene -> moe.sceneSelectionMemory().repeatedInGroup(scene)
                && selectable.stream().anyMatch(other -> other != scene
                        && other.selection().group().equals(scene.selection().group())));
        int totalWeight = candidates.stream().mapToInt(scene -> scene.selection().weight()).sum();
        int choice = moe.getRandom().nextInt(Math.max(1, totalWeight));
        for (Scene scene : candidates) {
            choice -= scene.selection().weight();
            if (choice < 0) return scene;
        }
        return candidates.getLast();
    }

    public List<SceneDebugResult> debug(SceneTrigger trigger, Moe moe) {
        return this.scenes.getOrDefault(trigger, List.of()).stream()
                .map(scene -> {
                    var diagnostic = scene.diagnose(moe);
                    return new SceneDebugResult(scene.id(), diagnostic.passed(), scene.filterCount(), diagnostic.reasons());
                })
                .sorted((left, right) -> left.id().toString().compareTo(right.id().toString()))
                .toList();
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            Executor backgroundExecutor,
            Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> load(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::applyLoaded, gameExecutor);
    }

    private static LoadedScenes load(ResourceManager resourceManager) {
        Map<ResourceLocation, JsonObject> rawScenes = new LinkedHashMap<>();
        List<ContentValidationIssue> issues = new ArrayList<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation id = own(resourceId(entry.getKey()));
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject json = GsonHelper.convertToJsonObject(JsonParser.parseReader(reader), "scene " + id);
                rawScenes.put(id, json);
            } catch (Exception exception) {
                issues.add(new ContentValidationIssue(id, "failed to read scene: " + exception.getMessage(), true));
            }
        }
        return load(rawScenes, issues);
    }

    public static LoadedScenes loadScenesForTests(Map<ResourceLocation, JsonObject> rawScenes) {
        return load(rawScenes, new ArrayList<>());
    }

    private static LoadedScenes load(Map<ResourceLocation, JsonObject> rawScenes, List<ContentValidationIssue> issues) {
        issues.addAll(validate(rawScenes));
        Set<ResourceLocation> rejected = rejectedSceneIds(issues);
        Map<SceneTrigger, List<Scene>> byTrigger = new ConcurrentHashMap<>();
        Map<ResourceLocation, Scene> byName = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, JsonObject> entry : rawScenes.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (rejected.contains(id)) {
                continue;
            }
            try {
                ParsedScene parsed = parseScene(id, entry.getValue());
                byTrigger.computeIfAbsent(parsed.trigger(), ignored -> new ArrayList<>()).add(parsed.scene());
                byName.put(id, parsed.scene());
            } catch (Exception exception) {
                issues.add(new ContentValidationIssue(id, "failed to parse scene: " + exception.getMessage(), true));
            }
        }
        Map<SceneTrigger, List<Scene>> immutableByTrigger = new LinkedHashMap<>();
        byTrigger.forEach((trigger, scenes) -> immutableByTrigger.put(trigger, List.copyOf(scenes)));
        return new LoadedScenes(Map.copyOf(immutableByTrigger), Map.copyOf(byName), List.copyOf(issues));
    }

    private static Set<ResourceLocation> rejectedSceneIds(List<ContentValidationIssue> issues) {
        Set<ResourceLocation> rejected = new HashSet<>();
        for (ContentValidationIssue issue : issues) {
            if (issue.rejectScene()) {
                rejected.add(issue.sceneId());
            }
        }
        return rejected;
    }

    private void applyLoaded(LoadedScenes loaded) {
        this.scenes = loaded.byTrigger();
        this.byName = loaded.byName();
        this.validationIssues = loaded.validationIssues();
        loadedCount = loaded.byName().size();
        if (!this.validationIssues.isEmpty()) {
            BlockParty.LOGGER.warn("[Block Party Content] {} scene validation issue(s)", this.validationIssues.size());
            this.validationIssues.stream().limit(50).forEach(issue ->
                    BlockParty.LOGGER.warn("[Block Party Content] {}: {}", issue.sceneId(), issue.message()));
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().broadcastSystemMessage(Component.literal(
                        "[Block Party Content] " + this.validationIssues.size() + " scene validation issue(s)")
                        .withStyle(ChatFormatting.RED), false);
                this.validationIssues.forEach(issue -> server.getPlayerList().broadcastSystemMessage(
                        Component.literal((issue.rejectScene() ? "REJECTED " : "WARNING ")
                                + issue.sceneId() + ": " + issue.message())
                                .withStyle(issue.rejectScene() ? ChatFormatting.RED : ChatFormatting.YELLOW), false));
            }
        }
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
        List<SceneAction> actions = parseActions(optionalArray(json, "actions", "scene " + id), "scene " + id);
        JsonObject selectionJson = json.has("selection") && json.get("selection").isJsonObject()
                ? json.getAsJsonObject("selection") : new JsonObject();
        Scene.Selection selection = new Scene.Selection(
                GsonHelper.getAsString(selectionJson, "group", ""),
                GsonHelper.getAsInt(selectionJson, "weight", 1),
                GsonHelper.getAsInt(selectionJson, "cooldown_ticks", 0));
        return new ParsedScene(trigger, new Scene(id, filters, actions, selection));
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

    private static List<SceneAction> parseActions(JsonArray array, String context) {
        List<SceneAction> actions = new ArrayList<>();
        for (int index = 0; index < array.size(); ++index) {
            actions.add(parseAction(array.get(index), context + " actions[" + index + "]"));
        }
        return actions;
    }

    private static SceneAction parseAction(JsonElement element) {
        return parseAction(element, "test action");
    }

    private static SceneAction parseAction(JsonObject json) {
        return parseAction(json, "test action");
    }

    private static SceneAction parseAction(JsonElement element, String context) {
        if (element.isJsonObject()) {
            return parseAction(element.getAsJsonObject(), context);
        }
        if (!element.isJsonPrimitive()) {
            throw new IllegalArgumentException("Scene action " + context + " must be a string ID or object");
        }
        return parseStringAction(actionId(element.getAsString(), context), context);
    }

    private static SceneAction parseAction(JsonObject json, String context) {
        ResourceLocation type = actionType(json, context);
        JsonObject payload = actionPayload(json, context);
        ActionParser parser = ACTION_PARSERS.get(type.getPath());
        if (parser == null) {
            throw unknownAction(type, context);
        }
        return parser.parse(payload);
    }

    private static SceneAction parseStringAction(ResourceLocation type, String context) {
        if ("end".equals(type.getPath())) {
            return EndAction.INSTANCE;
        }
        if (ACTION_PARSERS.containsKey(type.getPath())) {
            throw new IllegalArgumentException("Scene action " + context + " uses " + type
                    + " in string form; only block_party:end supports string form. Use an object with a type field.");
        }
        throw unknownAction(type, context);
    }

    private static Map<String, ActionParser> actionParsers() {
        Map<String, ActionParser> parsers = new LinkedHashMap<>();
        parsers.put("send_dialogue", ScenesReloadListener::parseDialogue);
        parsers.put("send_response", ScenesReloadListener::parseResponse);
        parsers.put("health", payload -> parseStat(StatAction.Stat.HEALTH, payload));
        parsers.put("food_level", payload -> parseStat(StatAction.Stat.FOOD_LEVEL, payload));
        parsers.put("loyalty", payload -> parseStat(StatAction.Stat.LOYALTY, payload));
        parsers.put("stress", payload -> parseStat(StatAction.Stat.STRESS, payload));
        parsers.put("cookie", payload -> new CookieAction(
                CookieAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "set")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsString(payload, "value", ""),
                variableScope(payload, SceneVariableScope.NPC)));
        parsers.put("player_cookie", payload -> new CookieAction(
                CookieAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "set")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsString(payload, "value", ""),
                SceneVariableScope.PLAYER));
        parsers.put("world_cookie", payload -> new CookieAction(
                CookieAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "set")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsString(payload, "value", ""),
                SceneVariableScope.WORLD));
        parsers.put("counter", payload -> new CounterAction(
                CounterAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "add")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsInt(payload, "value", 1),
                variableScope(payload, SceneVariableScope.NPC)));
        parsers.put("mark_time", payload -> new MarkTimeAction(
                GsonHelper.getAsString(payload, "name", ""),
                variableScope(payload, SceneVariableScope.NPC)));
        parsers.put("remember_location", payload -> new LocationAction(
                LocationAction.Operation.REMEMBER,
                GsonHelper.getAsString(payload, "name", ""),
                variableScope(payload, SceneVariableScope.NPC),
                LocationAction.Source.fromValue(GsonHelper.getAsString(payload, "source", "moe"))));
        parsers.put("forget_location", payload -> new LocationAction(
                LocationAction.Operation.FORGET,
                GsonHelper.getAsString(payload, "name", ""),
                variableScope(payload, SceneVariableScope.NPC), LocationAction.Source.MOE));
        parsers.put("assign_location", payload -> new SceneDirectiveAction(
                SceneDirectiveAction.Operation.ASSIGN_LOCATION,
                GsonHelper.getAsString(payload, "name", ""), GsonHelper.getAsString(payload, "id", ""),
                variableScope(payload, SceneVariableScope.NPC), null, "", 0, 0,
                GsonHelper.getAsDouble(payload, "speed", 1.0D),
                GsonHelper.getAsDouble(payload, "arrival_radius", 2.0D),
                GsonHelper.getAsInt(payload, "timeout_ticks", 1200)));
        parsers.put("assign_target", payload -> new SceneDirectiveAction(
                SceneDirectiveAction.Operation.ASSIGN_TARGET, "", GsonHelper.getAsString(payload, "id", ""), SceneVariableScope.NPC,
                SceneDirectiveAction.TargetSelector.fromValue(GsonHelper.getAsString(payload, "selector", "owner")),
                "", 0, 0,
                GsonHelper.getAsDouble(payload, "speed", 1.0D),
                GsonHelper.getAsDouble(payload, "arrival_radius", 2.0D),
                GsonHelper.getAsInt(payload, "timeout_ticks", 1200)));
        parsers.put("assign_near_block", payload -> new SceneDirectiveAction(
                SceneDirectiveAction.Operation.ASSIGN_BLOCK, "", GsonHelper.getAsString(payload, "id", ""),
                SceneVariableScope.NPC, null, GsonHelper.getAsString(payload, "block", ""),
                GsonHelper.getAsInt(payload, "search_radius", 16), GsonHelper.getAsInt(payload, "vertical_radius", 4),
                GsonHelper.getAsDouble(payload, "speed", 1.0D),
                GsonHelper.getAsDouble(payload, "arrival_radius", 2.0D),
                GsonHelper.getAsInt(payload, "timeout_ticks", 1200)));
        parsers.put("clear_assignment", payload -> new SceneDirectiveAction(
                SceneDirectiveAction.Operation.CLEAR, "", "", SceneVariableScope.NPC, null, "", 0, 0, 1.0D, 2.0D, 0));
        parsers.put("consume_assignment_result", payload -> moe -> moe.sceneDirective().consumeResult());
        parsers.put("wait_ticks", payload -> new TimedSceneAction(TimedSceneAction.Kind.WAIT, "",
                GsonHelper.getAsInt(payload, "ticks", 20), GsonHelper.getAsInt(payload, "ticks", 20)));
        parsers.put("wait_random_ticks", payload -> new TimedSceneAction(TimedSceneAction.Kind.WAIT, "",
                GsonHelper.getAsInt(payload, "min_ticks", 20), GsonHelper.getAsInt(payload, "max_ticks", 60)));
        parsers.put("play_animation", payload -> new TimedSceneAction(TimedSceneAction.Kind.ANIMATION,
                GsonHelper.getAsString(payload, "animation", "DEFAULT"),
                GsonHelper.getAsInt(payload, "ticks", 40), GsonHelper.getAsInt(payload, "ticks", 40)));
        parsers.put("set_emotion", payload -> new TimedSceneAction(TimedSceneAction.Kind.EMOTION,
                GsonHelper.getAsString(payload, "emotion", "NORMAL"),
                GsonHelper.getAsInt(payload, "ticks", 40), GsonHelper.getAsInt(payload, "ticks", 40)));
        parsers.put("sit", payload -> new StageAction(StageAction.Operation.SIT));
        parsers.put("stand", payload -> new StageAction(StageAction.Operation.STAND));
        parsers.put("jump", payload -> new StageAction(StageAction.Operation.JUMP));
        parsers.put("swing_hand", payload -> new StageAction(StageAction.Operation.SWING_HAND));
        parsers.put("look_at_assignment", payload -> new StageAction(StageAction.Operation.LOOK_AT_ASSIGNMENT));
        parsers.put("refresh_wood_family_progression", payload -> RefreshWoodFamilyProgressionAction.INSTANCE);
        parsers.put("refresh_samurai_progression", payload -> RefreshSamuraiProgressionAction.INSTANCE);
        parsers.put("reset_progression_counters", payload -> new ResetProgressionCountersAction(
                ResetProgressionCountersAction.Kind.fromValue(GsonHelper.getAsString(payload, "kind", "item")),
                resource(GsonHelper.getAsString(payload, "id", ""))));
        parsers.put("player_counter", payload -> new CounterAction(
                CounterAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "add")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsInt(payload, "value", 1),
                SceneVariableScope.PLAYER));
        parsers.put("world_counter", payload -> new CounterAction(
                CounterAction.Operation.fromValue(GsonHelper.getAsString(payload, "operation", "add")),
                GsonHelper.getAsString(payload, "name", ""),
                GsonHelper.getAsInt(payload, "value", 1),
                SceneVariableScope.WORLD));
        parsers.put("hide", payload -> new HideAction(HideUntil.EXPOSED.fromValue(GsonHelper.getAsString(payload, "until", "exposed"))));
        parsers.put("create_voicemail", payload -> new CreateVoicemailAction(
                GsonHelper.getAsString(payload, "text", ""),
                GsonHelper.getAsBoolean(payload, "tooltip", true),
                parseSpeaker(payload.has("speaker") && payload.get("speaker").isJsonObject() ? payload.getAsJsonObject("speaker") : new JsonObject()),
                payload.has("sound") ? resource(GsonHelper.getAsString(payload, "sound", "")) : null,
                voicemailDelayMillis(payload)));
        parsers.put("start_follow_session", payload -> new StartFollowSessionAction(
                parseMovementIntent(GsonHelper.getAsString(payload, "intent", "follow_request")),
                Math.max(0, GsonHelper.getAsInt(payload, "ticks", 20 * 60)),
                GsonHelper.getAsBoolean(payload, "can_change_dimension", false),
                GsonHelper.getAsBoolean(payload, "trigger_scene", false)));
        parsers.put("clear_follow_session", payload -> ClearFollowSessionAction.INSTANCE);
        parsers.put("go_to_anchor", payload -> new GoToAnchorAction(GsonHelper.getAsDouble(payload, "speed", 1.0D)));
        parsers.put("set_home_to_anchor", payload -> SetHomeToAnchorAction.INSTANCE);
        parsers.put("set_routine_intent", payload -> new SetRoutineIntentAction(RoutineIntent.fromValue(GsonHelper.getAsString(payload, "intent", "idle"))));
        parsers.put("clear_routine_intent", payload -> ClearRoutineIntentAction.INSTANCE);
        parsers.put("sleep_at_home", payload -> new SleepAtHomeAction(HideUntil.EXPOSED.fromValue(GsonHelper.getAsString(payload, "until", "exposed"))));
        parsers.put("open_inventory", payload -> OpenInventoryAction.INSTANCE);
        parsers.put("accept_offered_gift", payload -> AcceptOfferedGiftAction.INSTANCE);
        parsers.put("give_item", payload -> new GiveItemAction(
                SceneItemStacks.parse(payload),
                GiveItemAction.Target.fromValue(GsonHelper.getAsString(payload, "target", "player"))));
        parsers.put("take_item", payload -> new TakeItemAction(
                payload,
                Math.max(1, GsonHelper.getAsInt(payload, "count", 1)),
                TakeItemAction.Source.fromValue(GsonHelper.getAsString(payload, "source", "player")),
                TakeItemAction.Destination.fromValue(GsonHelper.getAsString(payload, "destination", "moe"))));
        parsers.put("wait", payload -> ClearFollowSessionAction.INSTANCE);
        parsers.put("dismiss", payload -> ClearFollowSessionAction.INSTANCE);
        parsers.put("end", payload -> EndAction.INSTANCE);
        return Collections.unmodifiableMap(parsers);
    }

    private static SceneVariableScope variableScope(JsonObject payload, SceneVariableScope fallback) {
        String key = payload.has("scope") ? "scope" : "target";
        return SceneVariableScope.fromValue(GsonHelper.getAsString(payload, key, fallback.serializedName()), fallback);
    }

    private static StatAction parseStat(StatAction.Stat stat, JsonObject json) {
        return new StatAction(
                stat,
                StatAction.Operation.fromValue(GsonHelper.getAsString(json, "operation", "add")),
                GsonHelper.getAsFloat(json, "value", 0.0F));
    }

    private static PlayerMovementIntent parseMovementIntent(String value) {
        try {
            return PlayerMovementIntent.valueOf(value.toUpperCase(Locale.ROOT));
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

    private static ResourceLocation actionType(JsonObject json, String context) {
        if (json.has("type")) {
            return actionId(GsonHelper.getAsString(json, "type", "block_party:end"), context + " type");
        }
        if (json.has("action") && json.get("action").isJsonPrimitive()) {
            return actionId(GsonHelper.getAsString(json, "action", "block_party:end"), context + " action");
        }
        throw new IllegalArgumentException("Scene action " + context + " must include a string 'type' field");
    }

    private static JsonObject actionPayload(JsonObject json, String context) {
        if (!json.has("action")) {
            return json;
        }
        if (json.get("action").isJsonObject()) {
            return json.getAsJsonObject("action");
        }
        if (json.get("action").isJsonPrimitive() && !json.has("type")) {
            return json;
        }
        throw new IllegalArgumentException("Scene action " + context + " field 'action' must be an object payload");
    }

    private static JsonArray optionalArray(JsonObject json, String field, String context) {
        if (!json.has(field)) {
            return new JsonArray();
        }
        if (!json.get(field).isJsonArray()) {
            throw new IllegalArgumentException("Scene " + context + " field '" + field + "' must be an array");
        }
        return json.getAsJsonArray(field);
    }

    private static List<ContentValidationIssue> validate(Map<ResourceLocation, JsonObject> rawScenes) {
        Set<String> writtenCookies = knownProgressionCookies();
        rawScenes.values().forEach(json -> collectWrittenCookies(json, writtenCookies));
        List<ContentValidationIssue> issues = new ArrayList<>();
        rawScenes.forEach((id, json) -> validateScene(id, json, writtenCookies, issues));
        return List.copyOf(issues);
    }

    private static Set<String> knownProgressionCookies() {
        Set<String> cookies = new HashSet<>();
        cookies.add(WoodFamilyProgression.OAK_REPLENISHMENT_SEEN);
        cookies.add(WoodFamilyProgression.BIRCH_REPLENISHMENT_SEEN);
        cookies.add(WoodFamilyProgression.SPRUCE_WASTE_AVOIDED);
        cookies.add(WoodFamilyProgression.ACACIA_CLEAN_USE_SEEN);
        cookies.add(WoodFamilyProgression.JUNGLE_REPLENISHMENT_SEEN);
        cookies.add(WoodFamilyProgression.DARK_OAK_REPLENISHMENT_SEEN);
        cookies.add(WoodFamilyProgression.OAK_BEFRIENDED);
        cookies.add(WoodFamilyProgression.BIRCH_BEFRIENDED);
        cookies.add(WoodFamilyProgression.SPRUCE_BEFRIENDED);
        cookies.add(WoodFamilyProgression.ACACIA_BEFRIENDED);
        cookies.add(WoodFamilyProgression.JUNGLE_BEFRIENDED);
        cookies.add(WoodFamilyProgression.DARK_OAK_BEFRIENDED);
        cookies.add(WoodFamilyProgression.WOOD_FAMILY_ARC_READY);
        return cookies;
    }

    private static void validateScene(ResourceLocation id, JsonObject json, Set<String> writtenCookies, List<ContentValidationIssue> issues) {
        ResourceLocation trigger = resource(GsonHelper.getAsString(json, "trigger", "block_party:null"));
        if (SceneTrigger.NULL.fromValue(own(trigger)) == SceneTrigger.NULL && !"null".equals(own(trigger).getPath())) {
            issues.add(new ContentValidationIssue(id, "unknown trigger: " + trigger, true));
        }
        if (json.has("filters") && json.get("filters").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("filters")) {
                validateFilter(id, element, writtenCookies, issues);
            }
        }
        if (json.has("actions") && json.get("actions").isJsonArray()) {
            validateActions(id, json.getAsJsonArray("actions"), issues);
        }
        if (json.has("selection") && json.get("selection").isJsonObject()) {
            JsonObject selection = json.getAsJsonObject("selection");
            if (GsonHelper.getAsInt(selection, "weight", 1) <= 0
                    || GsonHelper.getAsInt(selection, "cooldown_ticks", 0) < 0) {
                issues.add(new ContentValidationIssue(id, "selection weight must be positive and cooldown cannot be negative", true));
            }
        }
    }

    private static void validateFilter(ResourceLocation id, JsonElement element, Set<String> writtenCookies, List<ContentValidationIssue> issues) {
        ResourceLocation type = filterType(element);
        if (type == null) {
            issues.add(new ContentValidationIssue(id, "invalid filter entry", true));
            return;
        }
        if (!SceneFilters.ENTRIES.containsKey(type.getPath())) {
            issues.add(new ContentValidationIssue(id, "unknown filter: " + type));
        }
        JsonObject payload = filterPayload(element);
        switch (type.getPath()) {
            case "has_cookie", "player_has_cookie", "world_has_cookie" -> validateCookieReference(id, payload, writtenCookies, issues);
            case "held_item", "player_held_item", "attention_item", "gift_item" -> validateItem(id, payload, issues);
            case "has_item", "moe_has_item", "player_has_item" -> validateItem(id, payload, issues);
            case "block", "observed_block", "social_target_block", "near_block" -> validateBlock(id, payload, issues);
            case "self" -> validateEntity(id, payload, issues);
            case "dimension" -> validateResourceField(id, payload, "value", false, issues);
            case "location_dimension" -> {
                validateResourceField(id, payload, "value", false, issues);
                validateLocationName(id, payload, issues);
            }
            case "biome" -> validateResourceField(id, payload, "value", true, issues);
            case "has_location", "at_location", "distance_to_location" -> validateLocationName(id, payload, issues);
        }
    }

    private static void validateActions(ResourceLocation id, JsonArray actions, List<ContentValidationIssue> issues) {
        for (JsonElement element : actions) {
            if (element.isJsonPrimitive()) {
                ResourceLocation action = ResourceLocation.tryParse(element.getAsString());
                if (action == null || (!"end".equals(own(action).getPath()) && !ACTION_PARSERS.containsKey(own(action).getPath()))) {
                    issues.add(new ContentValidationIssue(id, "unknown action: " + element.getAsString(), true));
                } else if (!"end".equals(own(action).getPath())) {
                    issues.add(new ContentValidationIssue(id, "string action uses " + own(action)
                            + "; only block_party:end supports string form", true));
                }
                continue;
            }
            if (!element.isJsonObject()) {
                issues.add(new ContentValidationIssue(id, "invalid action entry", true));
                continue;
            }
            JsonObject source = element.getAsJsonObject();
            ResourceLocation action = actionTypeForValidation(source);
            if (action == null) {
                issues.add(new ContentValidationIssue(id, "invalid action entry", true));
                continue;
            }
            if (source.has("action") && !source.get("action").isJsonObject()
                    && !(source.get("action").isJsonPrimitive() && !source.has("type"))) {
                issues.add(new ContentValidationIssue(id, "invalid action payload for " + action, true));
                continue;
            }
            if (!ACTION_PARSERS.containsKey(action.getPath()) && !"end".equals(action.getPath())) {
                issues.add(new ContentValidationIssue(id, "unknown action: " + action, true));
            }
            JsonObject payload = source.has("action") && source.get("action").isJsonObject() ? source.getAsJsonObject("action") : source;
            validateActionPayload(id, action.getPath(), payload, issues);
        }
    }

    private static ResourceLocation actionTypeForValidation(JsonObject source) {
        if (source.has("type")) {
            ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(source, "type", "block_party:end"));
            return id == null ? null : own(id);
        }
        if (source.has("action") && source.get("action").isJsonPrimitive()) {
            ResourceLocation id = ResourceLocation.tryParse(GsonHelper.getAsString(source, "action", "block_party:end"));
            return id == null ? null : own(id);
        }
        return null;
    }

    private static void validateActionPayload(ResourceLocation id, String action, JsonObject payload, List<ContentValidationIssue> issues) {
        switch (action) {
            case "send_dialogue" -> {
                validateDialogueText(id, payload, issues);
                if (payload.has("responses") && payload.get("responses").isJsonArray()) {
                    for (JsonElement responseElement : payload.getAsJsonArray("responses")) {
                        if (responseElement.isJsonObject()) {
                            JsonObject response = responseElement.getAsJsonObject();
                            validateDialogueText(id, response, issues);
                            if (response.has("actions") && response.get("actions").isJsonArray()) {
                                validateActions(id, response.getAsJsonArray("actions"), issues);
                            }
                        }
                    }
                }
            }
            case "give_item", "take_item" -> validateItem(id, payload, issues);
            case "create_voicemail" -> validateDialogueText(id, payload, issues);
            case "remember_location", "forget_location", "assign_location" -> {
                validateLocationName(id, payload, issues);
                if ("remember_location".equals(action) && !Set.of(
                        "moe", "player", "home", "current_anchor", "remembered_place")
                        .contains(GsonHelper.getAsString(payload, "source", "moe").toLowerCase(Locale.ROOT))) {
                    issues.add(new ContentValidationIssue(id, "invalid location source", true));
                }
                if ("assign_location".equals(action)) validateDirectiveNumbers(id, payload, issues);
            }
            case "assign_target" -> {
                if (!Set.of("owner", "dialogue_player", "social_target", "nearest_moe")
                        .contains(GsonHelper.getAsString(payload, "selector", "owner").toLowerCase(Locale.ROOT))) {
                    issues.add(new ContentValidationIssue(id, "invalid assignment target selector", true));
                }
                validateDirectiveNumbers(id, payload, issues);
            }
            case "assign_near_block" -> {
                validateBlock(id, payload, issues);
                validateDirectiveNumbers(id, payload, issues);
                if (GsonHelper.getAsInt(payload, "search_radius", 16) < 1
                        || GsonHelper.getAsInt(payload, "search_radius", 16) > 32
                        || GsonHelper.getAsInt(payload, "vertical_radius", 4) < 0
                        || GsonHelper.getAsInt(payload, "vertical_radius", 4) > 16) {
                    issues.add(new ContentValidationIssue(id, "block assignment search radius must be 1-32 and vertical radius 0-16", true));
                }
            }
            case "wait_ticks", "play_animation", "set_emotion" -> {
                if (GsonHelper.getAsInt(payload, "ticks", 20) < 0) {
                    issues.add(new ContentValidationIssue(id, "timed action ticks cannot be negative", true));
                }
                if ("play_animation".equals(action) && !Set.of(
                        "DEFAULT", "AWE", "BEG", "HAPPY_DANCE", "LOOK_AROUND", "SHIVER", "YEARBOOK", "WAVE")
                        .contains(GsonHelper.getAsString(payload, "animation", "").toUpperCase(Locale.ROOT))) {
                    issues.add(new ContentValidationIssue(id, "invalid animation", true));
                }
                if ("set_emotion".equals(action) && !Set.of(
                        "ANGRY", "BEGGING", "CONFUSED", "CRYING", "MISCHIEVOUS", "EMBARRASSED", "HAPPY",
                        "NORMAL", "PAINED", "PSYCHOTIC", "SCARED", "SICK", "SNOOTY", "SMITTEN", "TIRED")
                        .contains(GsonHelper.getAsString(payload, "emotion", "").toUpperCase(Locale.ROOT))) {
                    issues.add(new ContentValidationIssue(id, "invalid emotion", true));
                }
            }
            case "wait_random_ticks" -> {
                int minimum = GsonHelper.getAsInt(payload, "min_ticks", 20);
                int maximum = GsonHelper.getAsInt(payload, "max_ticks", 60);
                if (minimum < 0 || maximum < minimum) {
                    issues.add(new ContentValidationIssue(id, "random wait requires 0 <= min_ticks <= max_ticks", true));
                }
            }
        }
    }

    private static void validateDirectiveNumbers(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        if (GsonHelper.getAsDouble(payload, "speed", 1.0D) <= 0.0D
                || GsonHelper.getAsDouble(payload, "arrival_radius", 2.0D) < 0.0D
                || GsonHelper.getAsInt(payload, "timeout_ticks", 1200) < 0) {
            issues.add(new ContentValidationIssue(id, "assignment speed must be positive; radius and timeout cannot be negative", true));
        }
    }

    private static void validateLocationName(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        String name = GsonHelper.getAsString(payload, "name", "");
        if (!name.matches("[a-z0-9_.-]+")) {
            issues.add(new ContentValidationIssue(id, "invalid named location: " + name, true));
        }
    }

    private static void validateResourceField(ResourceLocation id, JsonObject payload, String field, boolean tagAllowed,
            List<ContentValidationIssue> issues) {
        String value = GsonHelper.getAsString(payload, field, "");
        String resource = tagAllowed && value.startsWith("#") ? value.substring(1) : value;
        if (ResourceLocation.tryParse(resource) == null || (!tagAllowed && value.startsWith("#"))) {
            issues.add(new ContentValidationIssue(id, "invalid " + field + " resource: " + value, true));
        }
    }

    private static void validateDialogueText(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        if (!payload.has("text") || !payload.get("text").isJsonPrimitive()) {
            return;
        }
        String text = payload.get("text").getAsString();
        String key = text.startsWith("translate:") ? text.substring("translate:".length()) : text;
        if (key.startsWith("dialogue.") && !languageKeys().contains(key)) {
            issues.add(new ContentValidationIssue(id, "missing localization: " + key));
        }
    }

    private static void validateCookieReference(ResourceLocation id, JsonObject payload, Set<String> writtenCookies, List<ContentValidationIssue> issues) {
        String name = GsonHelper.getAsString(payload, "name", "");
        if (!name.isBlank() && !writtenCookies.contains(name)) {
            issues.add(new ContentValidationIssue(id, "unknown flag: " + name));
        }
    }

    private static void validateItem(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        String value = GsonHelper.getAsString(payload, payload.has("item") ? "item" : "name", "");
        ResourceLocation parsed = ResourceLocation.tryParse(value);
        if (!value.isBlank() && !value.startsWith("#") && (parsed == null || !BuiltInRegistries.ITEM.containsKey(parsed))) {
            issues.add(new ContentValidationIssue(id, "references unknown item: " + value, true));
        }
    }

    private static void validateBlock(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        String value = GsonHelper.getAsString(payload, payload.has("block") ? "block" : "name", "");
        ResourceLocation parsed = ResourceLocation.tryParse(value);
        if (!value.isBlank() && !value.startsWith("#") && (parsed == null || !BuiltInRegistries.BLOCK.containsKey(parsed))) {
            issues.add(new ContentValidationIssue(id, "references unknown block: " + value, true));
        }
    }

    private static void validateEntity(ResourceLocation id, JsonObject payload, List<ContentValidationIssue> issues) {
        String value = GsonHelper.getAsString(payload, "name", "");
        ResourceLocation parsed = ResourceLocation.tryParse(value);
        if (!value.isBlank() && !value.startsWith("#") && (parsed == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(parsed))) {
            issues.add(new ContentValidationIssue(id, "references unknown entity: " + value, true));
        }
    }

    private static void collectWrittenCookies(JsonElement element, Set<String> cookies) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            String type = object.has("type") && object.get("type").isJsonPrimitive()
                    ? own(resource(object.get("type").getAsString())).getPath()
                    : "";
            JsonObject payload = object.has("action") && object.get("action").isJsonObject() ? object.getAsJsonObject("action") : object;
            if (List.of("cookie", "player_cookie", "world_cookie").contains(type) && payload.has("name")) {
                cookies.add(payload.get("name").getAsString());
            }
            object.entrySet().forEach(entry -> collectWrittenCookies(entry.getValue(), cookies));
        } else if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(member -> collectWrittenCookies(member, cookies));
        }
    }

    private static ResourceLocation filterType(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return own(resource(element.getAsString()));
        }
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject source = element.getAsJsonObject();
        if (source.has("type")) {
            return own(resource(GsonHelper.getAsString(source, "type", "block_party:always")));
        }
        if (source.has("filter") && source.get("filter").isJsonPrimitive()) {
            return own(resource(GsonHelper.getAsString(source, "filter", "block_party:always")));
        }
        return BlockParty.source("always");
    }

    private static JsonObject filterPayload(JsonElement element) {
        if (!element.isJsonObject()) {
            return new JsonObject();
        }
        JsonObject source = element.getAsJsonObject();
        return source.has("filter") && source.get("filter").isJsonObject() ? source.getAsJsonObject("filter") : source;
    }

    private static Set<String> languageKeys() {
        try (Reader reader = new InputStreamReader(ScenesReloadListener.class.getClassLoader()
                .getResourceAsStream("assets/block_party/lang/en_us.json"), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return json.keySet();
        } catch (Exception ignored) {
            return Set.of();
        }
    }

    private static ResourceLocation actionId(String value, String context) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null) {
            throw new IllegalArgumentException("Scene action " + context + " has invalid action ID '" + value + "'");
        }
        return own(id);
    }

    private static IllegalArgumentException unknownAction(ResourceLocation type, String context) {
        return new IllegalArgumentException("Unknown scene action ID " + type + " at " + context
                + ". Supported action IDs: " + ACTION_PARSERS.keySet());
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
                parseActions(optionalArray(json, "actions", "send_response action"), "send_response action"));
    }

    private static Speaker parseSpeaker(JsonObject json) {
        boolean speaks = GsonHelper.getAsBoolean(json, "speaks", false);
        return new Speaker(
                Speaker.Identity.CHARACTER.fromValue(GsonHelper.getAsString(json, "identity", "character")),
                Speaker.Position.LEFT.fromValue(GsonHelper.getAsString(json, "position", "left")),
                GsonHelper.getAsString(json, "animation", "DEFAULT"),
                GsonHelper.getAsString(json, "emotion", "NORMAL").toUpperCase(Locale.ROOT),
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

    public record SceneDebugResult(ResourceLocation id, boolean available, int filterCount, List<String> reasons) {
    }

    public record ContentValidationIssue(ResourceLocation sceneId, String message, boolean rejectScene) {
        public ContentValidationIssue(ResourceLocation sceneId, String message) {
            this(sceneId, message, false);
        }
    }

    public record LoadedScenes(
            Map<SceneTrigger, List<Scene>> byTrigger,
            Map<ResourceLocation, Scene> byName,
            List<ContentValidationIssue> validationIssues) {
    }

    private interface ActionParser {
        SceneAction parse(JsonObject payload);
    }
}
