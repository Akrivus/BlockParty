package block_party.gametest;

import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.registry.CustomItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

final class GameTestSupport {
    static final String EMPTY_TEMPLATE = "empty";

    private GameTestSupport() {
    }

    static InteractionResult useMoeSpawnEgg(GameTestHelper helper, ServerPlayer player, BlockPos pos, Direction face) {
        long databaseID = helper.absolutePos(pos).asLong();
        NPC existing = BlockPartyDB.NPCs.find(databaseID);
        if (existing != null) {
            existing.delete();
        }

        ItemStack stack = new ItemStack(CustomItems.MOE_SPAWN_EGG.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        BlockPos absolutePos = helper.absolutePos(pos);
        Vec3 hitLocation = Vec3.atCenterOf(absolutePos);
        BlockHitResult hit = new BlockHitResult(hitLocation, face, absolutePos, false);
        return CustomItems.MOE_SPAWN_EGG.get().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
    }

    static Moe getSingleMoeNear(GameTestHelper helper, BlockPos pos) {
        return getSingleMoeNearAbsolute(helper, helper.absolutePos(pos));
    }

    static Moe getSingleMoeNearAbsolute(GameTestHelper helper, BlockPos pos) {
        List<Moe> moes = helper.getLevel().getEntitiesOfClass(Moe.class, boxAround(pos));
        if (moes.size() != 1) {
            helper.fail("Expected exactly one Moe near " + pos + ", got " + moes.size());
        }
        return moes.get(0);
    }

    static MoeInHiding getSingleHiddenMoeNearAbsolute(GameTestHelper helper, BlockPos pos) {
        List<MoeInHiding> moes = helper.getLevel().getEntitiesOfClass(MoeInHiding.class, boxAround(pos));
        if (moes.size() != 1) {
            helper.fail("Expected exactly one hidden Moe marker near " + pos + ", got " + moes.size());
        }
        return moes.get(0);
    }

    static void assertNoMoesNear(GameTestHelper helper, BlockPos pos) {
        assertNoMoesNearAbsolute(helper, helper.absolutePos(pos));
    }

    static void assertNoMoesNearAbsolute(GameTestHelper helper, BlockPos pos) {
        List<Moe> moes = helper.getLevel().getEntitiesOfClass(Moe.class, boxAround(pos));
        if (!moes.isEmpty()) {
            helper.fail("Expected no Moes near " + pos + ", got " + moes.size());
        }
    }

    static void assertNoHiddenMoesNearAbsolute(GameTestHelper helper, BlockPos pos) {
        List<MoeInHiding> moes = helper.getLevel().getEntitiesOfClass(MoeInHiding.class, boxAround(pos));
        if (!moes.isEmpty()) {
            helper.fail("Expected no hidden Moe markers near " + pos + ", got " + moes.size());
        }
    }

    static void assertBlockAt(GameTestHelper helper, Block block, BlockPos pos) {
        Block actual = helper.getLevel().getBlockState(pos).getBlock();
        if (actual != block) {
            helper.fail("Expected " + block.getName().getString() + ", got " + actual.getName().getString() + " at " + pos);
        }
    }

    private static AABB boxAround(BlockPos pos) {
        return new AABB(pos).inflate(1.0D);
    }
}
