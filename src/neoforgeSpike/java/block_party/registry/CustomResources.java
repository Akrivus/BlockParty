package block_party.registry;

import block_party.BlockParty;
import block_party.registry.resources.CountingJsonReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public final class CustomResources {
    public static final CountingJsonReloadListener MOE_ALIASES = new CountingJsonReloadListener("moes/aliases");
    public static final CountingJsonReloadListener MOE_NAMES = new CountingJsonReloadListener("moes/names");
    public static final CountingJsonReloadListener SCENES = new CountingJsonReloadListener("scenes");

    private CustomResources() {
    }

    public static void registerServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(BlockParty.source("moe_aliases"), MOE_ALIASES);
        event.addListener(BlockParty.source("moe_names"), MOE_NAMES);
        event.addListener(BlockParty.source("scenes"), SCENES);
    }
}
