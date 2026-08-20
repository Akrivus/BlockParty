package block_party.conversation.workbench;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ChangeOperation;
import block_party.conversation.model.Comparison;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.StateScope;
import block_party.conversation.model.StateType;
import block_party.conversation.model.SceneFilterCatalog;
import block_party.conversation.model.SceneNode;
import block_party.conversation.model.ResponseCues;
import block_party.conversation.model.TransitionType;
import block_party.conversation.model.TriggerTypes;
import block_party.conversation.model.SpeakerPresentation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class AuthoringSchema {
    private AuthoringSchema() {
    }

    static Map<String, Object> describe() {
        Map<String, List<String>> conditions = new LinkedHashMap<>();
        conditions.put("ALWAYS", List.of());
        conditions.put("HAS_COOKIE", List.of("scope", "state", "not"));
        conditions.put("COUNTER", List.of("scope", "state", "comparison", "value", "not"));
        conditions.put("HAS_ITEM", List.of("item", "count", "not"));
        conditions.put("HELD_ITEM", List.of("item", "count", "not"));
        conditions.put("MOE_HAS_ITEM", List.of("item", "count", "not"));
        conditions.put("BLOCK", List.of("item", "not"));
        conditions.put("ELAPSED_TIME", List.of("marker", "minGameDays", "not"));
        conditions.put("SCENE_FILTER", List.of("filter"));
        conditions.put("RAW", List.of("raw"));

        Map<String, List<String>> actions = new LinkedHashMap<>();
        actions.put("SET_COOKIE", List.of("scope", "state", "value"));
        actions.put("DELETE_COOKIE", List.of("scope", "state"));
        actions.put("CHANGE_COUNTER", List.of("scope", "state", "operation", "amount"));
        actions.put("GIVE_ITEM", List.of("item", "count"));
        actions.put("TAKE_ITEM", List.of("item", "count"));
        actions.put("ACCEPT_OFFERED_GIFT", List.of());
        actions.put("MARK_TIME", List.of("marker"));
        actions.put("REMEMBER_LOCATION", List.of("scope", "location", "source"));
        actions.put("FORGET_LOCATION", List.of("scope", "location"));
        actions.put("ASSIGN_LOCATION", List.of("id", "scope", "location", "speed", "arrivalRadius", "timeoutTicks"));
        actions.put("ASSIGN_TARGET", List.of("id", "target", "speed", "arrivalRadius", "timeoutTicks"));
        actions.put("CLEAR_ASSIGNMENT", List.of());
        actions.put("ASSIGN_NEAR_BLOCK", List.of("id", "block", "searchRadius", "verticalRadius", "speed", "arrivalRadius", "timeoutTicks"));
        actions.put("CONSUME_ASSIGNMENT_RESULT", List.of());
        actions.put("WAIT_TICKS", List.of("ticks"));
        actions.put("WAIT_RANDOM_TICKS", List.of("minTicks", "maxTicks"));
        actions.put("PLAY_ANIMATION", List.of("animation", "ticks"));
        actions.put("SET_EMOTION", List.of("emotion", "ticks"));
        actions.put("SIT", List.of());
        actions.put("STAND", List.of());
        actions.put("JUMP", List.of());
        actions.put("SWING_HAND", List.of());
        actions.put("LOOK_AT_ASSIGNMENT", List.of());
        actions.put("OPEN_INVENTORY", List.of("source", "destination"));
        actions.put("START_FOLLOW", List.of("target", "intent", "ticks", "canChangeDimension", "triggerScene"));
        actions.put("CLEAR_FOLLOW", List.of("target"));
        actions.put("END", List.of());
        actions.put("RAW", List.of("raw"));

        Map<String, Object> enums = new LinkedHashMap<>();
        enums.put("scope", names(StateScope.values()));
        enums.put("comparison", names(Comparison.values()));
        enums.put("operation", names(ChangeOperation.values()));
        enums.put("transition", names(TransitionType.values()));
        enums.put("nodeType", names(NodeType.values()));
        enums.put("stateType", names(StateType.values()));
        enums.put("trigger", TriggerTypes.values());
        enums.put("emotion", SpeakerPresentation.EMOTIONS);
        enums.put("animation", SpeakerPresentation.ANIMATIONS);
        enums.put("locationSource", List.of("moe", "player", "home", "current_anchor", "remembered_place"));
        enums.put("assignmentTarget", List.of("owner", "dialogue_player", "social_target", "nearest_moe"));
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("conditions", conditions);
        schema.put("sceneFilters", SceneFilterCatalog.types());
        schema.put("sceneFilterEnums", SceneFilterCatalog.enums());
        schema.put("sceneFilterFields", SceneFilterCatalog.fields());
        schema.put("sceneFilterFieldEnums", SceneFilterCatalog.fieldEnums());
        schema.put("actions", actions);
        schema.put("enums", enums);
        schema.put("conditionTypes", names(ConditionType.values()));
        schema.put("actionTypes", names(ActionType.values()));
        schema.put("maximumResponses", SceneNode.MAX_RESPONSES);
        schema.put("cues", ResponseCues.VALUES);
        return schema;
    }

    private static <E extends Enum<E>> List<String> names(E[] values) {
        return Arrays.stream(values).map(Enum::name).toList();
    }
}
