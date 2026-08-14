package block_party.conversation.generation;

import java.util.List;
import java.util.Map;

public record SceneIntention(
        String node,
        String speakerObjective,
        String emotionalState,
        List<String> mayReveal,
        List<String> mustNotReveal,
        Map<String, String> playerChoicePurpose,
        List<String> continuity) {
}
