package block_party.conversation.generation;

import java.util.List;

public record DialogueRevision(List<DialogueAlternative> alternatives) {
    public DialogueRevision {
        alternatives = alternatives == null ? List.of() : List.copyOf(alternatives);
    }
}
