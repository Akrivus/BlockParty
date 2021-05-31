package moeblocks.data;

import moeblocks.block.entity.GardenLanternTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GardenLantern extends Row<GardenLanternTileEntity> {
    public GardenLantern(ResultSet set) throws SQLException {
        super(MoeWorldData.GardenLanterns, set);
    }

    public GardenLantern(CompoundNBT compound) {
        super(MoeWorldData.GardenLanterns, compound);
    }

    public GardenLantern(GardenLanternTileEntity entity) {
        super(MoeWorldData.GardenLanterns, entity);
    }

    @Override
    public void sync(GardenLanternTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(GardenLanternTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
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
