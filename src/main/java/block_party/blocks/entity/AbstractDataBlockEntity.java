package block_party.blocks.entity;

import block_party.db.Recordable;
import block_party.db.sql.Record;
import block_party.util.DimBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class AbstractDataBlockEntity<M extends Record> extends BlockEntity implements Recordable<M> {
    protected long id;
    protected UUID playerUUID;
    protected boolean claimed;

    protected AbstractDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.id = pos.asLong();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.hasUUID("DatabaseID")) {
            this.id = compound.getLong("DatabaseID");
            if (compound.getBoolean("HasRow")) {
                this.getRow().load(this);
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putLong("DatabaseID", this.id);
        compound.putBoolean("HasRow", this.hasRow());
        return super.save(compound);
    }

    @Override
    public void setChanged() {
        if (this.hasRow()) { this.getRow().update(); }
        super.setChanged();
    }

    @Override
    public void setRemoved() {
        if (this.hasRow()) { this.getRow().delete(); }
        super.setRemoved();
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.level.dimension(), this.getBlockPos());
    }

    @Override
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public long getDatabaseID() {
        return this.id;
    }

    @Override
    public void setDatabaseID(long id) {
        this.id = id;
    }

    @Override
    public boolean hasRow() {
        return this.claimed && this.getRow() != null;
    }

    @Override
    public boolean claim(Player player) {
        if (Recordable.super.claim(player)) {
            this.claimed = true;
            this.setChanged();
        }
        return this.claimed;
    }

    @Override
    public Level getWorld() {
        return this.level;
    }
}
