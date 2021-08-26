package block_party.message;

import block_party.mob.Partyer;
import block_party.util.CellPhoneTeleporter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class CNPCTeleport extends CNPCQuery {
    public CNPCTeleport(UUID id) {
        super(id);
    }

    public CNPCTeleport(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        double x = player.getX() - Math.sin(0.0174532925F * player.yRot) * 1.44;
        double z = player.getZ() + Math.cos(0.0174532925F * player.yRot) * 1.44;
        ServerLevel world = (ServerLevel) player.level;
        world.getProfiler().push("teleportMoe");
        Partyer npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) { npc.teleport(world, new CellPhoneTeleporter(x, player.getY(), z)); }
        world.getProfiler().pop();
    }
}