package block_party.conversation.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record PackCondition(
        ConditionType type,
        StateScope scope,
        String state,
        String item,
        int count,
        Comparison comparison,
        int value,
        boolean not,
        String marker,
        int minGameDays,
        JsonObject filter,
        JsonElement raw) {
}
