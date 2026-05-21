package block_party.regression;

import block_party.db.DimBlockPos;
import block_party.db.Recordable;
import block_party.db.records.Garden;
import block_party.db.records.Location;
import block_party.db.records.NPC;
import block_party.db.records.Sapling;
import block_party.db.records.Shrine;
import block_party.db.sql.Row;
import block_party.db.sql.Table;
import block_party.scene.SceneObservation;
import block_party.scene.traits.BloodType;
import block_party.scene.traits.Dere;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertFalse;
import static block_party.regression.TestSupport.assertThrows;
import static block_party.regression.TestSupport.assertTrue;

final class PersistenceRegressionTest implements RegressionTest {
    private static final int LOCATION_REQUIRED_CONDITION = 6;
    private static final int LOCATION_PRIORITY = 7;

    @Override
    public void run() {
        testDimBlockPosRoundTrip();
        testDimBlockPosCorruptedDimensionFailsDuringRead();
        testCoreStoredLocationRowsRoundTrip();
        testLocationRowRoundTripPreservesConditionAndPriority();
        testNpcMissingOptionalFieldsUseCurrentDefaults();
        testNpcInvalidTraitPayloadFallsBackToColumnDefault();
        testNpcRowRoundTripPreservesCoreProfileColumns();
        testMultipleNpcRowsDoNotShareMutableColumnState();
        testNpcCorruptedPositionPayloadFailsDuringRead();
        testGeneratedSqlStringsAreSyntacticallyValidForSQLite();
        testUpdateSqlIncludesSetAndWhereClauses();
        testNoopRowUpdateDoesNotExecuteSql();
        testLoadedRowNoopUpdateDoesNotTreatLoadedColumnsAsDirty();
        testDirtyRowUpdateBindsSetColumnsAndWhereId();
    }

    private void testDimBlockPosRoundTrip() {
        DimBlockPos pos = new DimBlockPos(Level.NETHER, new BlockPos(12, 64, -9));
        CompoundTag written = pos.write();
        DimBlockPos restored = new DimBlockPos(written);

        assertEquals("minecraft:the_nether", written.getString("Dimension"), "DimBlockPos writes dimension key");
        assertEquals(new BlockPos(12, 64, -9), restored.getPos(), "DimBlockPos restores coordinates");
        assertEquals(Level.NETHER, restored.getDim(), "DimBlockPos restores dimension");
        assertFalse(restored.isEmpty(), "Non-empty DimBlockPos stays non-empty");

        DimBlockPos empty = new DimBlockPos(new DimBlockPos().write());
        assertEquals(BlockPos.ZERO, empty.getPos(), "Empty DimBlockPos stores zero coordinates");
        assertEquals(Level.OVERWORLD, empty.getDim(), "Empty DimBlockPos stores overworld dimension");
        assertTrue(empty.isEmpty(), "Empty DimBlockPos restores empty flag");
    }

    private void testDimBlockPosCorruptedDimensionFailsDuringRead() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Coordinates", BlockPos.ZERO.asLong());
        tag.putString("Dimension", "not a valid id");
        tag.putBoolean("IsEmpty", false);

