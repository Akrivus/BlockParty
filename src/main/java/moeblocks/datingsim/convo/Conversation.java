package moeblocks.datingsim.convo;

import moeblocks.datingsim.convo.enums.Interaction;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Predicate;

public class Conversation {
    private final Interaction interaction;
    private final Predicate<AbstractNPCEntity> condition;
    private final Scene firstScene;
    
    public Conversation(Interaction interaction, Predicate<AbstractNPCEntity> condition, Scene firstScene) {
        this.interaction = interaction;
        this.condition = condition;
        this.firstScene = firstScene;
    }
    
    public Scene getFirstScene() {
        return this.firstScene;
    }
    
    public boolean matches(Interaction interaction, AbstractNPCEntity npc) {
        if (this.interaction != interaction) { return false; }
        return this.condition.test(npc);
    }
}
