package moeblocks.message;

import moeblocks.init.MoeItems;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CRemovePage extends CNPCRemove {
    public CRemovePage(UUID id) {
        super(id);
    }

    public CRemovePage(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.removed) {
            ItemStack stack = new ItemStack(MoeItems.YEARBOOK_PAGE.get());
            stack.setTag(this.npc.write());
            if (player.addItemStackToInventory(stack)) { return; }
            player.entityDropItem(stack);
        }
    }
}
