package moeblocks.data;

import moeblocks.block.entity.LuckyCatTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LuckyCat extends Row<LuckyCatTileEntity> {
    public LuckyCat(ResultSet set) throws SQLException {
        super(MoeData.LuckyCats, set);
    }

    public LuckyCat(CompoundNBT compound) {
        super(MoeData.LuckyCats, compound);
    }

    public LuckyCat(LuckyCatTileEntity entity) {
        super(MoeData.LuckyCats, entity);
    }

    @Override
    public void sync(LuckyCatTileEntity entity) {

    }

    @Override
    public void load(LuckyCatTileEntity entity) {

    }

    public static class Schema extends Table<LuckyCat> {
        public Schema() {
            super("LuckyCats");
        }

        @Override
        public LuckyCat getRow(ResultSet set) throws SQLException {
            return new LuckyCat(set);
        }
    }
}
