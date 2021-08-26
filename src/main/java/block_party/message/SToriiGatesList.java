package block_party.message;

import block_party.BlockPartyDB;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class SToriiGatesList extends AbstractMessage {
    protected List<BlockPos> gates = new ArrayList<>();

    public SToriiGatesList(ResourceKey<Level> dim) {
        BlockPartyDB.ToriiGates.all().forEach((gate) -> {
            if (gate.isDim(dim)) { this.gates.add(gate.getPos()); }
        });
    }

    public SToriiGatesList(FriendlyByteBuf buffer) {
        int length = buffer.readInt();
        for (int i = 0; i < length; ++i) {
            this.gates.add(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.gates.size());
        this.gates.forEach((pos) -> {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        });
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) {

    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {

    }
}
