package block_party.db.records;

import block_party.BlockPartyDB;
import block_party.blocks.entity.LocativeBlockEntity;
import block_party.db.sql.Column;
import block_party.db.sql.Record;
import block_party.db.sql.Table;
import block_party.mob.automata.Condition;
import net.minecraft.nbt.CompoundTag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Location extends Record<LocativeBlockEntity> {
    protected static final int REQUIRED_CONDITION = 3;
    protected static final int PRIORITY = 4;

    public Location(ResultSet set) throws SQLException {
        super(BlockPartyDB.Locations, set);
    }

    public Location(CompoundTag compound) {
        super(BlockPartyDB.Locations, compound);
    }

    public Location(LocativeBlockEntity entity) {
        super(BlockPartyDB.Locations, entity);
    }

    @Override
    public void sync(LocativeBlockEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
        this.get(REQUIRED_CONDITION).set(entity.getRequiredCondition());
        this.get(PRIORITY).set(entity.getPriority());
    }

    @Override
    public void load(LocativeBlockEntity entity) {
        entity.setDatabaseID((long) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static class Schema extends Table<Location> {
        public Schema() {
            super("Locations");
            this.addColumn(new Column.AsEnum<>(this, "RequiredCondition", Condition.ALWAYS, (value) -> Condition.valueOf(value)));
            this.addColumn(new Column.AsInteger(this, "Priority"));
        }

        @Override
        public Location getRow(ResultSet set) throws SQLException {
            return new Location(set);
        }
    }
}
