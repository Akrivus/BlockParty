package block_party.db.sql;

import block_party.db.DimBlockPos;
import block_party.db.Recordable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Row<E extends Recordable> {
    public static final int DATABASE_ID = 0;
    public static final int POS = 1;
    public static final int POS_X = 2;
    public static final int POS_Y = 3;
    public static final int POS_Z = 4;
    public static final int PLAYER_UUID = 5;
    private final Table table;
    private final List<Column> columns;
    private final String name;

    public Row(Table table, ResultSet set) throws SQLException {
        this(table);
        for (int i = 0; i < this.columns.size(); ++i) {
            this.columns.get(i).setFromSet(i + 1, set);
        }
    }

    public Row(Table table) {
        this.table = table;
        this.columns = table.getColumns();
        this.name = table.getName();
    }

    public Row(Table table, CompoundTag compound) {
        this(table);
        this.read(compound);
    }

    public void read(CompoundTag compound) {
        for (Column column : this.getColumns()) {
            column.read(compound);
        }
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public Row(Table table, E entity) {
        this(table);
        this.sync(entity);
    }

    public abstract void sync(E entity);

    @Override
    public String toString() {
        return String.format("%s[%d]", this.table, this.get(DATABASE_ID).get());
    }

    public BlockPos getPos() {
        return this.getDimPos().getPos();
    }

    public DimBlockPos getDimPos() {
        return (DimBlockPos) this.get(POS).get();
    }

    public Column get(int i) {
        return this.columns.get(i);
    }

    public boolean isDim(ResourceKey<Level> dim) {
        return this.getDimPos().getDim() == dim;
    }

    public void insert() {
        this.table.update(String.format("INSERT INTO %s(%s) VALUES (%s);", this.name, this.getColumnNames(), this.getQuestionMarks()), this.getColumns());
    }

    private String getColumnNames() {
        return this.getColumnNames(this.getColumns());
    }

    private String getColumnNames(List<Column> columns) {
        String names = "";
        for (Column column : columns) { names += String.format("%s, ", column.getColumn()); }
        return names.substring(0, names.length() - 2);
    }

    private String getQuestionMarks() {
        return this.getQuestionMarks(this.getColumns());
    }

    private String getQuestionMarks(List<Column> columns) {
        String marks = "";
        for (Column column : columns) { marks += "?, "; }
        return marks.substring(0, marks.length() - 2);
    }

    public void delete() {
        this.table.update(String.format("DELETE FROM %s WHERE (DatabaseID = '%s');", this.name, this.getID()));
    }

    public long getID() {
        return (long) this.get(DATABASE_ID).get();
    }

    public void update(Consumer<Row> query) {
        query.accept(this);
        this.update();
    }

    public void update(E entity, Consumer<Row> query) {
        query.accept(this);
        this.update(entity);
    }

    public void update(E entity) {
        this.sync(entity);
        this.update();
    }

    public void update() {
        this.table.update(String.format("UPDATE %s SET %s WHERE (DatabaseID = '%s');", this.name, this.getDirtyColumnSetters(), this.getID()), this.getDirtyColumns());
    }

    private String getDirtyColumnSetters() {
        return this.getColumnSetters(this.getDirtyColumns());
    }

    public List<Column> getDirtyColumns() {
        List<Column> columns = new ArrayList<>();
        for (Column column : this.columns) {
            if (column.isDirty()) { columns.add(column); }
        }
        return columns;
    }

    private String getColumnSetters(List<Column> columns) {
        String names = "";
        for (Column column : columns) { names += String.format("%s = ?, ", column.getColumn()); }
        return names.substring(0, names.length() - 2);
    }

    public CompoundTag write() {
        return this.write(new CompoundTag());
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putString("Table", this.name);
        for (Column column : this.getColumns()) {
            column.write(compound);
        }
        return compound;
    }

    public List<Column> getColumns(int... indexes) {
        List<Column> columns = new ArrayList<>();
        for (int index : indexes) {
            columns.add(this.columns.get(index));
        }
        return columns;
    }

    private String getDirtyColumnNames() {
        return this.getColumnNames(this.getDirtyColumns());
    }

    private String getDirtyQuestionMarks() {
        return this.getColumnNames(this.getDirtyColumns());
    }

    private String getColumnSetters() {
        return this.getColumnSetters(this.getColumns());
    }

    public abstract void load(E entity);
}
