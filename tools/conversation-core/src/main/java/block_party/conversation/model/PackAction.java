package block_party.conversation.model;

import com.google.gson.JsonElement;

public record PackAction(
        ActionType type,
        StateScope scope,
        String state,
        String value,
        int amount,
        ChangeOperation operation,
        String item,
        int count,
        String target,
        String source,
        String destination,
        String marker,
        String intent,
        int ticks,
        boolean canChangeDimension,
        boolean triggerScene,
        JsonElement raw) {
}
