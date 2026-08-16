package block_party.conversation.workbench;

import block_party.conversation.model.ActionType;
import block_party.conversation.model.ChangeOperation;
import block_party.conversation.model.Comparison;
import block_party.conversation.model.ConditionType;
import block_party.conversation.model.NodeType;
import block_party.conversation.model.StateScope;
import block_party.conversation.model.StateType;
import block_party.conversation.model.TransitionType;
import block_party.conversation.model.TriggerTypes;
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
        conditions.put("RAW", List.of("raw"));

        Map<String, List<String>> actions = new LinkedHashMap<>();
        actions.put("SET_COOKIE", List.of("scope", "state", "value"));
        actions.put("DELETE_COOKIE", List.of("scope", "state"));
        actions.put("CHANGE_COUNTER", List.of("scope", "state", "operation", "amount"));
        actions.put("GIVE_ITEM", List.of("item", "count"));
        actions.put("TAKE_ITEM", List.of("item", "count"));
        actions.put("MARK_TIME", List.of("marker"));
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
        return Map.of(
                "conditions", conditions,
                "actions", actions,
                "enums", enums,
                "conditionTypes", names(ConditionType.values()),
                "actionTypes", names(ActionType.values()),
                "cues", List.of("chat_bubble", "green_checkmark", "red_x", "lovely_heart", "trusty_armor", "next_response"));
    }

    private static <E extends Enum<E>> List<String> names(E[] values) {
        return Arrays.stream(values).map(Enum::name).toList();
    }
}
