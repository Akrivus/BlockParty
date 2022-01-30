package block_party.db.sql;

import block_party.db.BlockPartyDB;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Table<R extends Row> {
    private final List<Column> columns = new ArrayList<>();
    private final String name;
    private ServerLevel level;
    private Connection connection;

    public Table(String name, Column... columns) {
        this.name = name;
        this.addColumn(new Column.AsLong(this, "DatabaseID", "PRIMARY_KEY"));
        this.addColumn(new Column.AsPosition(this, "Pos"));
        this.addColumn(new Column.AsUUID(this, "PlayerUUID"));
        for (Column column : columns) {
            this.addColumn(column);
        }
    }

    public void addColumn(Column column) {
        this.columns.add(column);
        column.afterAdd(this);
    }

    public List<Column> getColumns() {
        return new ArrayList<>(this.columns);
    }

    public String getName() {
        return this.name;
    }

    public R find(long id) {
        List<R> query = this.select(String.format("SELECT * FROM %s WHERE DatabaseID = %s LIMIT 1;", this.name, id));
        return query.isEmpty() ? null : query.get(0);
    }

    public List<R> select(String SQL, Object... columns) {
        return this.select(String.format(SQL, columns));
    }

    public List<R> select(String SQL) {
        return this.select(SQL, new ArrayList<>());
    }

    public List<R> select(String SQL, List<Column> columns) {
        ArrayList<R> query = new ArrayList<>();
        try (PreparedStatement sql = this.open(SQL)) {
            for (int i = 1; i <= columns.size(); ++i) { columns.get(i - 1).forSet(i, sql); }
            System.out.println(sql);
            ResultSet set = sql.executeQuery();
            while (set.next()) { query.add(this.getRow(set)); }
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        } finally {
            return query;
        }
    }

    public abstract R getRow(ResultSet set) throws SQLException;

    private PreparedStatement open(String SQL) throws SQLException {
        this.connection = BlockPartyDB.get(this.level).getConnection();
        return this.connection.prepareStatement(SQL);
    }

    private void shut() throws SQLException {
        BlockPartyDB.get(this.level).free(this.connection);
    }

    private void rescue(SQLException e) {
        e.printStackTrace();
        throw new ReportedException(new CrashReport("DB failed.", e));
    }

    public List<R> all() {
        return this.select(String.format("SELECT * FROM %s;", this.name));
    }

    public void update(String SQL) {
        this.update(SQL, new ArrayList<>());
    }

    public void update(String SQL, List<Column> columns) {
        try (PreparedStatement sql = this.open(SQL)) {
            for (int i = 1; i <= columns.size(); ++i) { columns.get(i - 1).forSet(i, sql); }
            System.out.println(sql);
            sql.executeUpdate();
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        }
    }

    public void create(ServerLevel level) {
        this.setWorld(level);
        this.create();
    }

    public void setWorld(ServerLevel level) {
        this.level = level;
    }

    public void create() {
        String columns = "";
        for (Column column : this.columns)
            columns += String.format("%s %s %s,", column.getColumn(), column.getType(), column.getExtra());
        columns = columns.substring(0, columns.length() - 1);
        try (PreparedStatement sql = this.open(String.format("CREATE TABLE IF NOT EXISTS %s (%s);", this.name, columns))) {
            System.out.println(sql);
            sql.execute();
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        }
    }
}
