package block_party.regression;

import block_party.registry.resources.BlockAliases;
import block_party.registry.resources.MoeSounds;
import block_party.registry.resources.MoeTextures;
import block_party.utils.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.function.Supplier;

import static block_party.regression.TestSupport.assertEquals;
import static block_party.regression.TestSupport.assertNotNull;
import static block_party.regression.TestSupport.assertNull;
import static block_party.regression.TestSupport.assertTrue;
import static block_party.regression.TestSupport.getField;

final class JsonUtilsRegistryLookupRegressionTest implements RegressionTest {
    @Override
    public void run() {
        testJsonUtilsResolvesValidBlockId();
        testJsonUtilsResolvesValidSoundEventIdUsedByMoeSounds();
        testJsonUtilsResolvesValidItemIdUsedBySceneObservations();
        testJsonUtilsResolvesValidEntityTypeIdUsedBySceneObservations();
        testJsonUtilsFailsSafelyForInvalidIds();
        testBlockAliasesParsesMinimalValidFixture();
        testMoeSoundsParsesMinimalValidFixture();
        testMoeTexturesParsesMinimalValidFixture();
    }

    private void testJsonUtilsResolvesValidBlockId() {
        Block block = JsonUtils.getAs(JsonUtils.BLOCK, "minecraft:stone");

        assertEquals(Blocks.STONE, block, "JsonUtils resolves valid block IDs");
    }

    private void testJsonUtilsResolvesValidSoundEventIdUsedByMoeSounds() {
        SoundEvent sound = JsonUtils.getAs(JsonUtils.SOUND_EVENT, "minecraft:block.note_block.bell");

        assertEquals(SoundEvents.NOTE_BLOCK_BELL.value(), sound, "JsonUtils resolves valid MoeSounds override sound IDs");
    }

    private void testJsonUtilsResolvesValidItemIdUsedBySceneObservations() {
        Item item = JsonUtils.getAs(JsonUtils.ITEM, "minecraft:cookie");

        assertEquals(Items.COOKIE, item, "JsonUtils resolves valid scene item observation IDs");
    }

    private void testJsonUtilsResolvesValidEntityTypeIdUsedBySceneObservations() {
        EntityType<?> type = JsonUtils.getAs(JsonUtils.ENTITY_TYPE, "minecraft:player");

        assertEquals(EntityType.PLAYER, type, "JsonUtils resolves valid scene entity observation IDs");
    }

    private void testJsonUtilsFailsSafelyForInvalidIds() {
        Block block = JsonUtils.getAs(JsonUtils.BLOCK, "block_party:not_a_real_block");
        SoundEvent sound = JsonUtils.getAs(JsonUtils.SOUND_EVENT, "block_party:not_a_real_sound");
        Item item = JsonUtils.getAs(JsonUtils.ITEM, "block_party:not_a_real_item");
        EntityType<?> type = JsonUtils.getAs(JsonUtils.ENTITY_TYPE, "block_party:not_a_real_entity");
        Item malformed = JsonUtils.getAs(JsonUtils.ITEM, "not a valid id");

        assertNull(block, "JsonUtils returns null for invalid block IDs");
        assertNull(sound, "JsonUtils returns null for invalid sound IDs");
        assertNull(item, "JsonUtils returns null for invalid item IDs");
        assertNull(type, "JsonUtils returns null for invalid entity type IDs");
        assertNull(malformed, "JsonUtils returns null for malformed registry IDs");
    }

    private void testBlockAliasesParsesMinimalValidFixture() {
        ExposedBlockAliases aliases = new ExposedBlockAliases();
        aliases.load(Map.of(
                new ResourceLocation("minecraft", "stone"),
                json("{\"aliases\":[\"minecraft:dirt\"]}")
        ));

        Map<Block, Block> map = (Map<Block, Block>) getField(aliases, "map");
        assertEquals(Blocks.STONE, map.get(Blocks.DIRT), "BlockAliases maps a valid alias block to its canonical block");
    }

    private void testMoeSoundsParsesMinimalValidFixture() {
        ExposedMoeSounds sounds = new ExposedMoeSounds();
        sounds.load(Map.of(
                new ResourceLocation("minecraft", "stone"),
                json("{\"angry\":\"minecraft:block.note_block.bell\"}")
        ));

        Map<Block, Map<MoeSounds.Sound, Supplier<SoundEvent>>> map = (Map<Block, Map<MoeSounds.Sound, Supplier<SoundEvent>>>) getField(sounds, "map");
        assertTrue(map.containsKey(Blocks.STONE), "MoeSounds stores overrides for a valid block ID");
        assertEquals(SoundEvents.NOTE_BLOCK_BELL.value(), map.get(Blocks.STONE).get(MoeSounds.Sound.ANGRY).get(), "MoeSounds resolves valid sound override IDs");
    }

    private void testMoeTexturesParsesMinimalValidFixture() {
        ExposedMoeTextures textures = new ExposedMoeTextures();
        textures.load(Map.of(
                new ResourceLocation("block_party", "stone"),
                json("{\"block\":\"minecraft:stone\",\"props\":[],\"texture\":\"block_party:textures/moe/stone.png\"}")
        ));

        Map<Block, Map<MoeTextures.BlockStatePattern, ResourceLocation>> map = (Map<Block, Map<MoeTextures.BlockStatePattern, ResourceLocation>>) getField(textures, "map");
        assertTrue(map.containsKey(Blocks.STONE), "MoeTextures stores overrides for a valid block ID");
        assertNotNull(MoeTextures.getTextureFor(map, Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), new ResourceLocation("block_party", "fallback")), "MoeTextures can resolve a parsed valid fixture");
    }

    private JsonElement json(String json) {
        return JsonParser.parseString(json);
    }

    private static final class ExposedBlockAliases extends BlockAliases {
        void load(Map<ResourceLocation, JsonElement> folder) {
            this.apply(folder, (ResourceManager) null, (ProfilerFiller) null);
        }
    }

    private static final class ExposedMoeSounds extends MoeSounds {
        void load(Map<ResourceLocation, JsonElement> folder) {
            this.apply(folder, (ResourceManager) null, (ProfilerFiller) null);
        }
    }

    private static final class ExposedMoeTextures extends MoeTextures {
        void load(Map<ResourceLocation, JsonElement> folder) {
            this.apply(folder, (ResourceManager) null, (ProfilerFiller) null);
        }
    }
}
