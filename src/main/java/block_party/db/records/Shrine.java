package block_party.db.records;

import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.sql.Row;
import block_party.db.sql.Table;
import block_party.utils.sorters.RowDistance;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Shrine extends Row<ShrineTabletBlockEntity> {
    public Shrine(ResultSet set) throws SQLException {
        super(BlockPartyDB.Shrines, set);
    }

    public Shrine(CompoundTag compound) {
        super(BlockPartyDB.Shrines, compound);
    }

    public Shrine(ShrineTabletBlockEntity entity) {
        super(BlockPartyDB.Shrines, entity);
    }

    @Override
    public void sync(ShrineTabletBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ShrineTabletBlockEntity entity) {
        entity.setDatabaseID((long) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static Shrine findClosest(UUID playerUUID, DimBlockPos pos) {
        return getClosest(BlockPartyDB.Shrines.select(String.format("SELECT * FROM Shrines WHERE (PlayerUUID = '%s') LIMIT 1;", playerUUID)), pos);
    }

    public static Shrine getClosest(List<Shrine> gates, DimBlockPos pos) {
        gates.sort(new RowDistance(pos));
        return gates.get(0);
    }

    public static class Schema extends Table<Shrine> {
        public Schema() {
            super("Shrines");
        }

        @Override
        public Shrine getRow(ResultSet set) throws SQLException {
            return new Shrine(set);
        }
    }
}
