package block_party.db.records;

import block_party.BlockPartyDB;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.db.sql.Record;
import block_party.db.sql.Table;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Garden extends Record<GardenLanternBlockEntity> {
    public Garden(ResultSet set) throws SQLException {
        super(BlockPartyDB.Gardens, set);
    }

    public Garden(CompoundTag compound) {
        super(BlockPartyDB.Gardens, compound);
    }

    public Garden(GardenLanternBlockEntity entity) {
        super(BlockPartyDB.Gardens, entity);
    }

    @Override
    public void sync(GardenLanternBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(GardenLanternBlockEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<Garden> {
        public Schema() {
            super("GardenLanterns");
        }

        @Override
        public Garden getRow(ResultSet set) throws SQLException {
            return new Garden(set);
        }
    }
}
