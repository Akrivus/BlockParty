package block_party.items;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.scene.traits.BloodType;
import block_party.scene.traits.Dere;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CustomSpawnEggItem extends Item implements ISortableItem {
    public CustomSpawnEggItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) { return InteractionResult.PASS; }
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (state.is(CustomTags.Blocks.SPAWNS_MOES)) {
            BlockPos spawn = pos.relative(context.getClickedFace());
            Moe moe = CustomEntities.MOE.get().create(level);
            moe.absMoveTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0, 0);
            moe.setDatabaseID(pos.asLong());
            moe.setBlockState(state);
            if (moe.getActualBlockState().hasBlockEntity())
                moe.setTileEntityData(level.getBlockEntity(pos).getPersistentData());
            moe.setDere(Dere.random());
            moe.setBloodType(BloodType.O.weigh(moe.getRandom()));
            moe.setPlayer(player);
            moe.claim(player);
            level.addFreshEntity(moe);
            level.destroyBlock(pos, false);
            context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        } else {
            String name = level.getBlockState(pos).getBlock().getName().getString();
            player.displayClientMessage(Component.translatable("item.block_party.moe_spawn_egg.error", name), true);
            return InteractionResult.FAIL;
        }
    }

    @Override
    public int getSortOrder() {
        return 1;
    }
}
