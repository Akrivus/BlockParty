package block_party.gametest;

import block_party.BlockParty;
import block_party.db.DimBlockPos;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.data.HidingSpots;
import block_party.entities.goals.HideUntil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeHideGameTests {
    private MoeHideGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 120)
    public static void leftClickHideSceneHidesAndTimedRevealRestoresMoe(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.EAST);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        BlockPos[] hidePos = new BlockPos[1];
        ServerPlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        helper.runAfterDelay(2, () -> {
            hidePos[0] = moe.blockPosition();
            moe.hurt(helper.getLevel().damageSources().playerAttack(player), 1.0F);
        });

        helper.runAfterDelay(5, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.DIRT, hidePos[0]);
            GameTestSupport.assertNoMoesNearAbsolute(helper, hidePos[0]);
            MoeInHiding hidden = GameTestSupport.getSingleHiddenMoeNearAbsolute(helper, hidePos[0]);
            if (!hidePos[0].equals(hidden.getAttachPos())) {
                helper.fail("Expected hidden Moe to attach to " + hidePos[0] + ", got " + hidden.getAttachPos());
            }
        });

        helper.runAfterDelay(35, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.AIR, hidePos[0]);
            Moe revealed = GameTestSupport.getSingleMoeNearAbsolute(helper, hidePos[0]);
            if (!sourceState.equals(revealed.getActualBlockState())) {
                helper.fail("Expected timed reveal to preserve " + sourceState + ", got " + revealed.getActualBlockState());
            }
            GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, hidePos[0]);
            helper.succeed();
        });
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 120)
    public static void hiddenSpotDisturbanceRevealsMoe(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.EAST);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        BlockPos hidePos = moe.blockPosition();
        moe.hide(HideUntil.EXPOSED);

        helper.runAfterDelay(2, () -> {
            GameTestSupport.assertBlockAt(helper, Blocks.DIRT, hidePos);
            GameTestSupport.assertNoMoesNearAbsolute(helper, hidePos);
            GameTestSupport.getSingleHiddenMoeNearAbsolute(helper, hidePos);

            boolean revealed = HidingSpots.spawn(
                    helper.getLevel(),
                    new DimBlockPos(helper.getLevel().dimension(), hidePos)
            );
            if (!revealed) {
                helper.fail("Expected HidingSpots disturbance spawn to reveal the hidden Moe");
            }

            GameTestSupport.assertBlockAt(helper, Blocks.AIR, hidePos);
            Moe disturbed = GameTestSupport.getSingleMoeNearAbsolute(helper, hidePos);
            if (!sourceState.equals(disturbed.getActualBlockState())) {
                helper.fail("Expected disturbed reveal to preserve " + sourceState + ", got " + disturbed.getActualBlockState());
            }
            GameTestSupport.assertNoHiddenMoesNearAbsolute(helper, hidePos);
            helper.succeed();
        });
    }
}
