package block_party.gametest;

import block_party.BlockParty;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class SpikeGameTests {
    private SpikeGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void emptyBlockPartySpikeLoads(GameTestHelper helper) {
        // TODO NeoForge spike: replace with real parity GameTests as systems are ported back.
        helper.succeed();
    }
}
