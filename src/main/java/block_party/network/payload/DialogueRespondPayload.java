package block_party.network.payload;

import java.util.UUID;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.scene.Response;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DialogueRespondPayload(long databaseId, Response response) implements CustomPacketPayload {
    public static final Type<DialogueRespondPayload> TYPE = new Type<>(BlockParty.source("dialogue_respond"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueRespondPayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueRespondPayload::write, DialogueRespondPayload::read);

    public static void handle(DialogueRespondPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DialogueRespondPayload.respondToDialogue(context.player(), payload.databaseId(), payload.response()));
    }

    public static boolean respondToDialogue(ServerLevel level, BlockPartyDB db, UUID owner, long databaseId, Response response) {
        java.util.Optional<Moe> moe = db.findOwnedLoadedMoe(level, owner, databaseId);
        if (moe.isEmpty()) {
            return false;
        }
        moe.get().setResponse(response);
        return moe.get().getResponse() == response;
    }

    public static boolean respondToDialogue(Player player, long databaseId, Response response) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        return respondToDialogue(level, BlockPartyDB.get(level), player.getUUID(), databaseId, response);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
        buffer.writeEnum(this.response);
    }

    private static DialogueRespondPayload read(RegistryFriendlyByteBuf buffer) {
        return new DialogueRespondPayload(buffer.readVarLong(), buffer.readEnum(Response.class));
    }

    @Override
    public Type<DialogueRespondPayload> type() {
        return TYPE;
    }
}
