package block_party.conversation.model;

import java.util.List;

public record ResponseEdge(String cue, String label, String target, TransitionType transition, List<PackAction> actions) {
    public ResponseEdge {
        transition = transition == null ? TransitionType.IMMEDIATE : transition;
        actions = actions == null ? List.of() : List.copyOf(actions);
    }
}
