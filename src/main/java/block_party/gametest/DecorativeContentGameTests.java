package block_party.gametest;

import block_party.BlockParty;
import block_party.blocks.GardenLanternBlock;
import block_party.blocks.GinkgoLeavesBlock;
import block_party.blocks.HangingScrollBlock;
import block_party.blocks.SakuraBlossomsBlock;
import block_party.blocks.ShimenawaBlock;
import block_party.blocks.ShojiLanternBlock;
import block_party.blocks.ShojiScreenBlock;
import block_party.blocks.ShrineTabletBlock;
import block_party.blocks.WritingTableBlock;
import block_party.blocks.WisteriaLeavesBlock;
import block_party.blocks.WisteriaVineBodyBlock;
import block_party.blocks.WisteriaVineTipBlock;
import block_party.registry.CustomBlocks;
import block_party.registry.CustomWorldGen;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
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
    public static void decorativeBlocksUseForgeClassesAndDefaultStates(GameTestHelper helper) {
        assertEquals(helper, true, CustomBlocks.GARDEN_LANTERN.get() instanceof GardenLanternBlock, "garden lantern block class");
        assertEquals(helper, false, CustomBlocks.GARDEN_LANTERN.get().defaultBlockState().getValue(GardenLanternBlock.LIT), "garden lantern lit default");

        assertEquals(helper, true, CustomBlocks.BLANK_HANGING_SCROLL.get() instanceof HangingScrollBlock, "hanging scroll block class");
        assertEquals(helper, Direction.NORTH, CustomBlocks.BLANK_HANGING_SCROLL.get().defaultBlockState().getValue(HangingScrollBlock.FACING), "hanging scroll default facing");

        assertEquals(helper, true, CustomBlocks.SHIMENAWA.get() instanceof ShimenawaBlock, "shimenawa block class");
        BlockState shimenawa = CustomBlocks.SHIMENAWA.get().defaultBlockState();
        assertEquals(helper, Direction.NORTH, shimenawa.getValue(ShimenawaBlock.FACING), "shimenawa default facing");
        assertEquals(helper, true, shimenawa.getValue(ShimenawaBlock.HANGING), "shimenawa hanging default");

        assertEquals(helper, true, CustomBlocks.SHRINE_TABLET.get() instanceof ShrineTabletBlock, "shrine tablet block class");
        assertEquals(helper, Direction.NORTH, CustomBlocks.SHRINE_TABLET.get().defaultBlockState().getValue(ShrineTabletBlock.FACING), "shrine tablet default facing");

        assertEquals(helper, true, CustomBlocks.SHOJI_SCREEN.get() instanceof ShojiScreenBlock, "shoji screen block class");
        assertEquals(helper, true, CustomBlocks.SHOJI_LANTERN.get() instanceof ShojiLanternBlock, "shoji lantern block class");
        assertEquals(helper, true, CustomBlocks.WRITING_TABLE.get() instanceof WritingTableBlock, "writing table block class");
        assertEquals(helper, Direction.NORTH, CustomBlocks.WRITING_TABLE.get().defaultBlockState().getValue(WritingTableBlock.FACING), "writing table default facing");
        assertEquals(helper, true, CustomBlocks.GINKGO_LEAVES.get() instanceof GinkgoLeavesBlock, "ginkgo leaves block class");
        assertEquals(helper, true, CustomBlocks.WISTERIA_LEAVES.get() instanceof WisteriaLeavesBlock, "wisteria leaves block class");
        assertEquals(helper, true, CustomBlocks.WISTERIA_VINE_BODY.get() instanceof WisteriaVineBodyBlock, "wisteria vine body class");
        assertEquals(helper, true, CustomBlocks.WISTERIA_VINE_TIP.get() instanceof WisteriaVineTipBlock, "wisteria vine tip class");
        assertEquals(helper, true, CustomBlocks.WISTERIA_VINE_BODY.get() instanceof GrowingPlantBodyBlock, "wisteria vine body growing plant");
        assertEquals(helper, true, CustomBlocks.WISTERIA_VINE_TIP.get() instanceof GrowingPlantHeadBlock, "wisteria vine tip growing plant");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void decorativeBlocksExposeForgeVoxelShapes(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);

        assertBounds(helper, box(3, 0, 3, 13, 16, 13),
                CustomBlocks.PAPER_LANTERN.get().defaultBlockState().getShape(level, origin).bounds(),
                "paper lantern shape");
        assertBounds(helper, box(1, 0, 1, 15, 16, 15),
                CustomBlocks.GARDEN_LANTERN.get().defaultBlockState().getShape(level, origin).bounds(),
                "garden lantern shape");
        assertEquals(helper, Shapes.block(),
                CustomBlocks.SHOJI_LANTERN.get().defaultBlockState().getShape(level, origin),
                "shoji lantern shape");

        assertBounds(helper, box(1, 1, 0, 15, 15, 2),
                CustomBlocks.BLANK_HANGING_SCROLL.get().defaultBlockState()
                        .setValue(HangingScrollBlock.FACING, Direction.NORTH)
                        .getShape(level, origin)
                        .bounds(),
                "hanging scroll north shape");
        assertBounds(helper, box(2, 0, 10, 14, 14, 16),
                CustomBlocks.SHRINE_TABLET.get().defaultBlockState()
                        .setValue(ShrineTabletBlock.FACING, Direction.NORTH)
                        .getShape(level, origin)
                        .bounds(),
                "shrine tablet north shape");
        assertBounds(helper, box(0, 12, 6, 16, 16, 10),
                CustomBlocks.SHIMENAWA.get().defaultBlockState()
                        .setValue(ShimenawaBlock.FACING, Direction.NORTH)
                        .getShape(level, origin)
                        .bounds(),
                "hanging shimenawa north shape");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hangingScrollAndShojiScreenKeepNonSolidBehavior(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        BlockState scroll = CustomBlocks.BLANK_HANGING_SCROLL.get().defaultBlockState();
        if (!scroll.getCollisionShape(level, origin, CollisionContext.empty()).isEmpty()) {
            helper.fail("Expected hanging scroll collision shape to be empty");
            return;
        }

        BlockState openScreen = CustomBlocks.SHOJI_SCREEN.get().defaultBlockState().setValue(ShojiScreenBlock.OPEN, true);
        if (!openScreen.getCollisionShape(level, origin, CollisionContext.empty()).isEmpty()) {
            helper.fail("Expected open shoji screen collision shape to be empty");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void pottedSaplingsUseFlowerPotBehavior(GameTestHelper helper) {
        assertPotted(helper, "potted_ginkgo_sapling", "ginkgo_sapling");
        assertPotted(helper, "potted_sakura_sapling", "sakura_sapling");
        assertPotted(helper, "potted_white_sakura_sapling", "white_sakura_sapling");
        assertPotted(helper, "potted_wisteria_sapling", "wisteria_sapling");
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

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wisteriaLeavesGrowVineTipsBelow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos leavesPos = helper.absolutePos(new BlockPos(1, 3, 1));
        BlockPos vinePos = leavesPos.below();
        level.setBlock(leavesPos, CustomBlocks.WISTERIA_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);

        if (!WisteriaLeavesBlock.growVineBelow(level, leavesPos)) {
            helper.fail("Expected wisteria leaves to place vine tip below");
            return;
        }
        assertEquals(helper, CustomBlocks.WISTERIA_VINE_TIP.get(), level.getBlockState(vinePos).getBlock(), "wisteria grown vine tip");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wisteriaVineTipBonemealGrowsDownwardBody(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos leavesPos = helper.absolutePos(new BlockPos(1, 4, 1));
        BlockPos tipPos = leavesPos.below();
        BlockPos grownPos = tipPos.below();
        BlockState tip = CustomBlocks.WISTERIA_VINE_TIP.get().defaultBlockState();
        level.setBlock(leavesPos, CustomBlocks.WISTERIA_LEAVES.get().defaultBlockState(), 3);
        level.setBlock(tipPos, tip, 3);

        ((BonemealableBlock) CustomBlocks.WISTERIA_VINE_TIP.get()).performBonemeal(level, level.random, tipPos, tip);

        if (level.getBlockState(grownPos).isAir()) {
            helper.fail("Expected wisteria vine tip bonemeal to grow downward");
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

    private static void assertPotted(GameTestHelper helper, String potId, String plantId) {
        if (!(CustomBlocks.ENTRIES.get(potId).get() instanceof FlowerPotBlock pot)) {
            helper.fail("Expected " + potId + " to be a FlowerPotBlock");
            return;
        }
        assertEquals(helper, CustomBlocks.ENTRIES.get(plantId).get(), pot.getPotted(), potId + " potted plant");
    }

    private static AABB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AABB(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
    }

    private static void assertBounds(GameTestHelper helper, AABB expected, AABB actual, String label) {
        double epsilon = 1.0E-7D;
        if (Math.abs(expected.minX - actual.minX) > epsilon
                || Math.abs(expected.minY - actual.minY) > epsilon
                || Math.abs(expected.minZ - actual.minZ) > epsilon
                || Math.abs(expected.maxX - actual.maxX) > epsilon
                || Math.abs(expected.maxY - actual.maxY) > epsilon
                || Math.abs(expected.maxZ - actual.maxZ) > epsilon) {
            helper.fail("Expected " + label + " bounds to be " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(GameTestHelper helper, Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            helper.fail("Expected " + label + " to be " + expected + ", got " + actual);
        }
    }
}
