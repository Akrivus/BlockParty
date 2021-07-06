package moeblocks.message;

import moeblocks.init.MoeWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class SToriiGatesList extends AbstractMessage {
    protected List<BlockPos> gates = new ArrayList<>();

    public SToriiGatesList(RegistryKey<World> dim) {
        MoeWorldData.ToriiGates.all().forEach((gate) -> {
            if (gate.isDim(dim)) { this.gates.add(gate.getPos()); }
        });
    }

    public SToriiGatesList(PacketBuffer buffer) {
        int length = buffer.readInt();
        for (int i = 0; i < length; ++i) {
            this.gates.add(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
        }
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(this.gates.size());
        this.gates.forEach((pos) -> {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        });
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {

    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {

    }
}
