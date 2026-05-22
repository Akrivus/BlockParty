package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.SceneActions;
import block_party.registry.SceneFilters;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class RegistryGameTests {
    private RegistryGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void representativeRegistryIdsExist(GameTestHelper helper) {
        assertRegistered(helper, BuiltInRegistries.BLOCK, "shoji_block");
        assertRegistered(helper, BuiltInRegistries.ITEM, "moe_spawn_egg");
        assertRegistered(helper, BuiltInRegistries.SOUND_EVENT, "moe.laugh");
        assertRegistered(helper, BuiltInRegistries.PARTICLE_TYPE, "sakura");
        assertRegistered(helper, BuiltInRegistries.ENTITY_TYPE, "moe");
        assertRegistered(helper, SceneActions.REGISTRY, "send_dialogue");
        assertRegistered(helper, SceneFilters.REGISTRY, "always");
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, Registry<?> registry, String path) {
        ResourceLocation id = BlockParty.source(path);
        if (!registry.containsKey(id)) {
            helper.fail("Expected registry ID " + id + " in " + registry.key().location());
        }
    }
}
