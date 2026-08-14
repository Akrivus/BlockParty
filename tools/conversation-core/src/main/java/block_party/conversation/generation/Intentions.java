package block_party.conversation.generation;

import java.util.List;

public record Intentions(List<SceneIntention> scenes) {
    public Intentions {
        scenes = scenes == null ? List.of() : List.copyOf(scenes);
    }
}
