package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class BlockPlacementGameTests {
    private BlockPlacementGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void shojiBlockCanBePlaced(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);

        helper.setBlock(pos, CustomBlocks.SHOJI_BLOCK.get());
        helper.assertBlockPresent(CustomBlocks.SHOJI_BLOCK.get(), pos);
        helper.succeed();
    }
}
