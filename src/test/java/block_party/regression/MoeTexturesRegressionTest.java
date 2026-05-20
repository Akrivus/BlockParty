package block_party.regression;

import block_party.regression.RegressionTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

final class MoeTexturesRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testBlockKeyedOverrideMapSelectsMatchingPattern();
        testBlockKeyedOverrideMapFallsBackWhenPatternDoesNotMatch();
        testMissingBlockBucketFallsBack();
    }

    private void testBlockKeyedOverrideMapSelectsMatchingPattern() {
        BlockState actualState = Blocks.NOTE_BLOCK.defaultBlockState().setValue(NoteBlock.NOTE, 5);
        ResourceLocation override = new ResourceLocation("minecraft", "textures/moe/note_block.note5.png");
        ResourceLocation fallback = new ResourceLocation("minecraft", "textures/moe/note_block.png");

        ResourceLocation selected = MoeTextures.getTextureFor(
                Map.of(
                        Blocks.NOTE_BLOCK,
                        Map.of(
                                new MoeTextures.BlockStatePattern(Blocks.NOTE_BLOCK.defaultBlockState(), Map.of(NoteBlock.NOTE, 5)),
                                override
                        )
                ),
                actualState,
                actualState,
                fallback
        );

        assertEquals(override, selected, "Moe texture overrides are selected from a Block-keyed map");
    }

    private void testBlockKeyedOverrideMapFallsBackWhenPatternDoesNotMatch() {
        BlockState visibleState = Blocks.CAKE.defaultBlockState();
        BlockState actualState = Blocks.CAKE.defaultBlockState().setValue(CakeBlock.BITES, 2);
        ResourceLocation override = new ResourceLocation("minecraft", "textures/moe/cake.bites3.png");
        ResourceLocation fallback = new ResourceLocation("minecraft", "textures/moe/cake.png");

        ResourceLocation selected = MoeTextures.getTextureFor(
                Map.of(
                        Blocks.CAKE,
                        Map.of(
                                new MoeTextures.BlockStatePattern(Blocks.CAKE.defaultBlockState(), Map.of(CakeBlock.BITES, 3)),
                                override
                        )
                ),
                visibleState,
                actualState,
                fallback
        );

        assertEquals(fallback, selected, "Moe texture lookup preserves fallback when no pattern matches");
    }

    private void testMissingBlockBucketFallsBack() {
        BlockState state = Blocks.NOTE_BLOCK.defaultBlockState().setValue(NoteBlock.NOTE, 5);
        ResourceLocation fallback = new ResourceLocation("minecraft", "textures/moe/note_block.png");

        ResourceLocation selected = MoeTextures.getTextureFor(
                Map.of(Blocks.CAKE, Map.of()),
                state,
                state,
                fallback
        );

        assertEquals(fallback, selected, "Moe texture lookup preserves fallback when a block has no override bucket");
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
