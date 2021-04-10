package moeblocks.data.sql;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Table<R extends Row> {
    private final List<Column> columns = new ArrayList<>();
    private final String name;
    private UUID gameUUID;
    private Connection connection;

    public Table(String name, Column... columns) {
        this.name = name;
        this.addColumn(new Column.AsUUID(this, "DatabaseID", "PRIMARY_KEY"));
        this.addColumn(new Column.AsPosition(this, "Pos"));
        this.addColumn(new Column.AsUUID(this, "PlayerUUID"));
        for (Column column : columns) {
            this.addColumn(column);
        }
    }

    public void setGameUUID(UUID gameUUID) {
        this.gameUUID = gameUUID;
    }

    public List<Column> getColumns() {
        return new ArrayList<>(this.columns);
    }

    public void addColumn(Column column) {
        this.columns.add(column);
        column.afterAdd(this);
    }

    public String getName() {
        return this.name;
    }

    public List<R> select(String SQL, List<Column> columns) {
        ArrayList<R> query = new ArrayList<>();
        try (PreparedStatement sql = this.open(SQL)) {
            for (int i = 1; i <= columns.size(); ++i) { columns.get(i - 1).in(i, sql); }
            ResultSet set = sql.executeQuery();
            while (set.next()) { query.add(this.getRow(set)); }
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        } finally {
            return query;
        }
    }

    public List<R> select(String SQL) {
        return this.select(SQL, new ArrayList<>());
    }

    public R find(UUID uuid) {
        List<R> query = this.select(String.format("SELECT * FROM Moes WHERE DatabaseID = '%s'", uuid.toString()));
        return query.isEmpty() ? null : query.get(0);
    }

    public void update(String SQL, List<Column> columns) {
        try (PreparedStatement sql = this.open(SQL)) {
            for (int i = 1; i <= columns.size(); ++i) { columns.get(i - 1).in(i, sql); }
            sql.executeUpdate();
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        }
    }

    public void update(String SQL) {
        this.update(SQL, new ArrayList<>());
    }

    public void create() {
        String columns = "";
        for (Column column : this.columns) { columns += String.format("%s %s %s,", column.getColumn(), column.getType(), column.getExtra()); }
        columns = columns.substring(0, columns.length() - 1);
        try (PreparedStatement sql = this.open(String.format("CREATE TABLE IF NOT EXISTS %s (%s);", this.name, columns))) {
            sql.execute();
            this.shut();
        } catch (SQLException e) {
            this.rescue(e);
        }
    }

    public void create(UUID gameUUID) {
        this.setGameUUID(gameUUID);
        this.create();
    }

    public abstract R getRow(ResultSet set) throws SQLException;

    private PreparedStatement open(String SQL, Class clazz) throws SQLException {
        File path = new File(String.format("./databases/%s.moedb", this.gameUUID.toString()));
        this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", path.getAbsolutePath()));
        return this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
    }

    private PreparedStatement open(String SQL) throws SQLException {
        try {
            return this.open(SQL, Class.forName("org.sqlite.JDBC"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void shut() throws SQLException {
        this.connection.close();
    }

    private void rescue(SQLException e) {
        throw new ReportedException(new CrashReport("DB failed.", e));
    }
}
