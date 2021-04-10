package moeblocks.message;

import moeblocks.client.screen.ControllerScreen;
import moeblocks.data.Moe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SNPCResponse extends AbstractMessage {
    protected final Moe npc;

    public SNPCResponse(PacketBuffer buffer) {
        this(new Moe(buffer.readCompoundTag()));
    }

    public SNPCResponse(Moe npc) {
        this.npc = npc;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeCompoundTag(this.npc.write(new CompoundNBT()));
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) { }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.currentScreen instanceof ControllerScreen) {
            ControllerScreen screen = (ControllerScreen) minecraft.currentScreen;
            screen.setNPC(this.npc);
        }
    }
}
