package block_party.convo;

import block_party.convo.Conversation;
import block_party.convo.Scene;
import block_party.convo.Transition;
import block_party.convo.enums.Interaction;
import block_party.convo.enums.Response;
import block_party.npc.BlockPartyNPC;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber
public class ConvoEngine {
    public static void registerConvos() {
        Conversation.register(new Conversation(Interaction.RIGHT_CLICK, (npc) -> true, new Scene((player, npc) -> {
            npc.say(player, String.format("Oh, hi %s, would you like me to follow you?", player.getScoreboardName()), Response.YES, Response.NO, Response.CHEST, Response.CONVO);
        }, new Transition(Response.YES, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right behind you.");
            npc.setFollowing(true);
        })), new Transition(Response.NO, new Scene((player, npc) -> {
            npc.say(player, "Okay, I'll be right here for you.");
            npc.setFollowing(false);
        })))));
    }

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent e) {
        e.getPlayer().level.getEntitiesOfClass(BlockPartyNPC.class, e.getPlayer().getBoundingBox().inflate(8, 8, 8)).forEach((npc) -> {
            if (StringUtils.containsIgnoreCase(e.getMessage(), npc.getGivenName())) { npc.onMention(e.getPlayer(), e.getMessage()); }
        });
    }
}
