package moeblocks.data;

import moeblocks.block.entity.ShimenawaTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ShimenawaTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
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
