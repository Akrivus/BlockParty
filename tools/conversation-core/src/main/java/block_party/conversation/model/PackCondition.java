package block_party.conversation.model;

import com.google.gson.JsonElement;

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
        JsonElement raw) {
}
