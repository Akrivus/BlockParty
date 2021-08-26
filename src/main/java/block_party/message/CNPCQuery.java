package block_party.message;

import block_party.BlockPartyDB;
import block_party.db.records.NPC;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.List;
import java.util.UUID;

public abstract class CNPCQuery extends AbstractMessage {
    protected final UUID id;
    protected List<UUID> list;
    protected NPC npc;

    public CNPCQuery(FriendlyByteBuf buffer) {
        this(buffer.readUUID());
    }

    public CNPCQuery(UUID id) {
        this.id = id;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) {
        this.list = BlockPartyDB.get(player.level).getFrom(player);
        this.npc = BlockPartyDB.NPCs.find(this.id);
        if (this.npc != null) {
            this.onFound(context, player);
        }
    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {

    }

    public abstract void onFound(NetworkEvent.Context context, ServerPlayer player);
}
