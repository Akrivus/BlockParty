package moeblocks.init;

import moeblocks.convo.Conversation;
import moeblocks.convo.Scene;
import moeblocks.convo.Transition;
import moeblocks.convo.enums.Interaction;
import moeblocks.convo.enums.Response;

import java.util.ArrayList;
import java.util.List;

public class MoeConvos {
    private static final List<Conversation> REGISTRY = new ArrayList<>();

    public static void registerConvos() {
        register(new Conversation(Interaction.RIGHT_CLICK, (npc) -> true, new Scene((player, npc) -> {
            npc.say(player, String.format("Oh, hi %s, would you like me to follow you?", player.getScoreboardName()), Response.YES, Response.NO, Response.CHEST, Response.CONVO);
        }, new Transition(Response.YES, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right behind you.");
            npc.setFollowing(true);
        })), new Transition(Response.NO, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right here for you.");
            npc.setFollowing(false);
        })))));
    }

    public static void register(Conversation conversation) {
        REGISTRY.add(conversation);
    }
}
