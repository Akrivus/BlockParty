package block_party.items;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;
import java.sql.SQLException;

public final class CustomSpawnEggItem extends Item {
    public CustomSpawnEggItem(Properties properties) {
        super(properties);
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
        return moe == null ? InteractionResult.FAIL : InteractionResult.SUCCESS;
    }

    public static Moe spawnMoe(ServerLevel level, BlockPos sourcePos, Direction face, Player player) {
        return spawnMoe(level, sourcePos, face, player == null ? null : player.getUUID());
    }

    public static Moe spawnMoe(ServerLevel level, BlockPos sourcePos, Direction face, UUID owner) {
        BlockState sourceState = level.getBlockState(sourcePos);
        if (!sourceState.is(CustomTags.SPAWNS_MOES)) {
            return null;
        }

        BlockPos spawnPos = sourcePos.relative(face);
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        moe.setBlockState(sourceState);
        if (owner != null) {
            moe.setOwnerUUID(owner);
        }
        try {
            BlockPartyDB db = BlockPartyDB.get(level);
            NPC row = db.createNpc(level, moe);
            row.applyTo(moe);
            if (owner != null) {
                db.addTo(owner, row.databaseId());
            }
        } catch (SQLException exception) {
            return null;
        }
        return level.addFreshEntity(moe) ? moe : null;
    }
}
