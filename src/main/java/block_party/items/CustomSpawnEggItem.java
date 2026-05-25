package block_party.items;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.registry.CustomEntities;
import block_party.registry.CustomTags;
import block_party.world.CellPhone;
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

import java.sql.SQLException;
import java.util.function.Consumer;
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

    public static Moe spawnMoe(ServerLevel level, BlockPos sourcePos, Direction face, UUID owner) {
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
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        moe.setBlockState(sourceState);
        moe.setTileEntityData(tileEntityData);
        if (owner != null) {
            moe.setOwnerUUID(owner);
        }
        MoeResolution resolution = findOrCreateMoe(level, moe, owner);
        if (resolution == null) {
            return null;
        }
        moe = resolution.moe();
        if (isLoaded(level, moe)) {
            moe.moveToBlock(spawnPos);
            level.destroyBlock(sourcePos, false);
            return moe;
        }
        if (!level.addFreshEntity(moe)) {
            if (resolution.created()) {
                try {
                    BlockPartyDB.get(level).deleteNpc(moe.getDatabaseID());
                } catch (SQLException ignored) {
                    // Best-effort cleanup; the spawn still fails safely from the player's perspective.
                }
            }
            return null;
        }
        level.destroyBlock(sourcePos, false);
        return moe;
    }

    public static Moe createMoe(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID owner) {
        return createMoe(level, spawnPos, sourceState, owner, moe -> {
        });
    }

    public static Moe createMoe(ServerLevel level, BlockPos spawnPos, BlockState sourceState, UUID owner, Consumer<Moe> configure) {
        Moe moe = new Moe(CustomEntities.MOE.get(), level);
        moe.moveToBlock(spawnPos);
        moe.setBlockState(sourceState);
        if (owner != null) {
            moe.setOwnerUUID(owner);
        }
        configure.accept(moe);
        MoeResolution resolution = findOrCreateMoe(level, moe, owner);
        return resolution == null ? null : resolution.moe();
    }

    private static MoeResolution findOrCreateMoe(ServerLevel level, Moe moe, UUID owner) {
        BlockPartyDB db = BlockPartyDB.get(level);
        NPC row;
        try {
            row = findExistingUniquePersonality(db, moe);
            if (row != null) {
                if (owner != null) {
                    db.addTo(owner, row.databaseId());
                }
                Moe loaded = CellPhone.findLoadedMoe(level, row.databaseId()).orElse(null);
                if (loaded != null) {
                    return new MoeResolution(loaded, false);
                }
                row.applyTo(moe);
                return new MoeResolution(moe, false);
            }
            row = db.createNpc(level, moe);
            row.applyTo(moe);
            if (owner != null) {
                db.addTo(owner, row.databaseId());
            }
        } catch (SQLException exception) {
            return null;
        }
        return new MoeResolution(moe, true);
    }

    private static NPC findExistingUniquePersonality(BlockPartyDB db, Moe moe) throws SQLException {
        if (!moe.getVisibleBlockState().is(CustomTags.UNIQUE_PERSONALITIES)) {
            return null;
        }
        return db.findUniquePersonality(moe.getVisibleBlockState()).orElse(null);
    }

    public static boolean isLoaded(ServerLevel level, Moe moe) {
        return CellPhone.findLoadedMoe(level, moe.getDatabaseID()).filter(live -> live == moe).isPresent();
    }

    private record MoeResolution(Moe moe, boolean created) {
    }
}
