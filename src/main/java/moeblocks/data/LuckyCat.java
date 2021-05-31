package moeblocks.data;

import moeblocks.block.entity.LuckyCatTileEntity;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LuckyCat extends Row<LuckyCatTileEntity> {
    public LuckyCat(ResultSet set) throws SQLException {
        super(MoeWorldData.LuckyCats, set);
    }

    public LuckyCat(CompoundNBT compound) {
        super(MoeWorldData.LuckyCats, compound);
    }

    public LuckyCat(LuckyCatTileEntity entity) {
        super(MoeWorldData.LuckyCats, entity);
    }

    @Override
    public void sync(LuckyCatTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(LuckyCatTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
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
