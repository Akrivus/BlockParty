package block_party.conversation.generation;

import java.util.List;

public record DialogueAlternative(String text, List<String> responseLabels, String rationale) {
    public DialogueAlternative {
        responseLabels = responseLabels == null ? List.of() : List.copyOf(responseLabels);
    }
}
