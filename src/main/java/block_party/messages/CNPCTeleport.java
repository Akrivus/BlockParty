package block_party.messages;

import block_party.npc.BlockPartyNPC;
import block_party.world.chunk.CellPhoneTeleporter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class CNPCTeleport extends CNPCQuery {
    public CNPCTeleport(long id) {
        super(id);
    }

    public CNPCTeleport(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        double x = player.getX() - Math.sin(0.0174532925F * player.getYRot()) * 1.44;
        double z = player.getZ() + Math.cos(0.0174532925F * player.getYRot()) * 1.44;
        ServerLevel level = (ServerLevel) player.level;
        level.getProfiler().push("teleportNPC");
        BlockPartyNPC npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) { npc.teleport(level, new CellPhoneTeleporter(x, player.getY(), z)); }
        level.getProfiler().pop();
    }
}