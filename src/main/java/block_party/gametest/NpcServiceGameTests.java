package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class NpcServiceGameTests {
    private NpcServiceGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void spawnAddsNpcIdToOwnerList(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1001L, 2001L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        List<Long> ids = db.listNpcIds(owner);
        if (!ids.contains(moe.getDatabaseID())) {
            helper.fail("Expected owner list to contain spawned NPC ID");
            return;
        }
        if (db.loadOwnedNpc(owner, moe.getDatabaseID()).isEmpty()) {
            helper.fail("Expected owner to load spawned NPC row");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void listReturnsOnlyOwnersNpcs(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1002L, 2002L);
        UUID other = new UUID(1003L, 2003L);
        Moe owned = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        Moe otherOwned = spawnOwnedMoe(helper, level, other, new BlockPos(3, 1, 1));
        if (owned == null || otherOwned == null) {
            return;
        }

        List<Long> ids = db.listNpcIds(owner);
        if (!ids.contains(owned.getDatabaseID())) {
            helper.fail("Expected owner list to include owner's NPC");
            return;
        }
        if (ids.contains(otherOwned.getDatabaseID())) {
            helper.fail("Expected owner list to exclude another player's NPC");
            return;
        }
        helper.kill(owned);
        helper.kill(otherOwned);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void otherPlayersCannotAccessOrRemoveOwnedNpc(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1004L, 2004L);
        UUID other = new UUID(1005L, 2005L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        long id = moe.getDatabaseID();
        if (db.loadOwnedNpc(other, id).isPresent()) {
            helper.fail("Expected non-owner access to be rejected");
            return;
        }
        if (db.removeOwnedNpc(other, id)) {
            helper.fail("Expected non-owner remove to be rejected");
            return;
        }
        if (!db.listNpcIds(owner).contains(id)) {
            helper.fail("Expected rejected remove to keep owner list entry");
            return;
        }
        if (db.removeOwnedNpc(owner, id)) {
            helper.fail("Expected owner remove of living NPC to be rejected");
            return;
        }
        NPC row = db.findNpcSafe(id).orElse(null);
        if (row == null) {
            helper.fail("Expected spawned NPC row before dead remove test");
            return;
        }
        try {
            row.markDead(db);
        } catch (SQLException exception) {
            helper.fail("Expected dead row update to succeed: " + exception.getMessage());
            return;
        }
        if (!db.removeOwnedNpc(owner, id)) {
            helper.fail("Expected owner remove of dead NPC to de-list NPC");
            return;
        }
        if (db.findNpcSafe(id).isEmpty()) {
            helper.fail("Expected de-list to leave SQLite row intact");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void estrangedYearbookPagesRemainVisibleAndRemovable(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1006L, 2006L);
        UUID other = new UUID(1007L, 2007L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        long id = moe.getDatabaseID();
        db.addTo(other, id);
        if (db.loadOwnedNpc(other, id).isPresent()) {
            helper.fail("Expected estranged row to remain inaccessible as an owned living NPC");
            return;
        }
        if (db.loadYearbookNpc(other, id).isEmpty()) {
            helper.fail("Expected estranged row to remain readable as a Yearbook page");
            return;
        }
        if (!db.listYearbookNpcIds(other).contains(id)) {
            helper.fail("Expected estranged row to remain listed in Yearbook pages");
            return;
        }
        if (!db.removeOwnedNpc(other, id)) {
            helper.fail("Expected estranged Yearbook page remove to de-list NPC");
            return;
        }
        if (db.listYearbookNpcIds(other).contains(id)) {
            helper.fail("Expected removed estranged page to leave other player's Yearbook");
            return;
        }
        if (db.findNpcSafe(id).isEmpty()) {
            helper.fail("Expected estranged page remove to leave SQLite row intact");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void deadCorruptAndMissingRowsFailSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1008L, 2008L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        long id = moe.getDatabaseID();
        NPC row = db.findNpcSafe(id).orElse(null);
        if (row == null) {
            helper.fail("Expected spawned NPC row before dead-row test");
            return;
        }
        try {
            row.markDead(db);
        } catch (SQLException exception) {
            helper.fail("Expected dead row update to succeed: " + exception.getMessage());
            return;
        }
        if (db.loadOwnedNpc(owner, id).isPresent()) {
            helper.fail("Expected dead NPC row access to be rejected");
            return;
        }
        if (db.loadYearbookNpc(owner, id).isEmpty()) {
            helper.fail("Expected dead NPC row to remain readable as a Yearbook page");
            return;
        }
        if (!db.listYearbookNpcIds(owner).contains(id)) {
            helper.fail("Expected dead NPC row to remain listed in Yearbook pages");
            return;
        }
        if (db.loadOwnedNpc(owner, Long.MAX_VALUE - 456L).isPresent()) {
            helper.fail("Expected missing NPC row access to be rejected");
            return;
        }

        long corruptId = insertCorruptNpc(helper, db);
        if (corruptId < 0L) {
            return;
        }
        db.addTo(owner, corruptId);
        if (db.loadOwnedNpc(owner, corruptId).isPresent()) {
            helper.fail("Expected corrupt NPC row access to be rejected");
            return;
        }
        if (db.listNpcIds(owner).contains(corruptId)) {
            helper.fail("Expected corrupt NPC row to be filtered from owner list");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hideRevealDoesNotDuplicateOwnerListEntries(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1007L, 2007L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        long id = moe.getDatabaseID();
        MoeInHiding hidden = moe.hide(HideUntil.EXPOSED);
        if (hidden == null) {
            helper.fail("Expected hide to succeed");
            return;
        }
        Moe revealed = HidingSpots.reveal(level, hidden.getAttachPos());
        if (revealed == null) {
            helper.fail("Expected reveal to succeed");
            return;
        }

        int count = 0;
        for (long listed : db.listNpcIds(owner)) {
            if (listed == id) {
                ++count;
            }
        }
        if (count != 1) {
            helper.fail("Expected exactly one owner list entry after hide/reveal, got " + count);
            return;
        }
        helper.kill(revealed);
        helper.succeed();
    }

    private static Moe spawnOwnedMoe(GameTestHelper helper, ServerLevel level, UUID owner, BlockPos relativeSource) {
        BlockPos source = helper.absolutePos(relativeSource);
        BlockState state = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, state, 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected owned Moe spawn to succeed");
        }
        return moe;
    }

    private static long insertCorruptNpc(GameTestHelper helper, BlockPartyDB db) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO NPCs (PlayerUUID, Name, BlockState, Gender)
                    VALUES ('not-a-uuid', 'Corrupt', 0, 'female');
                    """, Statement.RETURN_GENERATED_KEYS)) {
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        helper.fail("Expected corrupt row insert to return an ID");
                        return -1L;
                    }
                    return keys.getLong(1);
                }
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected corrupt row setup to succeed: " + exception.getMessage());
            return -1L;
        }
    }
}
