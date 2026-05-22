package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.resources.BlockAliasesReloadListener;
import block_party.registry.resources.CountingJsonReloadListener;
import block_party.registry.resources.MoeNamesReloadListener;
import block_party.registry.resources.MoeSoundsReloadListener;
import block_party.registry.resources.MoeTextureReloadListener;
import block_party.registry.resources.MoeTextures;
import block_party.registry.resources.ScenesReloadListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class ResourceGameTests {
    private ResourceGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void representativeBundledTagsResolve(GameTestHelper helper) {
        assertInTag(helper, BuiltInRegistries.BLOCK, Registries.BLOCK, "sakura_log", "sakura_logs");
        assertVanillaInBlockTag(helper, "emerald_block", "moe/has_male_pronouns");
        assertVanillaInBlockTag(helper, "chest", "moe/has_festive_textures");
        assertVanillaInBlockTag(helper, "glowstone", "moe/has_wings");
        assertVanillaInBlockTag(helper, "redstone_lamp", "moe/has_glow");
        assertVanillaInBlockTag(helper, "andesite", "moe/has_cat_features");
        assertVanillaInBlockTag(helper, "bamboo", "moe/ignores_volume");
        assertInTag(helper, BuiltInRegistries.ITEM, Registries.ITEM, "sakura_log", "sakura_logs");
        assertInTag(helper, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE, "moe", "spike_registered");
        assertInTag(helper, BuiltInRegistries.SOUND_EVENT, Registries.SOUND_EVENT, "moe.laugh", "spike_registered");
        assertInTag(helper, BuiltInRegistries.PARTICLE_TYPE, Registries.PARTICLE_TYPE, "sakura", "spike_registered");
        helper.succeed();
    }

    private static void assertVanillaInBlockTag(GameTestHelper helper, String entryPath, String tagPath) {
        ResourceLocation entryId = ResourceLocation.withDefaultNamespace(entryPath);
        Holder.Reference<net.minecraft.world.level.block.Block> holder = BuiltInRegistries.BLOCK
                .get(ResourceKey.create(Registries.BLOCK, entryId))
                .orElseThrow(() -> new IllegalStateException("Missing registry entry " + entryId));
        TagKey<net.minecraft.world.level.block.Block> tag = TagKey.create(Registries.BLOCK, BlockParty.source(tagPath));
        if (!holder.is(tag)) {
            helper.fail("Expected " + entryId + " in tag " + tag.location());
        }
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void bundledJsonReloadListenersParseResources(GameTestHelper helper) {
        assertLoaded(helper, "moes/aliases");
        if (BlockAliasesReloadListener.size() <= 0) {
            helper.fail("Expected block alias reload listener to build at least one alias");
            return;
        }
        assertLoaded(helper, "moes/names");
        if (MoeNamesReloadListener.totalNameCount() <= 0 || !MoeNamesReloadListener.names("female").contains("Akemi")) {
            helper.fail("Expected bundled Moe names to load");
            return;
        }
        if (!Blocks.STONE.defaultBlockState().equals(BlockAliasesReloadListener.resolve(Blocks.SMOOTH_STONE.defaultBlockState()))) {
            helper.fail("Expected bundled smooth_stone alias to resolve to stone");
            return;
        }
        assertLoaded(helper, "moes/sounds");
        if (MoeSoundsReloadListener.size() <= 0) {
            helper.fail("Expected Moe sound override reload listener to build at least one override");
            return;
        }
        SoundEvent bellStep = MoeSoundsReloadListener.get(Blocks.BELL, "step");
        ResourceLocation bellStepId = BuiltInRegistries.SOUND_EVENT.getKey(bellStep);
        if (!BlockParty.source("moe.bell.step").equals(bellStepId)) {
            helper.fail("Expected bell Moe step sound override to resolve to block_party:moe.bell.step, got " + bellStepId);
            return;
        }
        assertLoaded(helper, "scenes");
        if (ScenesReloadListener.loadedCount() <= 0) {
            helper.fail("Expected scene reload listener to parse at least one scene");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void representativeClientAssetsAreAvailable(GameTestHelper helper) {
        assertResource(helper, "items/moe_spawn_egg.json");
        assertResource(helper, "items/cell_phone.json");
        assertResource(helper, "items/yearbook.json");
        assertResource(helper, "items/sakura_log.json");
        assertResource(helper, "items/sakura_blossoms.json");
        assertResource(helper, "items/wisteria_vines.json");
        assertResource(helper, "models/item/moe_spawn_egg.json");
        assertResource(helper, "models/item/cell_phone.json");
        assertResource(helper, "models/item/yearbook.json");
        assertResource(helper, "textures/item/moe_spawn_egg.png");
        assertResource(helper, "textures/item/cell_phone.png");
        assertResource(helper, "textures/item/yearbook.png");
        assertResource(helper, "blockstates/sakura_blossoms.json");
        assertResource(helper, "models/block/sakura_blossoms.json");
        assertResource(helper, "textures/block/sakura_blossoms.png");
        assertResource(helper, "blockstates/shoji_screen.json");
        assertResource(helper, "models/block/shoji_screen_bottom.json");
        assertResource(helper, "models/block/shoji_screen_top.json");
        assertResource(helper, "textures/block/shoji_screen_bottom.png");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeTextureMetadataParsesAndMatchesProperties(GameTestHelper helper) {
        JsonObject json = JsonParser.parseString("""
                {
                  "texture": "minecraft:textures/moe/note_block.note5",
                  "block": "minecraft:note_block",
                  "props": [
                    { "name": "note", "value": 5 }
                  ]
                }
                """).getAsJsonObject();
        Optional<MoeTextures.Override> parsed = MoeTextureReloadListener.safeParseOverride(json);
        if (parsed.isEmpty()) {
            helper.fail("Expected texture override metadata to parse");
            return;
        }
        ResourceLocation expected = ResourceLocation.withDefaultNamespace("textures/moe/note_block.note5");
        if (!expected.equals(parsed.get().texture())) {
            helper.fail("Expected texture override " + expected + ", got " + parsed.get().texture());
            return;
        }
        if (!parsed.get().matches(Blocks.NOTE_BLOCK.defaultBlockState().setValue(NoteBlock.NOTE, 5))) {
            helper.fail("Expected texture override to match note_block note 5");
            return;
        }
        if (parsed.get().matches(Blocks.NOTE_BLOCK.defaultBlockState().setValue(NoteBlock.NOTE, 4))) {
            helper.fail("Expected texture override not to match note_block note 4");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void resourceOverrideShapeParsesForNamesAliasesTexturesAndSounds(GameTestHelper helper) {
        List<String> names = MoeNamesReloadListener.safeParseNames(JsonParser.parseString("{\"names\":[\"OverrideName\"]}").getAsJsonObject());
        if (!List.of("OverrideName").equals(names)) {
            helper.fail("Expected override-style name resource to parse");
            return;
        }

        Map<Block, Block> aliases = BlockAliasesReloadListener.parseAlias(
                ResourceLocation.withDefaultNamespace("stone"),
                JsonParser.parseString("{\"aliases\":[\"minecraft:dirt\"]}").getAsJsonObject());
        if (aliases.get(Blocks.DIRT) != Blocks.STONE) {
            helper.fail("Expected override-style alias resource to map dirt to stone");
            return;
        }

        Optional<MoeTextures.Override> texture = MoeTextureReloadListener.safeParseOverride(JsonParser.parseString("""
                {"texture":"minecraft:textures/moe/cake.bites3","block":"minecraft:cake","props":[{"name":"bites","value":3}]}
                """).getAsJsonObject());
        if (texture.isEmpty() || !ResourceLocation.withDefaultNamespace("textures/moe/cake.bites3").equals(texture.get().texture())) {
            helper.fail("Expected override-style texture metadata to parse");
            return;
        }

        Map<String, SoundEvent> sounds = MoeSoundsReloadListener.parseSounds(JsonParser.parseString("{\"step\":\"block_party:moe/bell/step\"}").getAsJsonObject());
        ResourceLocation soundId = BuiltInRegistries.SOUND_EVENT.getKey(sounds.get("step"));
        if (!BlockParty.source("moe.bell.step").equals(soundId)) {
            helper.fail("Expected slash-form Moe sound override to resolve to dotted registered ID, got " + soundId);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void malformedOptionalMoeMetadataFailsClosed(GameTestHelper helper) {
        if (!MoeNamesReloadListener.safeParseNames(JsonParser.parseString("{\"not_names\":[]}").getAsJsonObject()).isEmpty()) {
            helper.fail("Expected malformed names metadata to fail closed");
            return;
        }
        if (MoeTextureReloadListener.safeParseOverride(JsonParser.parseString("{\"block\":\"minecraft:missingno\"}").getAsJsonObject()).isPresent()) {
            helper.fail("Expected malformed texture metadata to fail closed");
            return;
        }
        if (MoeTextureReloadListener.safeParseOverride(JsonParser.parseString("""
                {"texture":"minecraft:textures/moe/cake.bad","block":"minecraft:cake","props":[{"name":"missing","value":3}]}
                """).getAsJsonObject()).isPresent()) {
            helper.fail("Expected unknown texture metadata properties to fail closed");
            return;
        }
        if (MoeTextureReloadListener.safeParseOverride(JsonParser.parseString("""
                {"texture":"minecraft:textures/moe/cake.bad","block":"minecraft:cake","props":[{"name":"bites","value":"not_a_bite"}]}
                """).getAsJsonObject()).isPresent()) {
            helper.fail("Expected invalid texture metadata property values to fail closed");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void moeTextureFallbackUsesVisibleBlockId(GameTestHelper helper) {
        ResourceLocation texture = MoeTextures.getDefaultPathFor(Blocks.BARREL.defaultBlockState());
        ResourceLocation expected = ResourceLocation.withDefaultNamespace("textures/moe/barrel.png");
        if (!expected.equals(texture)) {
            helper.fail("Expected Moe fallback texture " + expected + ", got " + texture);
            return;
        }
        helper.succeed();
    }

    private static <T> void assertInTag(
            GameTestHelper helper,
            Registry<T> registry,
            ResourceKey<? extends Registry<T>> registryKey,
            String entryPath,
            String tagPath) {
        ResourceLocation entryId = BlockParty.source(entryPath);
        Holder.Reference<T> holder = registry.get(ResourceKey.create(registryKey, entryId))
                .orElseThrow(() -> new IllegalStateException("Missing registry entry " + entryId));
        TagKey<T> tag = TagKey.create(registryKey, BlockParty.source(tagPath));
        if (!holder.is(tag)) {
            helper.fail("Expected " + entryId + " in tag " + tag.location());
        }
    }

    private static void assertLoaded(GameTestHelper helper, String directory) {
        int count = CountingJsonReloadListener.loadedCount(directory);
        if (count <= 0) {
            helper.fail("Expected bundled JSON resources in " + directory + " to load, got " + count);
        }
    }

    private static void assertResource(GameTestHelper helper, String path) {
        String classpathPath = "assets/block_party/" + path;
        if (ResourceGameTests.class.getClassLoader().getResource(classpathPath) == null) {
            helper.fail("Expected bundled client resource " + BlockParty.source(path));
        }
    }
}
