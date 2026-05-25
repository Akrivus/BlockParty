package block_party.network.payload;

import java.util.UUID;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.db.voicemail.VoicemailPlayback;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DialogueClosePayload(long databaseId) implements CustomPacketPayload {
    public static final Type<DialogueClosePayload> TYPE = new Type<>(BlockParty.source("dialogue_close"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueClosePayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueClosePayload::write, DialogueClosePayload::read);

    public static void handle(DialogueClosePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DialogueClosePayload.closeDialogue(context.player(), payload.databaseId()));
    }

    public static boolean closeDialogue(ServerLevel level, BlockPartyDB db, UUID owner, long databaseId) {
        java.util.Optional<Moe> moe = db.findOwnedLoadedMoe(level, owner, databaseId);
        if (moe.isEmpty()) {
            return false;
        }
        moe.get().clearDialogue();
        return true;
    }

    public static boolean closeDialogue(Player player, long databaseId) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer && VoicemailPlayback.close(serverPlayer)) {
            return true;
        }
        return closeDialogue(level, BlockPartyDB.get(level), player.getUUID(), databaseId);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
    }

    private static DialogueClosePayload read(RegistryFriendlyByteBuf buffer) {
        return new DialogueClosePayload(buffer.readVarLong());
    }

    @Override
    public Type<DialogueClosePayload> type() {
        return TYPE;
    }
}
