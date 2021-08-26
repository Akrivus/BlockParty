package block_party.message;

import block_party.client.screen.ControllerScreen;
import block_party.db.records.NPC;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SNPCResponse extends AbstractMessage {
    protected final NPC npc;

    public SNPCResponse(FriendlyByteBuf buffer) {
        this(new NPC(buffer.readNbt()));
    }

    public SNPCResponse(NPC npc) {
        this.npc = npc;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.npc.write(new CompoundTag()));
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) { }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen instanceof ControllerScreen) {
            ControllerScreen screen = (ControllerScreen) minecraft.screen;
            screen.setNPC(this.npc);
        }
    }
}
