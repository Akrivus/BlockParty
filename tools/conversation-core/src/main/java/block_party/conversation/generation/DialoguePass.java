package block_party.conversation.generation;

import java.util.List;

public record DialoguePass(List<DialoguePatch> nodes) {
    public DialoguePass {
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
    }
}
