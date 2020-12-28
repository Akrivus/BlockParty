package moeblocks.init;

import moeblocks.automata.state.enums.Animation;
import moeblocks.datingsim.convo.Conversation;
import moeblocks.datingsim.convo.Scene;
import moeblocks.datingsim.convo.Transition;
import moeblocks.datingsim.convo.enums.Interaction;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;

import java.util.*;

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
    
    static {
        register(new Conversation(Interaction.RIGHT_CLICK, (npc) -> true, new Scene((player, npc) -> {
            npc.say(player, "Are we going somewhere?", Response.YES, Response.NO, Response.CONVO);
        }, new Transition(Response.YES, new Scene((player, npc) -> {
            npc.say(player, "What are you waiting for? Let's go!", Response.CLOSE);
            npc.setFollowing(true);
        })), new Transition(Response.NO, new Scene((player, npc) -> {
            npc.say(player, "Okay, maybe later then.", Response.CLOSE);
            npc.setFollowing(false);
        })), new Transition(Response.CONVO, new Scene((player, npc) -> {
            npc.setConvo(Interaction.CONVO, player);
        })))));
    }
}
