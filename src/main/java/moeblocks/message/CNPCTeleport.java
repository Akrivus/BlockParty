package moeblocks.message;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.util.CellPhoneTeleporter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCTeleport extends CNPCQuery {
    public CNPCTeleport(UUID uuid) {
        super(uuid);
    }
    
    public CNPCTeleport(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.npc == null) { return; }
        double x = player.getPosX() - Math.sin(0.0174532925F * player.rotationYaw) * 1.44;
        double z = player.getPosZ() + Math.cos(0.0174532925F * player.rotationYaw) * 1.44;
        ServerWorld world = (ServerWorld) player.world;
        AbstractNPCEntity npc = this.npc.get(player.getServer());
        if (npc != null) {
            npc.teleport(world, new CellPhoneTeleporter(x, player.getPosY(), z));
        } else {
            this.sim.removeNPC(this.npc.getUUID());
        }
    }
}