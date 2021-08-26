package block_party.db.records;

import block_party.BlockPartyDB;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.db.sql.Record;
import block_party.db.sql.Table;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Gathering extends Record<HangingScrollBlockEntity> {
    protected static final int SYMBOL  =  3;

    public Gathering(ResultSet set) throws SQLException {
        super(BlockPartyDB.Gatherings, set);
    }

    public Gathering(CompoundTag compound) {
        super(BlockPartyDB.Gatherings, compound);
    }

    public Gathering(HangingScrollBlockEntity entity) {
        super(BlockPartyDB.Gatherings, entity);
    }

    @Override
    public void sync(HangingScrollBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
        this.get(SYMBOL).set(entity.getSymbol());
    }

    @Override
    public void load(HangingScrollBlockEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<Gathering> {
        public Schema() {
            super("HangingScrolls");
        }

        @Override
        public Gathering getRow(ResultSet set) throws SQLException {
            return new Gathering(set);
        }
    }
}
