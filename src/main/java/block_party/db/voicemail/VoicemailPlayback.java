package block_party.db.voicemail;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.network.payload.ControllerOpenPayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.Speaker;
import block_party.utils.Markdown;
import block_party.world.CellPhone;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;

public final class VoicemailPlayback {
    private static final long INTRO_DATABASE_ID = Long.MIN_VALUE + 7L;
    private static final Map<UUID, Session> SESSIONS = new java.util.HashMap<>();

    private VoicemailPlayback() {
    }

    public static boolean start(ServerPlayer player, InteractionHand hand) {
        Voicemails voicemails = Voicemails.get(player.level());
        List<Voicemails.Entry> messages = voicemails.revealed(player.getUUID());
        if (messages.isEmpty()) {
            return false;
        }
        Session session = new Session(hand, new ArrayList<>(messages), 0);
        SESSIONS.put(player.getUUID(), session);
        sendIntro(player, messages.size());
        return true;
    }

    public static boolean respond(ServerPlayer player, long databaseId, Response response) {
        Session session = SESSIONS.get(player.getUUID());
        if (session == null) {
            return false;
        }
        if (databaseId == INTRO_DATABASE_ID) {
            if (response == Response.NEXT_RESPONSE) {
                sendCurrent(player, session);
            } else if (response == Response.RED_X) {
                cancelToPhone(player, session);
            }
            return true;
        }

        if (response == Response.RED_X) {
            Voicemails.get(player.level()).delete(session.current());
            session.advance();
            sendNextOrPhone(player, session);
            return true;
        }
        if (response == Response.CHAT_BUBBLE) {
            long npcId = session.current().npcId();
            SESSIONS.remove(player.getUUID());
            CellPhone.queue(BlockPartyDB.get(player.level()), player, npcId);
            return true;
        }
        if (response == Response.NEXT_RESPONSE) {
            session.advance();
            sendNextOrPhone(player, session);
            return true;
        }
        return true;
    }

    public static boolean close(ServerPlayer player) {
        return SESSIONS.remove(player.getUUID()) != null;
    }

    private static void sendIntro(ServerPlayer player, int count) {
        Dialogue dialogue = new Dialogue(
                Component.translatable("gui.block_party.voicemail.intro", count).getString(),
                true,
                new Speaker(Speaker.Identity.NARRATOR, Speaker.Position.CENTER, "DEFAULT", "NORMAL", false, null, 1.0F),
                BlockParty.source("item.cell_phone.dial"),
                Map.of(
                        Response.NEXT_RESPONSE, Component.translatable("gui.block_party.voicemail.play").getString(),
                        Response.RED_X, Component.translatable("gui.block_party.voicemail.cancel").getString()));
        PacketDistributor.sendToPlayer(player, new DialogueOpenPayload(NpcDetailPayload.missing(INTRO_DATABASE_ID), dialogue));
    }

    private static void sendCurrent(ServerPlayer player, Session session) {
        Voicemails.Entry entry = session.current();
        BlockPartyDB db = BlockPartyDB.get(player.level());
        Optional<NPC> row = db.loadYearbookNpc(player.getUUID(), entry.npcId());
        Dialogue dialogue = row.map(npc -> messageDialogue(player, npc, entry))
                .orElseGet(VoicemailPlayback::missingMessageDialogue);
        PacketDistributor.sendToPlayer(player, new DialogueOpenPayload(NpcDetailPayload.from(db, player.getUUID(), entry.npcId(), row), dialogue));
    }

    private static Dialogue messageDialogue(ServerPlayer player, NPC npc, Voicemails.Entry entry) {
        if (!entry.text().isBlank()) {
            return new Dialogue(
                    resolveText(entry.text(), npc, player),
                    entry.tooltip(),
                    entry.speaker(),
                    entry.sound(),
                    messageResponses());
        }
        return new Dialogue(
                Component.translatable("gui.block_party.voicemail.message.default", npc.name()).getString(),
                true,
                new Speaker(Speaker.Identity.CHARACTER, Speaker.Position.LEFT, "DEFAULT", "NORMAL", false, null, 1.0F),
                null,
                messageResponses());
    }

    private static String resolveText(String text, NPC npc, ServerPlayer player) {
        return Markdown.mark(text
                .replace("@.name", player.getName().getString())
                .replace("@name", npc.name()));
    }

    private static Dialogue missingMessageDialogue() {
        return new Dialogue(
                Component.translatable("gui.block_party.voicemail.message.missing").getString(),
                true,
                new Speaker(Speaker.Identity.NARRATOR, Speaker.Position.CENTER, "DEFAULT", "NORMAL", false, null, 1.0F),
                null,
                messageResponses());
    }

    private static Map<Response, String> messageResponses() {
        return Map.of(
                Response.NEXT_RESPONSE, Component.translatable("gui.block_party.voicemail.keep").getString(),
                Response.RED_X, Component.translatable("gui.block_party.voicemail.delete").getString(),
                Response.CHAT_BUBBLE, Component.translatable("gui.block_party.voicemail.call_back").getString());
    }

    private static void sendNextOrPhone(ServerPlayer player, Session session) {
        if (session.done()) {
            cancelToPhone(player, session);
            return;
        }
        sendCurrent(player, session);
    }

    private static void cancelToPhone(ServerPlayer player, Session session) {
        SESSIONS.remove(player.getUUID());
        PacketDistributor.sendToPlayer(player,
                ControllerOpenPayload.cellPhone(BlockPartyDB.get(player.level()), player.getUUID(), session.hand()));
    }

    private record Session(InteractionHand hand, List<Voicemails.Entry> messages, int index) {
        private Voicemails.Entry current() {
            return this.messages.get(this.index);
        }

        private void advance() {
            this.messages.remove(this.index);
        }

        private boolean done() {
            return this.messages.isEmpty();
        }
    }
}
