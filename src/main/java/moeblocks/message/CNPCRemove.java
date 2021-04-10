package moeblocks.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCRemove extends CNPCQuery {
    protected boolean removed;

    public CNPCRemove(UUID id) {
        super(id);
    }

    public CNPCRemove(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayerEntity player) {
        this.removed = this.npc.isDeadOrEstrangedFrom(player) && this.list.remove(this.npc.getID());
    }
}
