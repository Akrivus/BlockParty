package moeblocks.data;

import moeblocks.block.entity.PaperLanternTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaperLantern extends Row<PaperLanternTileEntity> {
    public PaperLantern(ResultSet set) throws SQLException {
        super(MoeData.PaperLanterns, set);
    }

    public PaperLantern(CompoundNBT compound) {
        super(MoeData.PaperLanterns, compound);
    }

    public PaperLantern(PaperLanternTileEntity entity) {
        super(MoeData.PaperLanterns, entity);
    }

    @Override
    public void sync(PaperLanternTileEntity entity) {

    }

    @Override
    public void load(PaperLanternTileEntity entity) {

    }

    public static class Schema extends Table<PaperLantern> {
        public Schema() {
            super("PaperLanterns");
        }

        @Override
        public PaperLantern getRow(ResultSet set) throws SQLException {
            return new PaperLantern(set);
        }
    }
}
