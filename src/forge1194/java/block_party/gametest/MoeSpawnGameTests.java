package block_party.gametest;

import block_party.BlockParty;
import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class MoeSpawnGameTests {
    private MoeSpawnGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void moeSpawnEggConsumesValidBlockAndSpawnsMoe(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos spawn = source.east();
        BlockState sourceState = Blocks.DIRT.defaultBlockState();

        helper.setBlock(source, sourceState);
        ServerPlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        InteractionResult result = GameTestSupport.useMoeSpawnEgg(helper, player, source, Direction.EAST);

        if (result != InteractionResult.CONSUME) {
            helper.fail("Expected valid Moe spawn egg use to consume the action, got " + result);
        }
        helper.assertBlockPresent(Blocks.AIR, source);

        Moe moe = GameTestSupport.getSingleMoeNear(helper, spawn);
        if (!sourceState.equals(moe.getActualBlockState())) {
            helper.fail("Expected spawned Moe to preserve " + sourceState + ", got " + moe.getActualBlockState());
        }
        if (!player.getUUID().equals(moe.getPlayerUUID())) {
            helper.fail("Expected spawned Moe to be claimed by the player that used the egg");
        }

        helper.succeed();
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void moeSpawnEggFailsOnInvalidBlock(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);

        helper.setBlock(source, Blocks.TORCH.defaultBlockState());
        InteractionResult result = GameTestSupport.useMoeSpawnEgg(helper, FakePlayerFactory.getMinecraft(helper.getLevel()), source, Direction.UP);

        if (result != InteractionResult.FAIL) {
            helper.fail("Expected invalid Moe spawn egg use to fail, got " + result);
        }
        helper.assertBlockPresent(Blocks.TORCH, source);
        GameTestSupport.assertNoMoesNear(helper, source);

        helper.succeed();
    }
}
