package block_party.items;

import block_party.db.BlockPartyDB;
import block_party.network.CustomMessenger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class CellPhoneItem extends Item implements SortableItem {
    public CellPhoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getSortOrder() {
        return 10;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    CustomMessenger.cellPhoneOpenPayload(BlockPartyDB.get(level), player.getUUID(), hand));
        }
        return InteractionResult.SUCCESS;
    }
}
