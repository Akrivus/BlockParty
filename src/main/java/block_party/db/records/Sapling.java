package block_party.db.records;

import block_party.BlockPartyDB;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.db.sql.Record;
import block_party.db.sql.Table;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Sapling extends Record<SakuraSaplingBlockEntity> {
    public Sapling(ResultSet set) throws SQLException {
        super(BlockPartyDB.Saplings, set);
    }

    public Sapling(CompoundTag compound) {
        super(BlockPartyDB.Saplings, compound);
    }

    public Sapling(SakuraSaplingBlockEntity entity) {
        super(BlockPartyDB.Saplings, entity);
    }

    @Override
    public void sync(SakuraSaplingBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(SakuraSaplingBlockEntity entity) {
        entity.setDatabaseID((long) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<Sapling> {
        public Schema() {
            super("SakuraSaplings");
        }

        @Override
        public Sapling getRow(ResultSet set) throws SQLException {
            return new Sapling(set);
        }
    }
}
