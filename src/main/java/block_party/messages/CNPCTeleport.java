package block_party.messages;

import block_party.entities.BlockPartyNPC;
import block_party.world.CellPhone;
import block_party.world.chunk.ForcedChunk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CNPCTeleport extends CNPCQuery {
    public CNPCTeleport(long id) {
        super(id);
    }

    public CNPCTeleport(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        CellPhone cellPhone = new CellPhone(this.npc, player);
        BlockPartyNPC npc = cellPhone.call();
        if (npc != null) { npc.teleport((ServerLevel)(player.level()), cellPhone); }
        ForcedChunk.get(this.id).despawn();
    }
}