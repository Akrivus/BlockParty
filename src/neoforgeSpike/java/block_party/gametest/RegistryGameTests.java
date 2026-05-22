package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.CustomCreativeTabs;
import block_party.registry.SceneActions;
import block_party.registry.SceneFilters;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
        assertRegistered(helper, BuiltInRegistries.CREATIVE_MODE_TAB, "block_party");
        assertRegistered(helper, SceneActions.REGISTRY, "send_dialogue");
        assertRegistered(helper, SceneFilters.REGISTRY, "always");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void creativeTabContainsManualReviewItems(GameTestHelper helper) {
        List<ItemStack> stacks = CustomCreativeTabs.reviewStacks();
        assertTabContains(helper, stacks, "moe_spawn_egg");
        assertTabContains(helper, stacks, "cell_phone");
        assertTabContains(helper, stacks, "yearbook");
        assertTabContains(helper, stacks, "shrine_tablet");
        assertTabContains(helper, stacks, "garden_lantern");
        assertTabContains(helper, stacks, "sakura_sapling");
        assertTabContains(helper, stacks, "sakura_log");
        assertTabContains(helper, stacks, "sakura_blossoms");
        assertTabContains(helper, stacks, "wisteria_vines");
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, Registry<?> registry, String path) {
        ResourceLocation id = BlockParty.source(path);
        if (!registry.containsKey(id)) {
            helper.fail("Expected registry ID " + id + " in " + registry.key().location());
        }
    }

    private static void assertTabContains(GameTestHelper helper, List<ItemStack> stacks, String path) {
        Item item = BuiltInRegistries.ITEM.getValue(BlockParty.source(path));
        if (item == null || stacks.stream().noneMatch(stack -> stack.is(item))) {
            helper.fail("Expected Block Party creative tab to contain " + BlockParty.source(path));
        }
    }
}
