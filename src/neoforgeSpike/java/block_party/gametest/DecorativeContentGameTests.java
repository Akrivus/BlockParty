package block_party.gametest;

import block_party.BlockParty;
import block_party.blocks.SakuraBlossomsBlock;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomWorldGen;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class DecorativeContentGameTests {
    private DecorativeContentGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void legacyRecipesLoadAfterNeoForgePathMigration(GameTestHelper helper) {
        assertRecipeLoaded(helper, "sakura_planks");
        assertRecipeLoaded(helper, "shoji_lamp");
        assertRecipeLoaded(helper, "yearbook");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void legacyLootAndWorldgenResourcesAreAvailable(GameTestHelper helper) {
        assertResource(helper, "loot_table/blocks/sakura_log.json");
        assertResource(helper, "loot_table/blocks/sakura_slab.json");
        assertResource(helper, "worldgen/configured_feature/sakura_tree.json");
        assertResource(helper, "worldgen/placed_feature/sakura_tree.json");

        var configured = helper.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        if (configured.get(CustomWorldGen.SAKURA_TREE).isEmpty()) {
            helper.fail("Expected configured feature " + CustomWorldGen.SAKURA_TREE.location() + " to load");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void decorativeBlocksExposeLegacyStateProperties(GameTestHelper helper) {
        BlockState blossoms = CustomBlocks.SAKURA_BLOSSOMS.get().defaultBlockState();
        assertEquals(helper, true, blossoms.getValue(SakuraBlossomsBlock.BLOOMING), "sakura blossoms blooming default");
        assertEquals(helper, LeavesBlock.DECAY_DISTANCE, blossoms.getValue(LeavesBlock.DISTANCE), "sakura blossoms distance");

        BlockState sapling = CustomBlocks.SAKURA_SAPLING.get().defaultBlockState();
        assertEquals(helper, 0, sapling.getValue(SaplingBlock.STAGE), "sakura sapling stage default");

        BlockState slab = CustomBlocks.SAKURA_SLAB.get().defaultBlockState();
        assertEquals(helper, SlabType.BOTTOM, slab.getValue(SlabBlock.TYPE), "sakura slab default type");

        BlockState screen = CustomBlocks.SHOJI_SCREEN.get().defaultBlockState();
        assertEquals(helper, DoubleBlockHalf.LOWER, screen.getValue(net.minecraft.world.level.block.DoorBlock.HALF), "shoji screen default half");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wisteriaVinesKeepNonSolidSurvivalBehavior(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos support = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos vinePos = support.below();
        level.setBlock(support, CustomBlocks.WISTERIA_LEAVES.get().defaultBlockState(), 3);
        BlockState vine = CustomBlocks.WISTERIA_VINE_BODY.get().defaultBlockState();

        if (!vine.canSurvive(level, vinePos)) {
            helper.fail("Expected wisteria vine to survive below wisteria leaves");
            return;
        }
        level.removeBlock(support, false);
        if (vine.canSurvive(level, vinePos)) {
            helper.fail("Expected wisteria vine not to survive without wisteria support");
            return;
        }
        helper.succeed();
    }

    private static void assertRecipeLoaded(GameTestHelper helper, String path) {
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, BlockParty.source(path));
        if (helper.getLevel().getServer().getRecipeManager().byKey(key).isEmpty()) {
            helper.fail("Expected recipe " + key.location() + " to load");
        }
    }

    private static void assertResource(GameTestHelper helper, String path) {
        Optional<net.minecraft.server.packs.resources.Resource> resource = helper.getLevel()
                .getServer()
                .getServerResources()
                .resourceManager()
                .getResource(BlockParty.source(path));
        if (resource.isEmpty()) {
            helper.fail("Expected generated resource block_party:" + path + " to be available");
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
