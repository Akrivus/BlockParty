package moeblocks.data;

import moeblocks.block.entity.WritingTableTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WritingTable extends Row<WritingTableTileEntity> {
    public WritingTable(ResultSet set) throws SQLException {
        super(MoeData.WritingTables, set);
    }

    public WritingTable(CompoundNBT compound) {
        super(MoeData.WritingTables, compound);
    }

    public WritingTable(WritingTableTileEntity entity) {
        super(MoeData.WritingTables, entity);
    }

    @Override
    public void sync(WritingTableTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(WritingTableTileEntity entity) {

    }

    public static class Schema extends Table<WritingTable> {
        public Schema() {
            super("WritingTables");
        }

        @Override
        public WritingTable getRow(ResultSet set) throws SQLException {
            return new WritingTable(set);
        }
    }
}
