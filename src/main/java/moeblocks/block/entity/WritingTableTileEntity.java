package moeblocks.block.entity;

import moeblocks.data.WritingTable;
import moeblocks.init.MoeTileEntities;
import moeblocks.init.MoeWorldData;

public class WritingTableTileEntity extends AbstractDataTileEntity<WritingTable> {
    public WritingTableTileEntity() {
        super(MoeTileEntities.WRITING_TABLE.get());
    }

    @Override
    public WritingTable getRow() {
        return MoeWorldData.WritingTables.find(this.getDatabaseID());
    }

    @Override
    public WritingTable getNewRow() {
        return new WritingTable(this);
    }
}
