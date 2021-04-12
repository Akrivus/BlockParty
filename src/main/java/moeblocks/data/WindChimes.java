package moeblocks.data;

import moeblocks.block.entity.WindChimesTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WindChimes extends Row<WindChimesTileEntity> {
    public WindChimes(ResultSet set) throws SQLException {
        super(MoeData.WindChimes, set);
    }

    public WindChimes(CompoundNBT compound) {
        super(MoeData.WindChimes, compound);
    }

    public WindChimes(WindChimesTileEntity entity) {
        super(MoeData.WindChimes, entity);
    }

    @Override
    public void sync(WindChimesTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(WindChimesTileEntity entity) {

    }

    public static class Schema extends Table<WindChimes> {
        public Schema() {
            super("WindChimes");
        }

        @Override
        public WindChimes getRow(ResultSet set) throws SQLException {
            return new WindChimes(set);
        }
    }
}
