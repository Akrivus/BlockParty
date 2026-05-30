package block_party.blocks.entity;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import java.sql.SQLException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractDataBlockEntity extends BlockEntity {
    public static final UUID BLANK_UUID = new UUID(0L, 0L);
    public static final String NBT_DATABASE_ID = BlockPartyDB.COLUMN_DATABASE_ID;
    public static final String NBT_PLAYER_UUID = BlockPartyDB.COLUMN_PLAYER_UUID;
    public static final String NBT_HAS_ROW = "HasRow";
    public static final String NBT_CLAIMED = "Claimed";

    private long databaseId;
    private UUID playerUuid = BLANK_UUID;
    private boolean claimed;

    protected AbstractDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.databaseId = pos.asLong();
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains(NBT_DATABASE_ID)) {
            this.databaseId = compound.getLong(NBT_DATABASE_ID);
        }
        if (compound.hasUUID(NBT_PLAYER_UUID)) {
            this.playerUuid = compound.getUUID(NBT_PLAYER_UUID);
        }
        this.claimed = compound.getBoolean(NBT_HAS_ROW) || compound.getBoolean(NBT_CLAIMED);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        compound.putLong(NBT_DATABASE_ID, this.databaseId);
        compound.putBoolean(NBT_HAS_ROW, this.claimed);
        compound.putBoolean(NBT_CLAIMED, this.claimed);
        compound.putUUID(NBT_PLAYER_UUID, this.playerUuid);
        super.saveAdditional(compound, provider);
    }

    public long getDatabaseID() {
        return this.databaseId;
    }

    public void setDatabaseID(long databaseId) {
        this.databaseId = databaseId;
        this.setChanged();
    }

    public UUID getPlayerUUID() {
        return this.playerUuid;
    }

    public void setPlayerUUID(UUID playerUuid) {
        this.playerUuid = playerUuid == null ? BLANK_UUID : playerUuid;
        this.setChanged();
    }

    public boolean hasRow() {
        return this.claimed;
    }

    public void markClaimed(UUID playerUuid) {
        this.playerUuid = playerUuid == null ? BLANK_UUID : playerUuid;
        this.claimed = true;
        this.setChanged();
    }

    public boolean claim(Player player) {
        if (this.level == null || this.level.isClientSide()) {
            return false;
        }
        UUID playerUuid = player == null ? BLANK_UUID : player.getUUID();
        this.playerUuid = playerUuid;
        this.claimed = true;
        try {
            BlockPartyDB.get(this.level).upsertDataBlock(this);
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to claim Block Party data block " + this.databaseId, exception);
        }
        this.afterUpdate();
        this.afterChange();
        this.setChanged();
        return true;
    }

    public void onDestroyed() {
        if (!this.claimed || this.level == null || this.level.isClientSide()) {
            return;
        }
        try {
            BlockPartyDB.get(this.level).deleteDataBlock(this.getTableName(), this.databaseId);
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to delete Block Party data block " + this.databaseId, exception);
        }
        this.claimed = false;
        this.afterDelete();
        this.afterChange();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.claimed && this.level != null && !this.level.isClientSide()) {
            try {
                BlockPartyDB.get(this.level).upsertDataBlock(this);
            } catch (SQLException exception) {
                throw new IllegalStateException("Failed to update Block Party data block " + this.databaseId, exception);
            }
        }
    }

    public DimBlockPos getDimBlockPos() {
        Level level = this.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            return new DimBlockPos(serverLevel.dimension(), this.getBlockPos());
        }
        return new DimBlockPos();
    }

    public String getRequiredCondition() {
        return "ALWAYS";
    }

    public int getPriority() {
        return 0;
    }

    public abstract String getTableName();

    public void afterChange() {
    }

    public void afterDelete() {
    }

    public void afterUpdate() {
    }
}
