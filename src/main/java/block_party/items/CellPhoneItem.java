package block_party.items;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.messages.SOpenCellPhone;
import block_party.registry.CustomMessenger;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CellPhoneItem extends Item implements ISortableItem {
    public CellPhoneItem() {
        super(new Properties().tab(BlockParty.CreativeModeTab));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) { return InteractionResultHolder.pass(player.getItemInHand(hand)); }
        List<Long> npcs = BlockPartyDB.get(player.level).getFrom(player);
        CustomMessenger.send(player, new SOpenCellPhone(npcs, hand));
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
