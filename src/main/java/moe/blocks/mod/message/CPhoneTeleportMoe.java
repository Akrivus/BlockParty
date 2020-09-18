package moe.blocks.mod.message;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.data.dating.Relationship;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.item.YearbookPageItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CPhoneTeleportMoe {
    protected final Hand hand;
    protected final UUID moeUUID;

    public CPhoneTeleportMoe(Hand hand, UUID moeUUID) {
        this.hand = hand;
        this.moeUUID = moeUUID;
    }

    public CPhoneTeleportMoe(PacketBuffer buffer) {
        this(buffer.readEnumValue(Hand.class), buffer.readUniqueId());
    }

    public Hand getHand() {
        return this.hand;
    }

    public UUID getMoeUUID() {
        return this.moeUUID;
    }

    public static void encode(CPhoneRemoveMoe message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.getMoeUUID());
    }

    public static void handle(CPhoneRemoveMoe message, NetworkEvent.Context context, ServerPlayerEntity player) {
        if (player.getHeldItem(message.getHand()).getItem() != MoeItems.CELL_PHONE.get()) { return; }
        CharacterEntity character = CharacterEntity.getEntityFromUUID(CharacterEntity.class, player.world, message.getMoeUUID());
        if (character.getRelationshipWith(player).can(Relationship.Actions.TELEPORT)) {
            character.attemptTeleport(player.getPosXRandom(4.0F), player.getPosY(), player.getPosXRandom(4.0F), true);
        }
    }

    public static void handleContext(CPhoneRemoveMoe message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
