package block_party.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class SNPCList extends AbstractMessage {
    protected List<Long> npcs = new ArrayList<>();

    public SNPCList(List<Long> npcs) {
        this.npcs = npcs;
    }

    public SNPCList(FriendlyByteBuf buffer) {
        int length = buffer.readInt();
        for (int i = 0; i < length; ++i) {
            this.npcs.add(buffer.readLong());
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.npcs.size());
        this.npcs.forEach((key) -> buffer.writeLong(key));
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) { }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) { }
}
