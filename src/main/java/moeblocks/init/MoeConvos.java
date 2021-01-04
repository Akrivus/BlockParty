package moeblocks.init;

import moeblocks.automata.state.enums.Animation;
import moeblocks.automata.state.enums.BlockDataState;
import moeblocks.automata.state.enums.CupSize;
import moeblocks.automata.state.enums.Emotion;
import moeblocks.datingsim.convo.Conversation;
import moeblocks.datingsim.convo.Scene;
import moeblocks.datingsim.convo.Transition;
import moeblocks.datingsim.convo.enums.Interaction;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MoeConvos {
    private static final List<Conversation> REGISTRY = new ArrayList<>();

    public static void registerConvos() {
        register(new Conversation(Interaction.RIGHT_CLICK, (npc) -> npc.hasState(BlockDataState.DEFAULT), new Scene((player, npc) -> {
            npc.say(player, String.format("Oh, hi %s, would you like me to follow you?", player.getScoreboardName()), Response.YES, Response.NO, Response.CONVO);
        }, new Transition(Response.YES, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right behind you.", Response.CLOSE);
            npc.setFollowing(true);
        })), new Transition(Response.NO, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right here for you.", Response.CLOSE);
            npc.setFollowing(false);
        })))));
        register(new Conversation(Interaction.STARE, (npc) -> npc.asMoe((moe) -> moe.isBlock(Blocks.BARREL)), new Scene((player, npc) -> {
            npc.say(player, "I see you staring at me, and the barrel stays on.");
        })));
    }

    public static void register(Conversation conversation) {
        REGISTRY.add(conversation);
    }
    
    public static Scene find(Interaction interaction, AbstractNPCEntity npc) {
        List<Conversation> convos = REGISTRY.stream().filter((convo) -> convo.matches(interaction, npc)).collect(Collectors.toList());
        if (convos.isEmpty()) { return null; }
        return convos.get(npc.world.rand.nextInt(convos.size())).getScene();
    }
}
