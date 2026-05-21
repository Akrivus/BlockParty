package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.CustomBlockEntities;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomEntities;
import block_party.registry.CustomItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class RegistryGameTests {
    private RegistryGameTests() {
    }

    @GameTest(template = GameTestSupport.EMPTY_TEMPLATE, timeoutTicks = 100)
    public static void requiredRegistriesLoad(GameTestHelper helper) {
        assertRegistered(helper, ForgeRegistries.BLOCKS.getKey(CustomBlocks.SHOJI_BLOCK.get()), "block_party:shoji_block");
        assertRegistered(helper, ForgeRegistries.ITEMS.getKey(CustomItems.MOE_SPAWN_EGG.get()), "block_party:moe_spawn_egg");
        assertRegistered(helper, ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.MOE.get()), "block_party:moe");
        assertRegistered(helper, ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(CustomBlockEntities.PAPER_LANTERN.get()), "block_party:paper_lantern");

        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, ResourceLocation actual, String expected) {
        if (!new ResourceLocation(expected).equals(actual)) {
            helper.fail("Expected registry entry " + expected + ", got " + actual);
        }
    }
}
