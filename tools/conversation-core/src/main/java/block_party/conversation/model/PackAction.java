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
        String location,
        String intent,
        int ticks,
        double speed,
        double arrivalRadius,
        int timeoutTicks,
        String id,
        String block,
        int searchRadius,
        int verticalRadius,
        int minTicks,
        int maxTicks,
        String animation,
        String emotion,
        boolean canChangeDimension,
        boolean triggerScene,
        JsonElement raw) {
}
