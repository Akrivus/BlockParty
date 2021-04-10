package moeblocks.data;

import moeblocks.block.entity.GardenLanternTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GardenLantern extends Row<GardenLanternTileEntity> {
    public GardenLantern(ResultSet set) throws SQLException {
        super(MoeData.GardenLanterns, set);
    }

    public GardenLantern(CompoundNBT compound) {
        super(MoeData.GardenLanterns, compound);
    }

    public GardenLantern(GardenLanternTileEntity entity) {
        super(MoeData.GardenLanterns, entity);
    }

    @Override
    public void sync(GardenLanternTileEntity entity) {

    }

    @Override
    public void load(GardenLanternTileEntity entity) {

    }

    public static class Schema extends Table<GardenLantern> {
        public Schema() {
            super("GardenLanterns");
        }

        @Override
        public GardenLantern getRow(ResultSet set) throws SQLException {
            return new GardenLantern(set);
        }
    }
}
