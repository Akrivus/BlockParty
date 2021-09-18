package block_party.message;

import block_party.init.BlockPartyItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class CRemovePage extends CNPCRemove {
    public CRemovePage(long id) {
        super(id);
    }

    public CRemovePage(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) {
        super.handle(context, player);
        if (this.removed) {
            ItemStack stack = new ItemStack(BlockPartyItems.YEARBOOK_PAGE.get());
            stack.setTag(this.npc.write());
            if (player.addItem(stack)) { return; }
            player.spawnAtLocation(stack);
        }
    }
}
