package moeblocks.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCRemove extends CNPCQuery {
    protected boolean removed;
    
    public CNPCRemove(UUID uuid) {
        super(uuid);
    }
    
    public CNPCRemove(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.npc == null) { return; }
        if (this.npc.isRemovable()) {
            this.sim.removeNPC(this.uuid);
            this.removed = true;
        }
    }
}
