package block_party.gametest;

import block_party.BlockParty;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.blocks.entity.PaperLanternBlockEntity;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.db.records.Garden;
import block_party.db.records.Location;
import block_party.db.records.NPC;
import block_party.db.records.Sapling;
import block_party.db.records.Shrine;
import block_party.entities.Moe;
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
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dataBlocksCreateExpectedBlockEntities(GameTestHelper helper) {
        assertBlockEntity(helper, new BlockPos(1, 1, 1), CustomBlocks.GARDEN_LANTERN.get(), GardenLanternBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(2, 1, 1), CustomBlocks.PAPER_LANTERN.get(), PaperLanternBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(3, 1, 1), CustomBlocks.BLANK_HANGING_SCROLL.get(), HangingScrollBlockEntity.class);
        assertBlockEntity(helper, new BlockPos(4, 1, 1), CustomBlocks.SAKURA_SAPLING.get(), SakuraSaplingBlockEntity.class);
        placeSupport(helper, new BlockPos(5, 2, 1));
        assertBlockEntity(helper, new BlockPos(5, 1, 1), CustomBlocks.SHIMENAWA.get(), ShimenawaBlockEntity.class);
        placeSupport(helper, new BlockPos(6, 1, 2));
        assertBlockEntity(helper, new BlockPos(6, 1, 1), CustomBlocks.SHRINE_TABLET.get(), ShrineTabletBlockEntity.class);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void windChimesRemainDisabledUntilModelReturns(GameTestHelper helper) {
        assertNotRegistered(helper, BuiltInRegistries.BLOCK, "wind_chimes");
        assertNotRegistered(helper, BuiltInRegistries.ITEM, "wind_chimes");
        assertNotRegistered(helper, BuiltInRegistries.BLOCK_ENTITY_TYPE, "wind_chimes");
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
            assertTableExists(helper, db, "Saplings");
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
    public static void shrineListQueryUsesForgeOwnerOrDimensionRules(GameTestHelper helper) {
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
        deleteAllShrines(helper, db);
        ShrineTabletBlockEntity owned = claimShrine(helper, level, new BlockPos(1, 1, 1), owner);
        ShrineTabletBlockEntity sameDimension = claimShrine(helper, level, new BlockPos(2, 1, 1), other);

        try {
            List<BlockPartyDB.ShrineEntry> shrines = db.listShrines(owner, Level.OVERWORLD);
            List<Long> ids = shrines.stream().map(BlockPartyDB.ShrineEntry::databaseId).toList();
            if (shrines.size() != 2 || !ids.contains(owned.getDatabaseID()) || !ids.contains(sameDimension.getDatabaseID())) {
                helper.fail("Expected shrine list query to return owner shrines or same-dimension shrines");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine list query to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void typedDataBlockQueriesExposeWaypointRows(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        UUID owner = new UUID(1406L, 2406L);
        GardenLanternBlockEntity garden = claimGarden(helper, level, new BlockPos(1, 1, 1), owner);
        SakuraSaplingBlockEntity sapling = claimSapling(helper, level, new BlockPos(2, 1, 1), owner);
        PaperLanternBlockEntity location = claimPaperLantern(helper, level, new BlockPos(3, 1, 1), owner);
        ShrineTabletBlockEntity nearShrine = claimShrine(helper, level, new BlockPos(4, 1, 1), owner);
        ShrineTabletBlockEntity farShrine = claimShrine(helper, level, new BlockPos(8, 1, 1), owner);

        try {
            Garden gardenRow = db.listGardens().stream()
                    .filter(row -> row.databaseId() == garden.getDatabaseID())
                    .findFirst()
                    .orElse(null);
            if (gardenRow == null || !owner.equals(gardenRow.playerUuid()) || !garden.getBlockPos().equals(gardenRow.dimPos().getPos())) {
                helper.fail("Expected typed garden query to expose owner and position");
                return;
            }

            Sapling saplingRow = db.listSaplings().stream()
                    .filter(row -> row.databaseId() == sapling.getDatabaseID())
                    .findFirst()
                    .orElse(null);
            if (saplingRow == null || !owner.equals(saplingRow.playerUuid()) || !sapling.getBlockPos().equals(saplingRow.dimPos().getPos())) {
                helper.fail("Expected typed sapling query to expose owner and position, got " + saplingRow);
                return;
            }

            Location locationRow = db.listLocations(owner).stream()
                    .filter(row -> row.databaseId() == location.getDatabaseID())
                    .findFirst()
                    .orElse(null);
            if (locationRow == null || !"ALWAYS".equals(locationRow.requiredCondition()) || locationRow.priority() != 1) {
                helper.fail("Expected typed location query to expose condition and priority");
                return;
            }

            Shrine closest = db.findClosestShrine(owner, new DimBlockPos(level.dimension(), nearShrine.getBlockPos().west())).orElse(null);
            if (closest == null || closest.databaseId() != nearShrine.getDatabaseID() || closest.databaseId() == farShrine.getDatabaseID()) {
                helper.fail("Expected typed shrine query to return the closest owner shrine");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected typed data block queries to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shimenawaClaimCreatesHiddenNpcRowAndOwnerListEntry(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos pos = helper.absolutePos(relative);
        placeSupport(helper, relative.above());
        level.setBlock(pos, CustomBlocks.SHIMENAWA.get().defaultBlockState(), 3);
        ShimenawaBlockEntity shimenawa = (ShimenawaBlockEntity) level.getBlockEntity(pos);
        shimenawa.getPersistentData().putString("GivenName", "Hidden Friend");
        UUID owner = new UUID(1501L, 2501L);

        shimenawa.claim(mockPlayer(helper, owner));
        shimenawa.claim(mockPlayer(helper, owner));

        NPC row = findNpc(helper, shimenawa.getDatabaseID());
        if (row == null) {
            return;
        }
        assertEquals(helper, owner, row.playerUuid(), "shimenawa row owner");
        assertEquals(helper, true, row.hiding(), "shimenawa row hiding flag");
        assertEquals(helper, pos, row.hiddenPos(), "shimenawa hidden position");
        assertEquals(helper, CustomBlocks.SHIMENAWA.get().defaultBlockState(), row.blockState(), "shimenawa row block state");
        assertEquals(helper, "Hidden Friend", row.name(), "shimenawa persistent name");
        assertEquals(helper, 1, countOwnerListEntries(db, owner, row.databaseId()), "shimenawa owner list entries");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shimenawaRemovalDeletesNpcRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos pos = helper.absolutePos(relative);
        placeSupport(helper, relative.above());
        level.setBlock(pos, CustomBlocks.SHIMENAWA.get().defaultBlockState(), 3);
        ShimenawaBlockEntity shimenawa = (ShimenawaBlockEntity) level.getBlockEntity(pos);
        long id = shimenawa.getDatabaseID();
        shimenawa.claim(mockPlayer(helper, new UUID(1502L, 2502L)));

        level.removeBlock(pos, false);
        try {
            if (db.findNpc(id).isPresent()) {
                helper.fail("Expected removed shimenawa to delete its NPC row");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected shimenawa row lookup to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineTabletSetPlacedByRequiresForgeGatePattern(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(1, 6, 1));
        level.setBlock(pos.south(), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        BlockState state = CustomBlocks.SHRINE_TABLET.get().defaultBlockState();
        level.setBlock(pos, state, 3);
        ShrineTabletBlockEntity shrine = (ShrineTabletBlockEntity) level.getBlockEntity(pos);

        CustomBlocks.SHRINE_TABLET.get().setPlacedBy(level, pos, state, mockPlayer(helper, new UUID(1503L, 2503L)), net.minecraft.world.item.ItemStack.EMPTY);

        if (shrine.hasRow()) {
            helper.fail("Expected incomplete shrine gate pattern not to claim shrine tablet");
            return;
        }
        assertEquals(helper, 0, level.getEntitiesOfClass(Moe.class, new AABB(pos.below(5)).inflate(2.0)).size(), "incomplete shrine spawned Moe count");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineTabletClaimSpawnsLightningAndBellMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        UUID owner = new UUID(1504L, 2504L);
        BlockPos pos = helper.absolutePos(new BlockPos(1, 6, 1));
        level.setBlock(pos.south(), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        level.setBlock(pos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ShrineTabletBlockEntity shrine = (ShrineTabletBlockEntity) level.getBlockEntity(pos);

        shrine.claim(mockPlayer(helper, owner));

        BlockPos spawnPos = pos.below(5);
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(spawnPos).inflate(1.0));
        if (moes.size() != 1) {
            helper.fail("Expected shrine tablet to spawn exactly one Moe, got " + moes.size());
            return;
        }
        Moe moe = moes.getFirst();
        NPC row = findNpc(helper, moe.getDatabaseID());
        if (row == null) {
            return;
        }
        assertEquals(helper, Blocks.BELL.defaultBlockState(), moe.getBlockState(), "shrine spawned Moe block state");
        assertEquals(helper, 1, countOwnerListEntries(db, owner, moe.getDatabaseID()), "shrine owner list entries");
        if (level.getEntitiesOfClass(LightningBolt.class, new AABB(spawnPos).inflate(2.0)).isEmpty()) {
            helper.fail("Expected shrine tablet to spawn visual lightning");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineTabletBellMoeUsesUniquePersonalityRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = configuredDb(helper);
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
            return;
        }

        UUID owner = new UUID(1505L, 2505L);
        BlockPos first = helper.absolutePos(new BlockPos(1, 6, 1));
        BlockPos second = helper.absolutePos(new BlockPos(4, 6, 1));
        level.setBlock(first.south(), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        level.setBlock(second.south(), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        level.setBlock(first, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        level.setBlock(second, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ShrineTabletBlockEntity firstShrine = (ShrineTabletBlockEntity) level.getBlockEntity(first);
        ShrineTabletBlockEntity secondShrine = (ShrineTabletBlockEntity) level.getBlockEntity(second);
        int rowsBefore = countNpcRows(helper);

        firstShrine.claim(mockPlayer(helper, owner));
        List<Long> firstRelationships = db.getFrom(owner);
        if (firstRelationships.size() != 1) {
            helper.fail("Expected first shrine claim to create one Bell relationship, got " + firstRelationships.size());
            return;
        }
        long bellId = firstRelationships.getFirst();
        secondShrine.claim(mockPlayer(helper, owner));

        BlockPos secondSpawn = second.below(5);
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(secondSpawn).inflate(1.0),
                moe -> moe.getDatabaseID() == bellId);
        if (moes.size() != 1) {
            helper.fail("Expected duplicate shrine claims to keep one related Bell Moe at the second shrine, got " + moes.size());
            return;
        }
        Moe bell = moes.getFirst();
        assertEquals(helper, secondSpawn, bell.blockPosition(), "unique shrine Bell position");
        assertUnchangedOrOneNewRow(helper, rowsBefore, countNpcRows(helper), "duplicate shrine Bell claims");
        assertEquals(helper, 1, countOwnerListEntries(db, owner, bell.getDatabaseID()), "unique shrine owner list entries");
        helper.kill(bell);
        helper.succeed();
    }

    private static void deleteAllShrines(GameTestHelper helper, BlockPartyDB db) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Shrines;")) {
                statement.executeUpdate();
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine table cleanup to succeed: " + exception.getMessage());
        }
    }

    private static ShrineTabletBlockEntity claimShrine(GameTestHelper helper, ServerLevel level, BlockPos relative, UUID owner) {
        BlockPos pos = helper.absolutePos(relative);
        level.setBlock(pos.south(), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        level.setBlock(pos, CustomBlocks.SHRINE_TABLET.get().defaultBlockState(), 3);
        ShrineTabletBlockEntity shrine = (ShrineTabletBlockEntity) level.getBlockEntity(pos);
        shrine.markClaimed(owner);
        return shrine;
    }

    private static GardenLanternBlockEntity claimGarden(GameTestHelper helper, ServerLevel level, BlockPos relative, UUID owner) {
        BlockPos pos = helper.absolutePos(relative);
        level.setBlock(pos, CustomBlocks.GARDEN_LANTERN.get().defaultBlockState(), 3);
        GardenLanternBlockEntity garden = (GardenLanternBlockEntity) level.getBlockEntity(pos);
        garden.claim(mockPlayer(helper, owner));
        return garden;
    }

    private static SakuraSaplingBlockEntity claimSapling(GameTestHelper helper, ServerLevel level, BlockPos relative, UUID owner) {
        BlockPos pos = helper.absolutePos(relative);
        level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(pos, CustomBlocks.SAKURA_SAPLING.get().defaultBlockState(), 3);
        SakuraSaplingBlockEntity sapling = (SakuraSaplingBlockEntity) level.getBlockEntity(pos);
        sapling.claim(mockPlayer(helper, owner));
        return sapling;
    }

    private static PaperLanternBlockEntity claimPaperLantern(GameTestHelper helper, ServerLevel level, BlockPos relative, UUID owner) {
        BlockPos pos = helper.absolutePos(relative);
        level.setBlock(pos, CustomBlocks.PAPER_LANTERN.get().defaultBlockState(), 3);
        PaperLanternBlockEntity location = (PaperLanternBlockEntity) level.getBlockEntity(pos);
        location.claim(mockPlayer(helper, owner));
        return location;
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

    private static void placeSupport(GameTestHelper helper, BlockPos relative) {
        helper.getLevel().setBlock(helper.absolutePos(relative), CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
    }

    private static Player mockPlayer(GameTestHelper helper, UUID uuid) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setUUID(uuid);
        return player;
    }

    private static NPC findNpc(GameTestHelper helper, long id) {
        try {
            return BlockPartyDB.get(helper.getLevel()).findNpc(id).orElseGet(() -> {
                helper.fail("Expected NPC row " + id + " to exist");
                return null;
            });
        } catch (SQLException exception) {
            helper.fail("Expected NPC row lookup to succeed: " + exception.getMessage());
            return null;
        }
    }

    private static int countOwnerListEntries(BlockPartyDB db, UUID owner, long databaseId) {
        int count = 0;
        for (long id : db.getFrom(owner)) {
            if (id == databaseId) {
                ++count;
            }
        }
        return count;
    }

    private static int countNpcRows(GameTestHelper helper) {
        BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
        try {
            Connection connection = db.openConnection();
            try (ResultSet result = connection.createStatement().executeQuery("SELECT COUNT(*) FROM NPCs;")) {
                return result.next() ? result.getInt(1) : 0;
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected NPC row count to succeed: " + exception.getMessage());
            return -1;
        }
    }

    private static void assertUnchangedOrOneNewRow(GameTestHelper helper, int before, int after, String label) {
        if (after != before && after != before + 1) {
            helper.fail("Expected " + label + " to reuse an existing row or create one row, started with "
                    + before + " and got " + after);
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

    private static void assertNotRegistered(GameTestHelper helper, Registry<?> registry, String path) {
        ResourceLocation id = BlockParty.source(path);
        if (registry.containsKey(id)) {
            helper.fail("Expected registry ID " + id + " to remain disabled in " + registry.key().location());
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
