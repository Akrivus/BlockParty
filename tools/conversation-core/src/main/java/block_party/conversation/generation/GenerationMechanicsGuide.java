package block_party.conversation.generation;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.SceneFilterCatalog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Machine-readable mechanics vocabulary supplied to planning and graph generation. */
final class GenerationMechanicsGuide {
    private static final Gson GSON = new Gson();
    private GenerationMechanicsGuide() {}

    static JsonObject describe(GenerationBrief brief) {
        JsonObject guide = new JsonObject();
        guide.add("sceneFilterTypes", GSON.toJsonTree(SceneFilterCatalog.types()));
        guide.add("sceneFilterFields", GSON.toJsonTree(SceneFilterCatalog.fields()));
        guide.add("sceneFilterEnums", GSON.toJsonTree(SceneFilterCatalog.enums()));
        guide.add("actions", GSON.toJsonTree(Arrays.stream(ActionType.values())
                .filter(value -> value != ActionType.RAW).map(Enum::name).toList()));
        guide.add("actionFields", GSON.toJsonTree(Map.ofEntries(
                Map.entry("ASSIGN_LOCATION", List.of("id", "scope", "location", "speed", "arrivalRadius", "timeoutTicks")),
                Map.entry("ASSIGN_NEAR_BLOCK", List.of("id", "block", "searchRadius", "verticalRadius", "speed", "arrivalRadius", "timeoutTicks")),
                Map.entry("CONSUME_ASSIGNMENT_RESULT", List.of()),
                Map.entry("LOOK_AT_ASSIGNMENT", List.of()),
                Map.entry("PLAY_ANIMATION", List.of("animation", "ticks")),
                Map.entry("SET_EMOTION", List.of("emotion", "ticks")),
                Map.entry("WAIT_TICKS", List.of("ticks")),
                Map.entry("WAIT_RANDOM_TICKS", List.of("minTicks", "maxTicks")),
                Map.entry("SIT", List.of()), Map.entry("STAND", List.of()), Map.entry("JUMP", List.of()),
                Map.entry("SWING_HAND", List.of()))));
        JsonArray translations = new JsonArray();
        translations.add("sunset/evening -> SCENE_FILTER time_period=evening");
        translations.add("clear weather -> SCENE_FILTER weather=clear");
        translations.add("idle Moe -> SCENE_FILTER routine_intent=idle");
        translations.add("autonomous/background behavior -> trigger routine_tick with selection group, weight, and cooldown");
        translations.add("walk to a named place -> ASSIGN_LOCATION, then continue from assignment_arrived filtered by assignment_result_id");
        translations.add("walk beside a block -> ASSIGN_NEAR_BLOCK, then continue from assignment_arrived");
        translations.add("look/animate/wait after walking -> place staging actions on the arrival node, never the departure node");
        guide.add("translations", translations);
        guide.addProperty("assignmentPattern",
                "departure assignment -> assignment_arrived/assignment_failed nodes keyed by assignment ID -> staging -> next assignment");
        guide.addProperty("assignmentAcceptancePattern",
                "When a player response accepts an immediate trip, put the assignment action on that response and use "
                        + "EXTERNAL_EVENT toward a node triggered by assignment_arrived. Do not use LATER_INTERACTION unless "
                        + "the player must right-click again. Do not START_FOLLOW while an assignment route is active because follow suppresses directive movement.");
        guide.addProperty("sceneFilterShape",
                "A PackCondition has type SCENE_FILTER and a nested filter object, e.g. "
                        + "{type:SCENE_FILTER, filter:{type:block_party:weather,value:clear}}. "
                        + "Never put the nested filter type or value in PackCondition.state or PackCondition.value.");
        guide.addProperty("lockedSelectorRule",
                brief.lockedFilters().isEmpty()
                        ? "No selectors are locked. Infer and emit every environmental or state constraint requested by the creative prompt."
                        : "Locked selectors are mandatory and immutable. Preserve them and add non-conflicting selectors explicitly requested by the creative prompt.");
        return guide;
    }
}
