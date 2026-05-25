package block_party.items;

import block_party.entities.Moe;
import block_party.entities.MoeSpawner;
import block_party.registry.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public final class CustomSpawnEggItem extends Item implements SortableItem {
    public CustomSpawnEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getSortOrder() {
        return 1;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos sourcePos = context.getClickedPos();
        BlockState sourceState = level.getBlockState(sourcePos);
        if (!sourceState.is(CustomTags.SPAWNS_MOES)) {
            return InteractionResult.FAIL;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Moe moe = spawnMoe(serverLevel, sourcePos, context.getClickedFace(), context.getPlayer());
        if (moe == null) {
            return InteractionResult.FAIL;
        }
        Player player = context.getPlayer();
        if (player == null || !player.isCreative()) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    public static Moe spawnMoe(ServerLevel level, BlockPos sourcePos, Direction face, Player player) {
        return spawnMoe(level, sourcePos, face, player == null ? null : player.getUUID());
    }

    public static Moe spawnMoe(ServerLevel level, BlockPos sourcePos, Direction face, UUID player) {
        BlockState sourceState = level.getBlockState(sourcePos);
        if (!sourceState.is(CustomTags.SPAWNS_MOES)) {
            return null;
        }

        CompoundTag tileEntityData = new CompoundTag();
        BlockEntity blockEntity = level.getBlockEntity(sourcePos);
        if (blockEntity != null) {
            tileEntityData = blockEntity.getPersistentData().copy();
        }

        BlockPos spawnPos = sourcePos.relative(face);
        Moe moe = MoeSpawner.spawn(level, spawnPos, sourceState, player, tileEntityData, created -> {
        });
        if (moe == null) {
            return null;
        }
        level.destroyBlock(sourcePos, false);
        return moe;
    }
}
