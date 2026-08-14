package block_party.conversation.generation;

import java.util.List;

public record ArcPlan(String premise, String characterArc, List<ArcBeat> beats, List<String> outcomes) {
    public ArcPlan {
        beats = beats == null ? List.of() : List.copyOf(beats);
        outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }
}
