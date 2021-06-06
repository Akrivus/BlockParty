package moeblocks.data;

import moeblocks.block.entity.ToriiTabletTileEntity;
import moeblocks.data.sql.Column;
import moeblocks.data.sql.Row;
import moeblocks.data.sql.Table;
import moeblocks.init.MoeWorldData;
import moeblocks.util.DimBlockPos;
import moeblocks.util.sort.RowDistance;
import net.minecraft.nbt.CompoundNBT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ToriiGate extends Row<ToriiTabletTileEntity> {
    protected static final int RANGE =  6;

    public ToriiGate(ResultSet set) throws SQLException {
        super(MoeWorldData.ToriiGates, set);
    }

    public ToriiGate(CompoundNBT compound) {
        super(MoeWorldData.ToriiGates, compound);
    }

    public ToriiGate(ToriiTabletTileEntity entity) {
        super(MoeWorldData.ToriiGates, entity);
    }

    public int getRange() {
        return (Integer) this.get(RANGE).get();
    }

    public void addRange() {
        this.get(RANGE).set(this.getRange() + 1);
    }

    public void subRange() {
        this.get(RANGE).set(this.getRange() - 1);
    }

    @Override
    public void sync(ToriiTabletTileEntity entity) {
        this.get(DATABASE_ID).set(entity.getDatabaseID());
        this.get(POS).set(entity.getDimBlockPos());
        this.get(PLAYER_UUID).set(entity.getPlayerUUID());
    }

    @Override
    public void load(ToriiTabletTileEntity entity) {
        entity.setDatabaseID((UUID) this.get(DATABASE_ID).get());
        entity.setPlayerUUID((UUID) this.get(PLAYER_UUID).get());
    }

    public static ToriiGate findClosest(UUID playerUUID, DimBlockPos pos) {
        return getClosest(MoeWorldData.ToriiGates.select(String.format("SELECT * FROM ToriiGates WHERE (PlayerUUID = '%s') LIMIT 1;", playerUUID)), pos);
    }

    public static ToriiGate findClosest(DimBlockPos pos) {
        return getClosest(MoeWorldData.ToriiGates.select(String.format("SELECT * FROM ToriiGates LIMIT 1;")), pos);
    }

    public static ToriiGate getClosest(List<ToriiGate> gates, DimBlockPos pos) {
        gates.sort(new RowDistance(pos));
        return gates.get(0);
    }

    public static class Schema extends Table<ToriiGate> {
        public Schema() {
            super("ToriiGates");
            this.addColumn(new Column.AsBoolean(this, "Range"));
        }

        @Override
        public ToriiGate getRow(ResultSet set) throws SQLException {
            return new ToriiGate(set);
        }
    }
}
