package moeblocks.convo;

import moeblocks.convo.enums.Interaction;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Predicate;

public class Conversation {
    private final Interaction trigger;
    private final Predicate<AbstractNPCEntity> function;
    private final Scene scene;

    public Conversation(Interaction trigger, Predicate<AbstractNPCEntity> function, Scene scene) {
        this.trigger = trigger;
        this.function = function;
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    public boolean matches(Interaction trigger, AbstractNPCEntity npc) {
        return this.trigger == trigger && this.function.test(npc);
    }
}
