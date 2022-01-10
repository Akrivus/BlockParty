package block_party.items;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.registry.CustomMessenger;
import block_party.messages.SOpenYearbook;
import block_party.npc.BlockPartyNPC;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class YearbookItem extends Item implements ISortableItem {
    public YearbookItem() {
        super(new Properties().tab(BlockParty.CreativeModeTab));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) { return InteractionResultHolder.pass(player.getItemInHand(hand)); }
        return new InteractionResultHolder(this.openGui(player, hand, -1), player.getItemInHand(hand));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (player.level.isClientSide()) { return InteractionResult.PASS; }
        if (entity instanceof BlockPartyNPC) {
            BlockPartyNPC npc = (BlockPartyNPC) entity;
            if (npc.getPlayer().equals(player)) { return this.openGui(player, hand, npc.getDatabaseID()); }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult openGui(Player player, InteractionHand hand, long id) {
        List<Long> npcs = BlockPartyDB.get(player.level).getFrom(player);
        if (npcs.size() > 0) { CustomMessenger.send(player, new SOpenYearbook(npcs, id < 0 ? npcs.get(0) : id, hand)); }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getSortOrder() {
        return 2;
    }
}
