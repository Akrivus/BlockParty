package moeblocks.data;

import moeblocks.block.entity.SakuraSaplingTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SakuraSapling extends Row<SakuraSaplingTileEntity> {
    public SakuraSapling(ResultSet set) throws SQLException {
        super(MoeData.SakuraTrees, set);
    }

    public SakuraSapling(CompoundNBT compound) {
        super(MoeData.SakuraTrees, compound);
    }

    public SakuraSapling(SakuraSaplingTileEntity entity) {
        super(MoeData.SakuraTrees, entity);
    }

    @Override
    public void sync(SakuraSaplingTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(SakuraSaplingTileEntity entity) {

    }

    public static class Schema extends Table<SakuraSapling> {
        public Schema() {
            super("SakuraSaplings");
        }

        @Override
        public SakuraSapling getRow(ResultSet set) throws SQLException {
            return new SakuraSapling(set);
        }
    }
}
