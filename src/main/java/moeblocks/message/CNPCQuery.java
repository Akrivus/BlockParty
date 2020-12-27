package moeblocks.message;

import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.DatingData;
import moeblocks.datingsim.DatingSim;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCQuery extends AbstractMessage {
    protected final UUID uuid;
    protected DatingSim sim;
    protected CacheNPC npc;
    
    public CNPCQuery(UUID uuid) {
        this.uuid = uuid;
    }
    
    public CNPCQuery(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }
    
    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        this.sim = DatingData.get(player.world, player.getUniqueID());
        this.npc = this.sim.getNPC(this.uuid);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) { }
}
