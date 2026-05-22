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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import java.sql.SQLException;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeLifecycleGameTests {
    private MoeLifecycleGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void validSpawnEggUseCreatesMoeShell(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        UUID owner = new UUID(11L, 22L);
        BlockState sourceState = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, sourceState, 3);

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected valid tagged block to spawn Moe");
            return;
        }
        NPC row = findNpc(helper, moe.getDatabaseID());
        if (row == null) {
            return;
        }
        assertEquals(helper, row.databaseId(), moe.getDatabaseID(), "spawned Moe database ID");
        assertEquals(helper, owner, moe.getOwnerUUID(), "spawned Moe owner UUID");
        assertEquals(helper, owner, row.playerUuid(), "spawned row owner UUID");
        assertEquals(helper, sourceState, moe.getBlockState(), "spawned Moe source block state");
        assertEquals(helper, sourceState, row.blockState(), "spawned row block state");
        assertEquals(helper, helper.absolutePos(new BlockPos(1, 2, 1)), moe.blockPosition(), "spawned Moe position");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void invalidSpawnEggUseFailsSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos source = helper.absolutePos(new BlockPos(1, 1, 1));
        level.setBlock(source, Blocks.AIR.defaultBlockState(), 3);

        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, new UUID(33L, 44L));
        if (moe != null) {
            helper.fail("Expected invalid block to produce no Moe");
            return;
        }
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(source).inflate(3.0));
        if (!moes.isEmpty()) {
            helper.fail("Expected invalid spawn to leave no nearby Moe entities");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hideCreatesBlockHiddenEntityAndHidingSpot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(55L, 66L);
        BlockState sourceState = CustomBlocks.SAKURA_PLANKS.get().defaultBlockState();
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.moveToBlock(pos);
        moe.setOwnerUUID(owner);
        moe.setBlockState(sourceState);
        NPC row = createNpc(helper, level, moe);
        if (row == null) {
            return;
        }
        row.applyTo(moe);
        level.addFreshEntity(moe);

        MoeInHiding hidden = moe.hide(HideUntil.ONE_SECOND_PASSES);
        if (hidden == null) {
            helper.fail("Expected Moe hide to create a MoeInHiding shell");
            return;
        }
        assertEquals(helper, sourceState, level.getBlockState(pos), "hidden block state");
        assertEquals(helper, row.databaseId(), hidden.getDatabaseID(), "hidden database ID");
        assertEquals(helper, owner, hidden.getOwnerUUID(), "hidden owner UUID");
        assertEquals(helper, pos, hidden.getAttachPos(), "hidden attach position");
        assertEquals(helper, HideUntil.ONE_SECOND_PASSES, hidden.getHideUntil(), "hidden HideUntil");
        assertLong(helper, row.databaseId(), HidingSpots.get(level).find(pos), "HidingSpots record");
        NPC updated = findNpc(helper, row.databaseId());
        if (updated == null) {
            return;
        }
        assertEquals(helper, true, updated.hiding(), "hidden row hiding flag");
        assertEquals(helper, pos, updated.hiddenPos(), "hidden row position");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void manualRevealRestoresSameIdentityShell(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        UUID owner = new UUID(77L, 88L);
        BlockState sourceState = CustomBlocks.GINKGO_PLANKS.get().defaultBlockState();
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.moveToBlock(pos);
        moe.setOwnerUUID(owner);
        moe.setBlockState(sourceState);
        NPC row = createNpc(helper, level, moe);
        if (row == null) {
            return;
        }
        row.applyTo(moe);
        level.addFreshEntity(moe);
        MoeInHiding hidden = moe.hide(HideUntil.EXPOSED);
        if (hidden == null) {
            helper.fail("Expected hidden shell before reveal");
            return;
        }

        Moe revealed = HidingSpots.reveal(level, pos);
        if (revealed == null) {
            helper.fail("Expected manual reveal to restore Moe shell");
            return;
        }
        assertEquals(helper, row.databaseId(), revealed.getDatabaseID(), "revealed database ID");
        assertEquals(helper, owner, revealed.getOwnerUUID(), "revealed owner UUID");
        assertEquals(helper, sourceState, revealed.getBlockState(), "revealed block state");
        NPC updated = findNpc(helper, row.databaseId());
        if (updated == null) {
            return;
        }
        assertEquals(helper, false, updated.hiding(), "revealed row hiding flag");
        if (HidingSpots.get(level).find(pos).isPresent()) {
            helper.fail("Expected reveal to clear HidingSpots record");
            return;
        }
        helper.kill(revealed);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingRowRevealFailsSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
        long missingId = Long.MAX_VALUE - 123L;
        level.setBlock(pos, CustomBlocks.GINKGO_PLANKS.get().defaultBlockState(), 3);

        MoeInHiding hidden = new MoeInHiding(block_party.registry.CustomEntities.MOE_IN_HIDING.get(), level);
        hidden.setDatabaseID(missingId);
        hidden.setAttachPos(pos);
        hidden.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(hidden);
        HidingSpots.get(level).put(pos, missingId);

        Moe revealed = HidingSpots.reveal(level, pos);
        if (revealed != null) {
            helper.fail("Expected reveal with missing SQLite row to no-op");
            return;
        }
        if (!hidden.isAlive()) {
            helper.fail("Expected hidden marker to remain when row-backed reveal fails");
            return;
        }
        helper.kill(hidden);
        HidingSpots.get(level).remove(pos);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingHiddenSpotRevealNoOps(GameTestHelper helper) {
        Moe revealed = HidingSpots.reveal(helper.getLevel(), helper.absolutePos(new BlockPos(4, 1, 4)));
        if (revealed != null) {
            helper.fail("Expected missing hidden spot reveal to no-op");
            return;
        }
        helper.succeed();
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

    private static NPC createNpc(GameTestHelper helper, ServerLevel level, Moe moe) {
        try {
            return BlockPartyDB.get(level).createNpc(level, moe);
        } catch (SQLException exception) {
            helper.fail("Expected NPC row creation to succeed: " + exception.getMessage());
            return null;
        }
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
}
