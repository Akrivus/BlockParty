package moeblocks.data;

import moeblocks.block.entity.ShimenawaTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Shimenawa extends Row<ShimenawaTileEntity> {
    public Shimenawa(ResultSet set) throws SQLException {
        super(MoeData.Shimenawa, set);
    }

    public Shimenawa(CompoundNBT compound) {
        super(MoeData.Shimenawa, compound);
    }

    public Shimenawa(ShimenawaTileEntity entity) {
        super(MoeData.Shimenawa, entity);
    }

    @Override
    public void sync(ShimenawaTileEntity entity) {

    }

    @Override
    public void load(ShimenawaTileEntity entity) {

    }

    public static class Schema extends Table<Shimenawa> {
        public Schema() {
            super("Shimenawa");
        }

        @Override
        public Shimenawa getRow(ResultSet set) throws SQLException {
            return new Shimenawa(set);
        }
    }
}
