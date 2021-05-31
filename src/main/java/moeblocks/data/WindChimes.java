package moeblocks.data;

import moeblocks.block.entity.WindChimesTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WindChimes extends Row<WindChimesTileEntity> {
    public WindChimes(ResultSet set) throws SQLException {
        super(MoeWorldData.WindChimes, set);
    }

    public WindChimes(CompoundNBT compound) {
        super(MoeWorldData.WindChimes, compound);
    }

    public WindChimes(WindChimesTileEntity entity) {
        super(MoeWorldData.WindChimes, entity);
    }

    @Override
    public void sync(WindChimesTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(WindChimesTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
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
