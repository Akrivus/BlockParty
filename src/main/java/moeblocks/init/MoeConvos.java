package moeblocks.init;

import moeblocks.convo.Conversation;
import moeblocks.convo.Scene;
import moeblocks.convo.Transition;
import moeblocks.convo.enums.Interaction;
import moeblocks.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
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

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
        e.getPlayer().world.getEntitiesWithinAABB(AbstractNPCEntity.class, e.getPlayer().getBoundingBox().grow(8, 8, 8)).forEach((npc) -> {
            if (StringUtils.containsIgnoreCase(e.getMessage(), npc.getGivenName())) { npc.onMention(e.getPlayer(), e.getMessage()); }
        });
    }
}
