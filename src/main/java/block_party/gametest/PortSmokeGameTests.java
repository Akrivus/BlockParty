package block_party.gametest;

import block_party.BlockParty;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class PortSmokeGameTests {
    private PortSmokeGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void emptyBlockPartyPortLoads(GameTestHelper helper) {
        // Keep the shared empty template available for the broader port GameTest suite.
        helper.succeed();
    }
}
