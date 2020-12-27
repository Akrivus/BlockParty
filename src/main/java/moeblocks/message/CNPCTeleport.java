package moeblocks.message;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeSounds;
import moeblocks.util.CellPhoneTeleporter;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

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
        npc.teleport(world, new CellPhoneTeleporter(x, player.getPosY(), z));
    }
}