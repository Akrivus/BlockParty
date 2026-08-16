package block_party.conversation.generation;

import java.util.List;

public record GenerationConstraints(
        int minimumCards,
        int maximumCards,
        int maximumDialogueCharacters,
        String dialogueStyle,
        List<String> requiredFeatures,
        List<String> allowedActions,
        List<String> allowedConditions,
        List<String> responseCues) {
    public GenerationConstraints {
        minimumCards = minimumCards <= 0 ? 3 : minimumCards;
        maximumCards = maximumCards < minimumCards ? minimumCards : maximumCards;
        maximumDialogueCharacters = maximumDialogueCharacters <= 0 ? 160 : maximumDialogueCharacters;
        dialogueStyle = dialogueStyle == null || dialogueStyle.isBlank()
                ? "Concise, playful internet-anime banter. Be expressive and lightly meme-y; use occasional rawr/xd energy "
                        + "only when it suits the character, never as constant noise."
                : dialogueStyle.strip();
        requiredFeatures = requiredFeatures == null ? List.of() : List.copyOf(requiredFeatures);
        allowedActions = allowedActions == null ? List.of() : List.copyOf(allowedActions);
        allowedConditions = allowedConditions == null ? List.of() : List.copyOf(allowedConditions);
        responseCues = responseCues == null ? List.of() : List.copyOf(responseCues);
    }
}
