package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.messages.CNPCTeleport;
import block_party.world.chunk.ForcedChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class CellPhoneGameTests {
    private CellPhoneGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void cellPhoneFindsKnownMoeAndTeleportsIt(GameTestHelper helper) {
        ServerPlayer player = positionedPlayer(helper);
        Moe moe = spawnKnownMoe(helper, player);
        Vec3 expected = expectedCallPosition(player);

        new CNPCTeleport(moe.getDatabaseID()).handle(null, player);

        Moe called = getCalledMoe(helper, expected, moe.getDatabaseID());
        if (called.distanceToSqr(expected) > 0.25D) {
            helper.fail("Expected Cell Phone to teleport known Moe near " + expected + ", got " + called.position());
        }

        helper.succeed();
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void cellPhoneMissingAndDeadMoeFailSafely(GameTestHelper helper) {
        ServerPlayer player = positionedPlayer(helper);
        Moe moe = spawnKnownMoe(helper, player);
        long id = moe.getDatabaseID();
        Vec3 original = moe.position();

        moe.getRow().update((row) -> row.get(NPC.DEAD).set(true));
        new CNPCTeleport(id).handle(null, player);

        if (!moe.position().equals(original)) {
            helper.fail("Expected dead Moe call to leave Moe in place");
        }
        if (moe.isFollowing()) {
            helper.fail("Expected dead Moe call not to set following");
        }
        if (ForcedChunk.get(id) != null) {
            helper.fail("Expected forced chunk to be released after dead Moe call");
        }

        long missingId = id + 1L;
        while (BlockPartyDB.NPCs.find(missingId) != null) {
            missingId++;
        }
        new CNPCTeleport(missingId).handle(null, player);
        if (ForcedChunk.get(missingId) != null) {
            helper.fail("Expected missing Moe call not to leave a forced chunk");
        }

        helper.succeed();
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void cellPhoneSuccessfulCallSetsFollowing(GameTestHelper helper) {
        ServerPlayer player = positionedPlayer(helper);
        Moe moe = spawnKnownMoe(helper, player);
        Vec3 expected = expectedCallPosition(player);

        new CNPCTeleport(moe.getDatabaseID()).handle(null, player);

        Moe called = getCalledMoe(helper, expected, moe.getDatabaseID());
        if (!called.isFollowing()) {
            helper.fail("Expected successful Cell Phone call to set following");
        }

        helper.succeed();
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void cellPhoneSuccessfulCallReleasesForcedChunk(GameTestHelper helper) {
        ServerPlayer player = positionedPlayer(helper);
        Moe moe = spawnKnownMoe(helper, player);
        long id = moe.getDatabaseID();

        ForcedChunk.queue(id, helper.getLevel(), new ChunkPos(moe.blockPosition()));
        if (ForcedChunk.get(id) == null) {
            helper.fail("Expected setup to queue a forced chunk for Cell Phone lookup");
        }

        new CNPCTeleport(id).handle(null, player);

        if (ForcedChunk.get(id) != null) {
            helper.fail("Expected successful Cell Phone call to release forced chunk");
        }

        helper.succeed();
    }

    private static Moe spawnKnownMoe(GameTestHelper helper, ServerPlayer player) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();

        helper.setBlock(source, Blocks.DIRT.defaultBlockState());
        GameTestSupport.useMoeSpawnEgg(helper, player, source, Direction.EAST);
        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        moe.setFollowing(false);
        moe.getRow().update(moe);
        return moe;
    }

    private static ServerPlayer positionedPlayer(GameTestHelper helper) {
        ServerPlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        BlockPos pos = helper.absolutePos(new BlockPos(6, 1, 1));
        player.absMoveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        return player;
    }

    private static Vec3 expectedCallPosition(ServerPlayer player) {
        return new Vec3(player.getX(), player.getY(), player.getZ() + 1.44D);
    }

    private static Moe getCalledMoe(GameTestHelper helper, Vec3 pos, long id) {
        List<Moe> moes = helper.getLevel().getEntitiesOfClass(Moe.class, new AABB(pos, pos).inflate(1.0D));
        for (Moe moe : moes) {
            if (moe.getDatabaseID() == id) {
                return moe;
            }
        }
        helper.fail("Expected called Moe " + id + " near " + pos + ", got " + moes.size() + " Moes");
        throw new IllegalStateException("GameTestHelper.fail should throw");
    }
}
