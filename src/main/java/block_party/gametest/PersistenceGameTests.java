package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.entities.data.HidingSpots;
import block_party.scene.SceneVariables;
import block_party.scene.data.Locations;
import block_party.scene.Speaker;
import block_party.db.voicemail.Voicemails;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class PersistenceGameTests {
    private PersistenceGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void savedDataInstancesCanLoadStore(GameTestHelper helper) {
        HolderLookup.Provider provider = helper.getLevel().registryAccess();
        UUID playerId = new UUID(0x1234L, 0x5678L);

        BlockPartyDB db = new BlockPartyDB();
        db.addName("Moe");
        db.addTo(playerId, 99L);
        BlockPartyDB loadedDb = BlockPartyDB.load(db.save(new CompoundTag(), provider), provider);
        assertEquals(helper, List.of("Moe"), loadedDb.names(), "BlockPartyDB names");
        assertEquals(helper, List.of(99L), loadedDb.getFrom(playerId), "BlockPartyDB player NPC IDs");

        HidingSpots spots = new HidingSpots();
        spots.put(new BlockPos(3, 4, 5), 77L);
        HidingSpots loadedSpots = HidingSpots.load(spots.save(new CompoundTag(), provider), provider);
        assertLong(helper, 77L, loadedSpots.find(new BlockPos(3, 4, 5)), "HidingSpots ID");

        SceneVariables variables = makeSceneVariables();
        SceneVariables loadedVariables = SceneVariables.load(variables.save(new CompoundTag(), provider), provider);
        assertSceneVariables(helper, loadedVariables);

        Voicemails voicemails = new Voicemails();
        voicemails.add(playerId, 99L, "Call me back, @.name.", true, Speaker.DEFAULT, BlockParty.source("message"), 60L * 60L * 1000L);
        Voicemails loadedVoicemails = Voicemails.load(voicemails.save(new CompoundTag(), provider), provider);
        assertEquals(helper, 1, loadedVoicemails.allForTests().size(), "voicemail count");
        assertEquals(helper, "Call me back, @.name.", loadedVoicemails.allForTests().getFirst().text(), "voicemail text");
        assertEquals(helper, BlockParty.source("message"), loadedVoicemails.allForTests().getFirst().sound(), "voicemail sound");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hidingSpotsDirtyOnlyWhenEntriesChange(GameTestHelper helper) {
        HidingSpots spots = new HidingSpots();
        BlockPos pos = new BlockPos(3, 4, 5);
        spots.remove(pos);
        if (spots.isDirty()) {
            helper.fail("Expected missing HidingSpots remove to stay clean");
            return;
        }
        spots.put(pos, 77L);
        if (!spots.isDirty()) {
            helper.fail("Expected new HidingSpots entry to mark data dirty");
            return;
        }
        spots.setDirty(false);
        spots.put(pos, 77L);
        if (spots.isDirty()) {
            helper.fail("Expected unchanged HidingSpots put to stay clean");
            return;
        }
        spots.remove(pos);
        if (!spots.isDirty()) {
            helper.fail("Expected existing HidingSpots remove to mark data dirty");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void sqliteOpensAndClosesSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MinecraftServer server = level.getServer();
        BlockPartyDB data = BlockPartyDB.get(level);
        data.configureDatabase(server);
        try {
            Connection connection = data.openConnection();
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS block_party_port_smoke (id INTEGER PRIMARY KEY)");
            }
            data.free(connection);
        } catch (SQLException exception) {
            helper.fail("Expected SQLite to open and close safely: " + exception.getMessage());
            return;
        }
        if (data.openConnectionCount() != 0) {
            helper.fail("Expected no tracked SQLite connections after free");
            return;
        }
        BlockPartyDB.shutdown(server);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dimensionDataStorageReturnsPersistenceShells(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        if (BlockPartyDB.get(level) != BlockPartyDB.get(level)) {
            helper.fail("Expected DimensionDataStorage to cache BlockPartyDB");
            return;
        }
        if (HidingSpots.get(level) != HidingSpots.get(level)) {
            helper.fail("Expected DimensionDataStorage to cache HidingSpots");
            return;
        }
        if (SceneVariables.get(level) != SceneVariables.get(level)) {
            helper.fail("Expected DimensionDataStorage to cache SceneVariables");
            return;
        }
        if (Voicemails.get(level) != Voicemails.get(level)) {
            helper.fail("Expected DimensionDataStorage to cache Voicemails");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void voicemailsRevealAfterDelay(GameTestHelper helper) {
        UUID playerId = new UUID(0x9999L, 0x1111L);
        Voicemails voicemails = new Voicemails();
        voicemails.add(playerId, 12L, "Late message", true, Speaker.DEFAULT, null, 1000L);
        Voicemails.Entry entry = voicemails.allForTests().getFirst();

        assertEquals(helper, 0, voicemails.reveal(playerId, entry.createdAt() + 999L), "early voicemail reveal count");
        assertEquals(helper, 0, voicemails.revealed(playerId).size(), "early revealed voicemail count");
        assertEquals(helper, 1, voicemails.reveal(playerId, entry.availableAt()), "ready voicemail reveal count");
        assertEquals(helper, 1, voicemails.revealed(playerId).size(), "ready revealed voicemail count");
        helper.succeed();
    }

    private static SceneVariables makeSceneVariables() {
        SceneVariables variables = new SceneVariables();
        variables.cookies(42L).set("name", "Moe");
        variables.counters(42L).set("health", 20);
        variables.playerCookies(new UUID(0x1234L, 0x5678L)).set("route", "friend");
        variables.playerCounters(new UUID(0x1234L, 0x5678L)).set("visits", 5);
        variables.worldCookies().set("season", "spring");
        variables.worldCounters().set("festival_day", 2);
        variables.locations(42L).set("home", new DimBlockPos(Level.OVERWORLD, new BlockPos(1, 2, 3)));
        variables.targets(42L).set("look_at", 123);
        return variables;
    }

    private static void assertSceneVariables(GameTestHelper helper, SceneVariables variables) {
        assertEquals(helper, "Moe", variables.cookies(42L).get("name"), "SceneVariables cookie");
        assertEquals(helper, 20, variables.counters(42L).get("health"), "SceneVariables counter");
        assertEquals(helper, "friend", variables.playerCookies(new UUID(0x1234L, 0x5678L)).get("route"), "SceneVariables player cookie");
        assertEquals(helper, 5, variables.playerCounters(new UUID(0x1234L, 0x5678L)).get("visits"), "SceneVariables player counter");
        assertEquals(helper, "spring", variables.worldCookies().get("season"), "SceneVariables world cookie");
        assertEquals(helper, 2, variables.worldCounters().get("festival_day"), "SceneVariables world counter");
        Locations locations = variables.locations(42L);
        DimBlockPos home = locations.get("home");
        if (!new BlockPos(1, 2, 3).equals(home.getPos()) || home.isEmpty()) {
            helper.fail("Expected SceneVariables location to round-trip");
            return;
        }
        assertEquals(helper, Level.OVERWORLD, home.getDim(), "SceneVariables location dimension");
        assertEquals(helper, 123, variables.targets(42L).get("look_at"), "SceneVariables target");
    }

    private static void assertLong(GameTestHelper helper, long expected, OptionalLong actual, String label) {
        if (actual.isEmpty() || actual.getAsLong() != expected) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
