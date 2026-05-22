package block_party.gametest;

import block_party.BlockParty;
import block_party.registry.resources.CountingJsonReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
        assertInTag(helper, BuiltInRegistries.ITEM, Registries.ITEM, "sakura_log", "sakura_logs");
        assertInTag(helper, BuiltInRegistries.ENTITY_TYPE, Registries.ENTITY_TYPE, "moe", "spike_registered");
        assertInTag(helper, BuiltInRegistries.SOUND_EVENT, Registries.SOUND_EVENT, "moe.laugh", "spike_registered");
        assertInTag(helper, BuiltInRegistries.PARTICLE_TYPE, Registries.PARTICLE_TYPE, "sakura", "spike_registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void bundledJsonReloadListenersParseResources(GameTestHelper helper) {
        assertLoaded(helper, "moes/aliases");
        assertLoaded(helper, "moes/names");
        assertLoaded(helper, "scenes");
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
}
