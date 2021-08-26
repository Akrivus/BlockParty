package block_party.convo;

import block_party.convo.enums.Interaction;
import block_party.mob.Partyer;

import java.util.function.Predicate;

public class Conversation {
    private final Interaction trigger;
    private final Predicate<Partyer> function;
    private final Scene scene;

    public Conversation(Interaction trigger, Predicate<Partyer> function, Scene scene) {
        this.trigger = trigger;
        this.function = function;
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    public boolean matches(Interaction trigger, Partyer npc) {
        return this.trigger == trigger && this.function.test(npc);
    }
}