        assertThrows(net.minecraft.ResourceLocationException.class, () -> new DimBlockPos(tag), "DimBlockPos corrupted dimension currently fails during read");
    }

    private void testCoreStoredLocationRowsRoundTrip() {
        assertLocationLikeRowRoundTrip(new Shrine(rowTag(11L, "minecraft:overworld", new BlockPos(1, 2, 3))));
        assertLocationLikeRowRoundTrip(new Garden(rowTag(12L, "minecraft:the_nether", new BlockPos(-1, 64, 8))));
        assertLocationLikeRowRoundTrip(new Sapling(rowTag(13L, "minecraft:the_end", new BlockPos(7, 80, -4))));
    }

    private void testLocationRowRoundTripPreservesConditionAndPriority() {
        CompoundTag tag = rowTag(21L, "minecraft:overworld", new BlockPos(4, 5, 6));
        tag.putString("RequiredCondition", "NIGHT");
        tag.putInt("Priority", 9);

        Location restored = new Location(new Location(tag).write());

        assertEquals(21L, restored.getID(), "Location round trip preserves ID");
        assertEquals(new BlockPos(4, 5, 6), restored.getPos(), "Location round trip preserves position");
        assertEquals(SceneObservation.NIGHT, restored.get(LOCATION_REQUIRED_CONDITION).get(), "Location round trip preserves required condition");
        assertEquals(9, restored.get(LOCATION_PRIORITY).get(), "Location round trip preserves priority");
    }

    private void testNpcRowRoundTripPreservesCoreProfileColumns() {
        NPC restored = new NPC(new NPC(npcTag(31L, "Sora")).write());

        assertEquals(31L, restored.getID(), "NPC round trip preserves ID");
        assertEquals(new BlockPos(9, 10, 11), restored.getPos(), "NPC round trip preserves position");
        assertEquals("Sora", restored.getName(), "NPC round trip preserves name");
        assertEquals(BloodType.AB, restored.get(NPC.BLOOD_TYPE).get(), "NPC round trip preserves blood type");
        assertEquals(Dere.KUUDERE, restored.get(NPC.DERE).get(), "NPC round trip preserves dere");
        assertEquals(18.5F, restored.get(NPC.HEALTH).get(), "NPC round trip preserves health");
        assertEquals(true, restored.get(NPC.HAS_HOME).get(), "NPC round trip preserves has-home flag");
        assertEquals(new BlockPos(12, 70, -2), ((DimBlockPos) restored.get(NPC.HOME_POS).get()).getPos(), "NPC round trip preserves home position");
    }

    private void testNpcMissingOptionalFieldsUseCurrentDefaults() {
        CompoundTag tag = rowTag(32L, "minecraft:overworld", new BlockPos(0, 64, 0));
        tag.put("HomePosDim", dimPosTag("minecraft:overworld", 0, 0, 0, true));

        NPC npc = new NPC(tag);

        assertEquals("", npc.getName(), "NPC missing name defaults to empty string");
        assertEquals(BloodType.O, npc.get(NPC.BLOOD_TYPE).get(), "NPC missing blood type defaults to O");
        assertEquals(Dere.NYANDERE, npc.get(NPC.DERE).get(), "NPC missing dere defaults to NYANDERE");
        assertEquals(0.0F, npc.get(NPC.HEALTH).get(), "NPC missing health defaults to zero");
        assertEquals(false, npc.get(NPC.HAS_HOME).get(), "NPC missing has-home defaults to false");
    }

    private void testNpcInvalidTraitPayloadFallsBackToColumnDefault() {
        CompoundTag tag = npcTag(33L, "InvalidTrait");
        tag.putString("BloodType", "bogus");
        tag.putString("Dere", "also_bogus");

        NPC npc = new NPC(tag);

        assertEquals(BloodType.O, npc.get(NPC.BLOOD_TYPE).get(), "NPC invalid blood type falls back to O");
        assertEquals(Dere.NYANDERE, npc.get(NPC.DERE).get(), "NPC invalid dere falls back to NYANDERE");
    }

    private void testMultipleNpcRowsDoNotShareMutableColumnState() {
        new NPC(npcTag(35L, "PreviousRowWithAB"));

        CompoundTag missing = rowTag(36L, "minecraft:overworld", new BlockPos(0, 64, 0));
        missing.put("HomePosDim", dimPosTag("minecraft:overworld", 0, 0, 0, true));
        NPC isolated = new NPC(missing);

        assertEquals(BloodType.O, isolated.get(NPC.BLOOD_TYPE).get(), "NPC missing blood type does not leak previous row value");
        assertEquals(Dere.NYANDERE, isolated.get(NPC.DERE).get(), "NPC missing dere does not leak previous row value");
    }

    private void testNpcCorruptedPositionPayloadFailsDuringRead() {
        CompoundTag tag = npcTag(34L, "BadPosition");
        tag.getCompound("PosDim").putString("Dimension", "bad dimension");

        assertThrows(net.minecraft.ResourceLocationException.class, () -> new NPC(tag), "NPC corrupted position dimension currently fails during read");
    }

    private void testGeneratedSqlStringsAreSyntacticallyValidForSQLite() {
        CapturingTable table = new CapturingTable();
        FakeRow row = new FakeRow(table);
        row.get(Row.DATABASE_ID).set(1L);
        row.get(FakeRow.NAME).set("Aki");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            connection.prepareStatement(table.getCreateTableSQL()).execute();
            connection.prepareStatement(row.getInsertSQL()).close();
            connection.prepareStatement(row.getUpdateSQL()).close();
            connection.prepareStatement(row.getDeleteSQL()).close();
        } catch (SQLException e) {
            throw new AssertionError("Generated SQL should be valid SQLite syntax", e);
        }
    }

    private void testUpdateSqlIncludesSetAndWhereClauses() {
        FakeRow row = new FakeRow(new FakeTable());
        row.get(Row.DATABASE_ID).set(5L);
        row.get(FakeRow.NAME).set("Ren");

        assertEquals("UPDATE FakeRows SET DatabaseID = ?, Name = ? WHERE DatabaseID = ?;", row.getUpdateSQL(), "Dirty update SQL includes SET and WHERE clauses");
    }

    private void testNoopRowUpdateDoesNotExecuteSql() {
        CapturingTable table = new CapturingTable();
        FakeRow row = new FakeRow(table);
        assertEquals(0, row.getDirtyColumns().size(), "Fresh row has no dirty columns");
        row.update();
        assertEquals(0, table.updateCalls, "No-op Row.update does not execute SQL");
        assertEquals(null, row.getUpdateSQL(), "No-op Row.update has no SQL");
    }

    private void testLoadedRowNoopUpdateDoesNotTreatLoadedColumnsAsDirty() {
        CapturingTable table = new CapturingTable();
        FakeRow inserted = new FakeRow(table);
        inserted.get(Row.DATABASE_ID).set(42L);
        inserted.get(Row.POS).set(new DimBlockPos(Level.OVERWORLD, new BlockPos(1, 2, 3)));
        inserted.get(Row.PLAYER_UUID).set(new UUID(1L, 2L));
        inserted.get(FakeRow.NAME).set("Loaded");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            connection.prepareStatement(table.getCreateTableSQL()).execute();
            try (java.sql.PreparedStatement statement = connection.prepareStatement(inserted.getInsertSQL())) {
                for (int i = 1; i <= inserted.getColumns().size(); ++i) {
                    inserted.getColumns().get(i - 1).forSet(i, statement);
                }
                statement.executeUpdate();
            }

            FakeRow loaded = loadFakeRow(connection, table);
            assertEquals("Loaded", loaded.get(FakeRow.NAME).get(), "Loaded row preserves inserted text field");
            assertEquals(0, loaded.getDirtyColumns().size(), "ResultSet-loaded columns establish a clean baseline");
            assertEquals(null, loaded.getUpdateSQL(), "Loaded no-op update has no SQL");

            loaded.update();

            assertEquals(0, table.updateCalls, "No-op update on a loaded row does not execute SQL");
            assertEquals(null, table.lastSQL, "No-op update on a loaded row generates no SQL");

            table.reset();
            FakeRow renamed = loadFakeRow(connection, table);
            renamed.get(FakeRow.NAME).set("Changed");
            renamed.update();

            assertEquals(1, table.updateCalls, "Mutating a loaded row still executes an update");
            assertEquals("UPDATE FakeRows SET Name = ? WHERE DatabaseID = ?;", table.lastSQL, "Loaded row mutation updates only the dirty column");
            assertEquals(2, table.lastColumnCount, "Loaded row mutation binds the dirty column plus DatabaseID WHERE parameter");

            table.reset();
            FakeRow moved = loadFakeRow(connection, table);
            moved.get(Row.POS).set(new DimBlockPos(Level.OVERWORLD, new BlockPos(1, 2, 3)));
            moved.update();

            assertEquals(1, table.updateCalls, "Mutating a loaded position still executes an update");
            assertEquals("UPDATE FakeRows SET PosDim = ?, PosX = ?, PosY = ?, PosZ = ? WHERE DatabaseID = ?;", table.lastSQL, "Loaded position mutation updates the composite position columns together");
            assertEquals(5, table.lastColumnCount, "Loaded position mutation binds the composite position plus DatabaseID WHERE parameter");
        } catch (SQLException e) {
            throw new AssertionError("Loaded row no-op updates should stay safe while later mutations remain dirty", e);
        }
    }

    private FakeRow loadFakeRow(Connection connection, FakeTable table) throws SQLException {
        try (java.sql.PreparedStatement statement = connection.prepareStatement("SELECT * FROM FakeRows WHERE DatabaseID = 42;");
             ResultSet set = statement.executeQuery()) {
            assertTrue(set.next(), "Inserted row can be loaded back through ResultSet");
            return table.getRow(set);
        }
    }

    private void testDirtyRowUpdateBindsSetColumnsAndWhereId() {
        CapturingTable table = new CapturingTable();
        FakeRow row = new FakeRow(table);
        row.get(Row.DATABASE_ID).set(9L);
        row.get(FakeRow.NAME).set("Mika");

        row.update();

        assertEquals("UPDATE FakeRows SET DatabaseID = ?, Name = ? WHERE DatabaseID = ?;", table.lastSQL, "Dirty Row.update SQL");
        assertEquals(3, table.lastColumnCount, "Dirty Row.update binds dirty columns plus DatabaseID WHERE parameter");
    }

    private void assertLocationLikeRowRoundTrip(Row row) {
        Row restored;
        if (row instanceof Shrine) {
            restored = new Shrine(row.write());
        } else if (row instanceof Garden) {
            restored = new Garden(row.write());
        } else if (row instanceof Sapling) {
            restored = new Sapling(row.write());
        } else {
            throw new AssertionError("Unsupported row type " + row.getClass().getName());
        }

        assertEquals(row.getID(), restored.getID(), row.getClass().getSimpleName() + " round trip preserves ID");
        assertEquals(row.getPos(), restored.getPos(), row.getClass().getSimpleName() + " round trip preserves position");
        assertEquals(row.get(Row.PLAYER_UUID).get(), restored.get(Row.PLAYER_UUID).get(), row.getClass().getSimpleName() + " round trip preserves owner UUID");
    }

    private CompoundTag rowTag(long id, String dim, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("DatabaseID", id);
        tag.put("PosDim", dimPosTag(dim, pos.getX(), pos.getY(), pos.getZ(), false));
        tag.putUUID("PlayerUUID", new UUID(0L, 0L));
        return tag;
    }

    private CompoundTag npcTag(long id, String name) {
        CompoundTag tag = rowTag(id, "minecraft:overworld", new BlockPos(9, 10, 11));
        tag.putBoolean("Dead", false);
        tag.putString("Name", name);
        tag.putString("BloodType", "AB");
        tag.putString("Dere", "KUUDERE");
        tag.putFloat("Health", 18.5F);
        tag.putFloat("FoodLevel", 19.0F);
        tag.putFloat("Exhaustion", 1.0F);
        tag.putFloat("Saturation", 4.0F);
        tag.putFloat("Stress", 2.0F);
        tag.putFloat("Relaxation", 3.0F);
        tag.putFloat("Loyalty", 5.0F);
        tag.putFloat("Affection", 6.0F);
        tag.putFloat("Slouch", 7.0F);
        tag.putFloat("Age", 8.0F);
        tag.putLong("LastSeenAt", 123L);
        tag.putInt("BlockState", 0);
        tag.putBoolean("Hiding", false);
        tag.putBoolean("HasHome", true);
        tag.put("HomePosDim", dimPosTag("minecraft:the_nether", 12, 70, -2, false));
        return tag;
    }

    private CompoundTag dimPosTag(String dim, int x, int y, int z, boolean empty) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Coordinates", new BlockPos(x, y, z).asLong());
        tag.putString("Dimension", dim);
        tag.putBoolean("IsEmpty", empty);
        return tag;
    }

    private static class FakeTable extends Table<FakeRow> {
        private FakeTable() {
            super("FakeRows");
            this.addColumn(new block_party.db.sql.Column.AsString(this, "Name"));
        }

        @Override
        public FakeRow getRow(ResultSet set) throws SQLException {
            return new FakeRow(this, set);
        }
    }

    private static final class CapturingTable extends FakeTable {
        private int updateCalls;
        private String lastSQL;
        private int lastColumnCount;

        @Override
        public void update(String SQL, java.util.List<block_party.db.sql.Column> columns) {
            this.updateCalls++;
            this.lastSQL = SQL;
            this.lastColumnCount = columns.size();
        }

        private void reset() {
            this.updateCalls = 0;
            this.lastSQL = null;
            this.lastColumnCount = 0;
        }
    }

    private static final class FakeRow extends Row<Recordable> {
        private static final int NAME = 6;

        private FakeRow(Table table) {
            super(table);
        }

        private FakeRow(Table table, ResultSet set) throws SQLException {
            super(table, set);
        }

        @Override
        public void sync(Recordable entity) {
        }

        @Override
        public void load(Recordable entity) {
        }
    }
}
