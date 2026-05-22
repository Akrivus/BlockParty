package block_party.gametest;

import block_party.BlockParty;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.blocks.entity.PaperLanternBlockEntity;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.blocks.entity.WindChimesBlockEntity;
import block_party.db.BlockPartyDB;
import block_party.registry.CustomBlocks;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class BlockEntityGameTests {
    private BlockEntityGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dataBlockEntityRegistryIdsExist(GameTestHelper helper) {
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "garden_lantern");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "hanging_scroll");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "paper_lantern");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "sakura_sapling");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "shimenawa");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "shrine_tablet");
        assertRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "wind_chimes");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dataBlocksCreateExpectedBlockEntities(GameTestHelper helper) {
        assertBlockEntity(helper, new BlockPos(1, 1, 1), CustomBlocks.GARDEN_LANTERN.get(), GardenLanternBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(2, 1, 1), CustomBlocks.PAPER_LANTERN.get(), PaperLanternBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(3, 1, 1), CustomBlocks.BLANK_HANGING_SCROLL.get(), HangingScrollBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(4, 1, 1), CustomBlocks.SAKURA_SAPLING.get(), SakuraSaplingBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(5, 1, 1), CustomBlocks.SHIMENAWA.get(), ShimenawaBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(6, 1, 1), CustomBlocks.SHRINE_TABLET.get(), ShrineTabletBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(7, 1, 1), CustomBlocks.WIND_CHIMES.get(), WindChimesBlockEntity.class);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dataBlockEntityNbtRoundTripsOwnerAndRowIdentity(GameTestHelper helper) {
        HolderLookup.Provider provider = helper.getLevel().registryAccess();
        UUID owner = new UUID(1401L, 2401L);
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        ShrineTabletBlockEntity original = new ShrineTabletBlockEntity(pos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState());
        original.setDatabaseID(98765L);
        original.markClaimed(owner);

        CompoundTag tag = original.saveWithoutMetadata(provider);
        ShrineTabletBlockEntity loaded = new ShrineTabletBlockEntity(pos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState());
        loaded.loadCustomOnly(tag, provider);

        assertLong(helper, 98765L, loaded.getDatabaseID(), "DatabaseID");
        assertEquals(helper, owner, loaded.getPlayerUUID(), "PlayerUUID");
        if (!loaded.hasRow()) {
            helper.fail("Expected HasRow to round-trip");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void sqliteCreatesShrineGardenLocationAndSaplingTables(GameTestHelper helper) {
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
            assertTableExists(helper, db, "Shrines");
            assertTableExists(helper, db, "GardenLanterns");
            assertTableExists(helper, db, "Locations");
            assertTableExists(helper, db, "SakuraSaplings");
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void claimedDataBlockCreatesAndDeletesRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(pos, CustomBlocks.GARDEN_LANTERN.get().defaultBlockState(), 3);
        GardenLanternBlockEntity entity = (GardenLanternBlockEntity) level.getBlockEntity(pos);
        UUID owner = new UUID(1402L, 2402L);
        entity.markClaimed(owner);

        try {
            if (!db.dataBlockRowExists("GardenLanterns", entity.getDatabaseID())) {
                helper.fail("Expected claimed garden lantern to create a row");
                return;
            }
            level.removeBlock(pos, false);
            if (db.dataBlockRowExists("GardenLanterns", entity.getDatabaseID())) {
                helper.fail("Expected removed garden lantern to delete its row");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected garden lantern row checks to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void locativeBlockStoresConditionAndPriority(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(pos, CustomBlocks.PAPER_LANTERN.get().defaultBlockState(), 3);
        PaperLanternBlockEntity entity = (PaperLanternBlockEntity) level.getBlockEntity(pos);
        entity.markClaimed(new UUID(1403L, 2403L));

        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT RequiredCondition, Priority FROM Locations WHERE DatabaseID = ? LIMIT 1;")) {
                statement.setLong(1, entity.getDatabaseID());
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        helper.fail("Expected paper lantern to create a location row");
                        return;
                    }
                    assertEquals(helper, "ALWAYS", result.getString("RequiredCondition"), "RequiredCondition");
                    assertLong(helper, 1L, result.getInt("Priority"), "Priority");
                }
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected location row query to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineListQueryReturnsOnlyOwnerAndDimension(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        UUID owner = new UUID(1404L, 2404L);
        UUID other = new UUID(1405L, 2405L);
        deleteShrinesFor(helper, db, owner, other);
        ShrineTabletBlockEntity owned = claimShrine(helper, level, new BlockPos(1, 1, 1), owner);
        claimShrine(helper, level, new BlockPos(2, 1, 1), other);

        try {
            List<BlockPartyDB.ShrineEntry> shrines = db.listShrines(owner, Level.OVERWORLD);
            if (shrines.size() != 1 || shrines.getFirst().databaseId() != owned.getDatabaseID()) {
                helper.fail("Expected shrine list query to return exactly the owner's shrine");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine list query to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    private static void deleteShrinesFor(GameTestHelper helper, BlockPartyDB db, UUID... owners) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Shrines WHERE PlayerUUID = ?;")) {
                for (UUID owner : owners) {
                    statement.setString(1, owner.toString());
                    statement.executeUpdate();
                }
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine cleanup to succeed: " + exception.getMessage());
        }
    }

    private static ShrineTabletBlockEntity claimShrine(GameTestHelper helper, ServerLevel level, BlockPos relative, UUID owner) {
        BlockPos pos = helper.absolutePos(relative);
        level.setBlock(pos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ShrineTabletBlockEntity shrine = (ShrineTabletBlockEntity) level.getBlockEntity(pos);
        shrine.markClaimed(owner);
        return shrine;
    }

    private static BlockPartyDB configuredDb(GameTestHelper helper) {
        BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
        db.configureDatabase(helper.getLevel().getServer());
        return db;
    }

    private static void assertBlockEntity(GameTestHelper helper, BlockPos relative, Block block, Class<? extends BlockEntity> expected) {
        BlockPos pos = helper.absolutePos(relative);
        helper.getLevel().setBlock(pos, block.defaultBlockState(), 3);
        BlockEntity entity = helper.getLevel().getBlockEntity(pos);
        if (!expected.isInstance(entity)) {
            helper.fail("Expected " + block + " to create " + expected.getSimpleName() + ", got " + entity);
        }
    }

    private static void assertTableExists(GameTestHelper helper, BlockPartyDB db, String table) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1;")) {
            statement.setString(1, table);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    helper.fail("Expected SQLite table " + table + " to exist");
                }
            }
        } finally {
            db.free(connection);
        }
    }

    private static void assertRegistered(GameTestHelper helper, Registry<?> registry, String path) {
        ResourceLocation id = BlockParty.source(path);
        if (!registry.containsKey(id)) {
            helper.fail("Expected registry ID " + id + " in " + registry.key().location());
        }
    }

    private static void assertLong(GameTestHelper helper, long expected, long actual, String label) {
        if (expected != actual) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
