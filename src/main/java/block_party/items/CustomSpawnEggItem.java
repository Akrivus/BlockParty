package block_party.items;

import block_party.BlockParty;
import block_party.npc.BlockPartyNPC;
import block_party.scene.filters.Dere;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CustomSpawnEggItem extends Item implements ISortableItem {

    public CustomSpawnEggItem() {
        super(new Properties().tab(BlockParty.CreativeModeTab));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) { return InteractionResult.PASS; }
        Player player = context.getPlayer();
        BlockPos block = context.getClickedPos();
        BlockPos spawn = block.relative(context.getClickedFace());
        if (BlockPartyNPC.spawn(level, block, spawn, player.getYRot(), player.getXRot(), Dere.random(), player)) {
            context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        } else {
            String name = level.getBlockState(block).getBlock().getName().getString();
            player.displayClientMessage(new TranslatableComponent("item.block_party.npc_spawn_egg.error", name), true);
            return InteractionResult.FAIL;
        }
    }

    @Override
    public int getSortOrder() {
        return 1;
    }
}
