package block_party.messages;

import block_party.client.screen.ControllerScreen;
import block_party.db.records.NPC;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SNPCResponse extends AbstractMessage.Server {
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
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen instanceof ControllerScreen) {
            ControllerScreen screen = (ControllerScreen) minecraft.screen;
            screen.setNPC(this.npc);
        }
    }
}
