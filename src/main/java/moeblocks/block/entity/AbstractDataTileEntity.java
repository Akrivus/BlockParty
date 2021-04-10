package moeblocks.block.entity;

import moeblocks.data.IModelEntity;
import moeblocks.data.sql.Row;
import moeblocks.util.DimBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.UUID;

public abstract class AbstractDataTileEntity<M extends Row> extends TileEntity implements IModelEntity<M> {
    protected UUID id;
    protected UUID entityID;
    protected UUID playerUUID;

    public AbstractDataTileEntity(TileEntityType<?> type) {
        super(type);
        this.entityID = UUID.randomUUID();
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.entityID = compound.getUniqueId("EntityID");
        if (compound.getBoolean("HasRow")) {
            this.playerUUID = compound.getUniqueId("PlayerUUID");
            this.id = compound.getUniqueId("DatabaseID");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("HasRow", this.hasRow());
        compound.putUniqueId("EntityID", this.entityID);
        if (compound.getBoolean("HasRow")) {
            compound.putUniqueId("PlayerUUID", this.playerUUID);
            compound.putUniqueId("DatabaseID", this.id);
        }
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
        return this.id != null;
    }
}
