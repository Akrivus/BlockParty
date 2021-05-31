package moeblocks.block.entity;

import moeblocks.data.IModelEntity;
import moeblocks.data.sql.Row;
import moeblocks.util.DimBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.UUID;

public abstract class AbstractDataTileEntity<M extends Row> extends TileEntity implements IModelEntity<M> {
    protected UUID id = UUID.randomUUID();
    protected UUID playerUUID;
    protected boolean claimed;

    protected AbstractDataTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if (compound.hasUniqueId("DatabaseID")) {
            this.id = compound.getUniqueId("DatabaseID");
            if (compound.getBoolean("HasRow")) {
                this.getRow().load(this);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("DatabaseID", this.id);
        compound.putBoolean("HasRow", this.hasRow());
        return super.write(compound);
    }

    @Override
    public void markDirty() {
        if (this.hasRow()) { this.getRow().update(); }
        super.markDirty();
    }

    @Override
    public void remove() {
        if (this.hasRow()) { this.getRow().delete(); }
        super.remove();
    }

    @Override
    public DimBlockPos getDimBlockPos() {
        return new DimBlockPos(this.world.getDimensionKey(), this.getPos());
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
    public UUID getDatabaseID() {
        return this.id;
    }

    @Override
    public void setDatabaseID(UUID id) {
        this.id = id;
    }

    @Override
    public boolean hasRow() {
        return this.claimed && this.getRow() != null;
    }

    @Override
    public boolean claim(PlayerEntity player) {
        if (IModelEntity.super.claim(player)) {
            this.claimed = true;
            this.markDirty();
        }
        return this.claimed;
    }
}
