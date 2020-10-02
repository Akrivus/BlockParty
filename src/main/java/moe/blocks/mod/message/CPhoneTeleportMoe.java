package moe.blocks.mod.message;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.init.MoeSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CPhoneTeleportMoe {
    protected final UUID moeUUID;

    public CPhoneTeleportMoe(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }

    public CPhoneTeleportMoe(UUID moeUUID) {
        this.moeUUID = moeUUID;
        Minecraft.getInstance().player.playSound(MoeSounds.CELL_PHONE_RING.get(), 1.0F, 1.0F);
    }

    public static void encode(CPhoneTeleportMoe message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.getMoeUUID());
    }

    public UUID getMoeUUID() {
        return this.moeUUID;
    }

    public static void handleContext(CPhoneTeleportMoe message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), context.get().getSender()));
        context.get().setPacketHandled(true);
    }

    public static void handle(CPhoneTeleportMoe message, NetworkEvent.Context context, ServerPlayerEntity player) {
        AbstractNPCEntity character = AbstractNPCEntity.getEntityFromUUID(AbstractNPCEntity.class, player.getServerWorld(), message.getMoeUUID());
        if (character != null) {
            double x = player.getPosX() - Math.sin(0.0174532925F * player.rotationYaw) * 1.44;
            double z = player.getPosZ() + Math.cos(0.0174532925F * player.rotationYaw) * 1.44;
            float yaw = (float) Math.atan2(player.getPosX() - x, player.getPosZ() - z);
            character.setPositionAndRotation(x, player.getPosY() + 1.0F, z, yaw, -player.rotationPitch);
        } else {
            // pull up some sort of dialogue GUI
        }
    }
}
