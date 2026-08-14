package block_party.conversation.generation;

import java.util.List;

public record GenerationConstraints(
        int minimumCards,
        int maximumCards,
        int maximumDialogueCharacters,
        List<String> requiredFeatures,
        List<String> allowedActions,
        List<String> allowedConditions,
        List<String> responseCues) {
    public GenerationConstraints {
        minimumCards = minimumCards <= 0 ? 3 : minimumCards;
        maximumCards = maximumCards < minimumCards ? minimumCards : maximumCards;
        maximumDialogueCharacters = maximumDialogueCharacters <= 0 ? 240 : maximumDialogueCharacters;
        requiredFeatures = requiredFeatures == null ? List.of() : List.copyOf(requiredFeatures);
        allowedActions = allowedActions == null ? List.of() : List.copyOf(allowedActions);
        allowedConditions = allowedConditions == null ? List.of() : List.copyOf(allowedConditions);
        responseCues = responseCues == null ? List.of() : List.copyOf(responseCues);
    }
}
