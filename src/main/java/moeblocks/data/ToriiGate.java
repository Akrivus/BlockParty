package moeblocks.data;

import moeblocks.block.entity.ToriiTabletTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ToriiGate extends Row<ToriiTabletTileEntity> {
    public ToriiGate(ResultSet set) throws SQLException {
        super(MoeData.ToriiGates, set);
    }

    public ToriiGate(CompoundNBT compound) {
        super(MoeData.ToriiGates, compound);
    }

    public ToriiGate(ToriiTabletTileEntity entity) {
        super(MoeData.ToriiGates, entity);
    }

    @Override
    public void sync(ToriiTabletTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ToriiTabletTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<ToriiGate> {
        public Schema() {
            super("ToriiGates");
        }

        @Override
        public ToriiGate getRow(ResultSet set) throws SQLException {
            return new ToriiGate(set);
        }
    }
}
