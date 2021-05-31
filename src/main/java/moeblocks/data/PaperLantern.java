package moeblocks.data;

import moeblocks.block.entity.PaperLanternTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PaperLantern extends Row<PaperLanternTileEntity> {
    public PaperLantern(ResultSet set) throws SQLException {
        super(MoeWorldData.PaperLanterns, set);
    }

    public PaperLantern(CompoundNBT compound) {
        super(MoeWorldData.PaperLanterns, compound);
    }

    public PaperLantern(PaperLanternTileEntity entity) {
        super(MoeWorldData.PaperLanterns, entity);
    }

    @Override
    public void sync(PaperLanternTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(PaperLanternTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<PaperLantern> {
        public Schema() {
            super("PaperLanterns");
        }

        @Override
        public PaperLantern getRow(ResultSet set) throws SQLException {
            return new PaperLantern(set);
        }
    }
}
