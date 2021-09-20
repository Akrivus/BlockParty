package block_party.convo;

import block_party.convo.enums.Interaction;
import block_party.npc.BlockPartyNPC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Conversation {
    private static final List<Conversation> REGISTRY = new ArrayList<>();
    private final Interaction trigger;
    private final Predicate<BlockPartyNPC> function;
    private final Scene scene;

    public Conversation(Interaction trigger, Predicate<BlockPartyNPC> function, Scene scene) {
        this.trigger = trigger;
        this.function = function;
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    public boolean matches(Interaction trigger, BlockPartyNPC npc) {
        return this.trigger == trigger && this.function.test(npc);
    }

    public static void register(Conversation conversation) {
        REGISTRY.add(conversation);
    }
}
