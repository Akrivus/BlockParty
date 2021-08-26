package block_party.db.records;

import block_party.BlockPartyDB;
import block_party.blocks.entity.ToriiTabletBlockEntity;
import block_party.db.sql.Record;
import block_party.db.sql.Table;
import block_party.util.DimBlockPos;
import block_party.util.sort.RowDistance;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Party extends Record<ToriiTabletBlockEntity> {
    public Party(ResultSet set) throws SQLException {
        super(BlockPartyDB.Parties, set);
    }

    public Party(CompoundTag compound) {
        super(BlockPartyDB.Parties, compound);
    }

    public Party(ToriiTabletBlockEntity entity) {
        super(BlockPartyDB.Parties, entity);
    }

    @Override
    public void sync(ToriiTabletBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ToriiTabletBlockEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static Party findClosest(UUID playerUUID, DimBlockPos pos) {
        return getClosest(BlockPartyDB.Parties.select(String.format("SELECT * FROM Parties WHERE (PlayerUUID = '%s') LIMIT 1;", playerUUID)), pos);
    }

    public static Party getClosest(List<Party> gates, DimBlockPos pos) {
        gates.sort(new RowDistance(pos));
        return gates.get(0);
    }

    public static class Schema extends Table<Party> {
        public Schema() {
            super("Parties");
        }

        @Override
        public Party getRow(ResultSet set) throws SQLException {
            return new Party(set);
        }
    }
}
