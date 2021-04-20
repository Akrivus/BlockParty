package moeblocks.data;

import moeblocks.block.entity.HangingScrollTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HangingScroll extends Row<HangingScrollTileEntity> {
    protected static final int SYMBOL  =  3;

    public HangingScroll(ResultSet set) throws SQLException {
        super(MoeData.HangingScrolls, set);
    }

    public HangingScroll(CompoundNBT compound) {
        super(MoeData.HangingScrolls, compound);
    }

    public HangingScroll(HangingScrollTileEntity entity) {
        super(MoeData.HangingScrolls, entity);
    }

    @Override
    public void sync(HangingScrollTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
        this.get(SYMBOL).set(entity.getSymbol());
    }

    @Override
    public void load(HangingScrollTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<HangingScroll> {
        public Schema() {
            super("HangingScrolls");
        }

        @Override
        public HangingScroll getRow(ResultSet set) throws SQLException {
            return new HangingScroll(set);
        }
    }
}
