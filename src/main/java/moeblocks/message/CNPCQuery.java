package moeblocks.message;

import moeblocks.data.Moe;
import moeblocks.init.MoeWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.UUID;

public abstract class CNPCQuery extends AbstractMessage {
    protected final UUID id;
    protected List<UUID> list;
    protected Moe npc;

    public CNPCQuery(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }

    public CNPCQuery(UUID id) {
        this.id = id;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUniqueId(this.id);
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        this.list = MoeWorldData.get(player.world).byPlayer.get(player.getUniqueID());
        this.npc = MoeWorldData.Moes.find(this.id);
        if (this.npc != null) {
            this.onFound(context, player);
        }
    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {

    }

    public abstract void onFound(NetworkEvent.Context context, ServerPlayerEntity player);
}
