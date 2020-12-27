package moeblocks.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SNPCList extends AbstractMessage {
    protected List<UUID> npcs = new ArrayList<>();
    
    public SNPCList(List<UUID> npcs) {
        this.npcs = npcs;
    }
    
    public SNPCList(PacketBuffer buffer) {
        int length = buffer.readInt();
        for (int i = 0; i < length; ++i) {
            this.npcs.add(buffer.readUniqueId());
        }
    }
    
    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.npcs.size());
        this.npcs.forEach((uuid) -> buffer.writeUniqueId(uuid));
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) { }
    
    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) { }
}
