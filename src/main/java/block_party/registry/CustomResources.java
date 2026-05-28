package block_party.registry;

import block_party.BlockParty;
import block_party.registry.resources.BlockAliasesReloadListener;
import block_party.registry.resources.CountingJsonReloadListener;
import block_party.registry.resources.MoeNamesReloadListener;
import block_party.registry.resources.MoeItemPreferenceReloadListener;
import block_party.registry.resources.MoeSoundsReloadListener;
import block_party.registry.resources.MoeTextureReloadListener;
import block_party.registry.resources.ScenesReloadListener;
import block_party.registry.resources.SocialAffinityReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public final class CustomResources {
    public static final CountingJsonReloadListener MOE_ALIASES = new CountingJsonReloadListener("moes/aliases");
    public static final BlockAliasesReloadListener BLOCK_ALIASES = new BlockAliasesReloadListener();
    public static final CountingJsonReloadListener MOE_NAMES = new CountingJsonReloadListener("moes/names");
    public static final MoeNamesReloadListener MOE_NAME_VALUES = new MoeNamesReloadListener();
    public static final CountingJsonReloadListener MOE_SOUND_RESOURCES = new CountingJsonReloadListener("moes/sounds");
    public static final MoeSoundsReloadListener MOE_SOUNDS = new MoeSoundsReloadListener();
    public static final MoeTextureReloadListener MOE_TEXTURES = new MoeTextureReloadListener();
    public static final CountingJsonReloadListener MOE_SOCIAL_AFFINITY_RESOURCES = new CountingJsonReloadListener("moes/social_affinities");
    public static final SocialAffinityReloadListener MOE_SOCIAL_AFFINITIES = new SocialAffinityReloadListener();
    public static final CountingJsonReloadListener MOE_ITEM_PREFERENCE_RESOURCES = new CountingJsonReloadListener("moes/item_preferences");
    public static final MoeItemPreferenceReloadListener MOE_ITEM_PREFERENCES = new MoeItemPreferenceReloadListener();
    public static final CountingJsonReloadListener SCENE_RESOURCES = new CountingJsonReloadListener("scenes");
    public static final ScenesReloadListener SCENES = new ScenesReloadListener();

    private CustomResources() {
    }

    public static void registerServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(BlockParty.source("moe_aliases"), MOE_ALIASES);
        event.addListener(BlockParty.source("block_aliases"), BLOCK_ALIASES);
        event.addListener(BlockParty.source("moe_names"), MOE_NAMES);
        event.addListener(BlockParty.source("moe_name_values"), MOE_NAME_VALUES);
        event.addListener(BlockParty.source("moe_sound_resources"), MOE_SOUND_RESOURCES);
        event.addListener(BlockParty.source("moe_sounds"), MOE_SOUNDS);
        event.addListener(BlockParty.source("moe_social_affinity_resources"), MOE_SOCIAL_AFFINITY_RESOURCES);
        event.addListener(BlockParty.source("moe_social_affinities"), MOE_SOCIAL_AFFINITIES);
        event.addListener(BlockParty.source("moe_item_preference_resources"), MOE_ITEM_PREFERENCE_RESOURCES);
        event.addListener(BlockParty.source("moe_item_preferences"), MOE_ITEM_PREFERENCES);
        event.addListener(BlockParty.source("scene_resources"), SCENE_RESOURCES);
        event.addListener(BlockParty.source("scenes"), SCENES);
    }
}
