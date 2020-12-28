package moeblocks.init;

import moeblocks.datingsim.convo.Conversation;
import moeblocks.datingsim.convo.enums.Interaction;
import moeblocks.entity.AbstractNPCEntity;

import java.util.ArrayList;
import java.util.List;

public class MoeConvos {
    private static final List<Conversation> REGISTRY = new ArrayList<>();
    
    public static void register(Conversation conversation) {
        REGISTRY.add(conversation);
    }
    
    public static Conversation find(Interaction interaction, AbstractNPCEntity npc) {
        for (Conversation conversation : REGISTRY) {
            if (conversation.matches(interaction, npc)) { return conversation; }
        }
        return null;
    }
}
